package cn.meiot.controller;

import cn.meiot.config.MSG;
import cn.meiot.entity.vo.DeviceVersionVo;
import cn.meiot.entity.vo.ProgressBarVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.UpgradeVo;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IEquipmentUserService;
import cn.meiot.service.IFilesService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.RabbitMqUtil;
import cn.meiot.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Package cn.meiot.controller
 * @Description:
 * @author: 武有
 * @date: 2019/12/2 12:27
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("nofilter")
@SuppressWarnings("all")
@Slf4j
public class TestController {
    @Value("${upgrade.ip}")
    private String ip;
    @Value("${upgrade.port}")
    private Integer port;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private IEquipmentUserService equipmentUserService;
    @Autowired
    private IFilesService filesService;
    @Autowired
    private UserFeign userFeign;

    @GetMapping("sendUpgrade")
    public Result sendUpgrade(@RequestParam("serialNumber") String serialNumber,
                              @RequestParam("version") String version) {
        Result result = Result.getDefaultTrue();
        result.setMsg("ok");
        Map<String, Object> map = RabbitMqUtil.sendMsg(serialNumber, version);
        result.setData(map);
        return result;
    }

    @GetMapping("sendMsg/{userId}")
    public void sendMsg(@PathVariable("userId") Long userId) {
        List uploadVos = RedisUtil.getUploadVos(userId, 0);
        RabbitMqUtil.sendMsg(ConstantsUtil.SocketType.FIRMWARE_UPGRADE, RedisUtil.getToken(userId), uploadVos);
    }

    @GetMapping("sendMsg/{userId}/{projectId}")
    public Result sendMsg2(@PathVariable("userId") Long userId,
                           @PathVariable("projectId") Integer projectId) {
        Long mainUserId = userFeign.getMainUserIdByUserId(userId);
        List<UpgradeVo> upgradeListRedis = RedisUtil.getUpgradeListRedis(userId, projectId);
        if (null == upgradeListRedis) {
            upgradeListRedis = equipmentUserService.getUpgradeAndDevice(userId, projectId);
            if (null == upgradeListRedis || upgradeListRedis.size() <= 0) {
                Result result = Result.getDefaultFalse();
                result.setMsg("没有检测出需要升级的设备");
                return result;
            }
        }

//        List<ProgressBarVo> uploadVos = RedisUtil.getUploadVos(userId, projectId);
//        if (null != uploadVos) {
//            Result result = Result.getDefaultTrue();
//            result.setMsg("设备已经在升级中 请勿重复提交");
//            return result;
//        }
        for (UpgradeVo b : upgradeListRedis) {
            List<DeviceVersionVo> list = b.getList();
            for (DeviceVersionVo d : list) {
                String serialNumber = d.getSerialNumber();
                RedisUtil.setUploadVo(userId, projectId, serialNumber, new ProgressBarVo(1, serialNumber, 0L, 0L));
                /**
                 * 告诉硬件更新设备
                 */
                RabbitMqUtil.sendMsg(serialNumber, b.getVersion());
                log.info("已经发送更新：{}", serialNumber);
                /**
                 * 发送死信设备
                 */
                RabbitMqUtil.sendMsg(serialNumber);
            }
        }
        List<ProgressBarVo> uploadV = RedisUtil.getUploadVos(userId, projectId);
        List list = new ArrayList();
        for (ProgressBarVo p : uploadV) {
            p.setStatus(1);
            p.setLength(100000L);
            Random ra = new Random();
            p.setCurrentLength(Long.valueOf(ra.nextInt(100000) + 1));
            list.add(p);
        }
        RabbitMqUtil.sendMsg(MSG.UPGRADE, RedisUtil.getToken(userId), list);
        return Result.getDefaultTrue();
    }

    @GetMapping("testSendMsg/{no}")
    public void test(@PathVariable("no") String s) {
        RabbitMqUtil.sendMsg(s);
    }

    @GetMapping("setVersion/{version}")
    public Result setVersion(@PathVariable("version") String version) {
        if (null == version || version.length() <= 2) {
            RedisUtil.cleanKey("test_version");
            Result result = Result.getDefaultTrue();
            result.setMsg("清除最新版本成功");
            return result;
        }
        String version1 = RedisUtil.getVersion();
        RedisUtil.setVersion(version);
        Map map = new HashMap();
        map.put("currentVersion", version);
        map.put("priorToVersion", version1);
        Result result = Result.getDefaultTrue();
        result.setData(map);
        result.setMsg("设置最新版本成功");
        return result;
    }
}
