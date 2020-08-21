package cn.meiot.controller;

import cn.meiot.aop.Log;
import cn.meiot.aop.UpgradeDetection;
import cn.meiot.config.MSG;
import cn.meiot.entity.bo.UserInfoBo;
import cn.meiot.entity.vo.DeviceVersionVo;
import cn.meiot.entity.vo.ProgressBarVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.UpgradeVo;
import cn.meiot.enums.AccountType;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IEquipmentUserService;
import cn.meiot.service.IFilesService;
import cn.meiot.utils.MyUUID;
import cn.meiot.utils.NetworkingUtlis;
import cn.meiot.utils.RabbitMqUtil;
import cn.meiot.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Package cn.meiot.controller
 * @Description:
 * @author: 武有
 * @date: 2019/11/27 15:07
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("Personal")
@Slf4j
@SuppressWarnings("all")
public class PersonalController extends BaseBaseController {

    @Autowired
    private IEquipmentUserService equipmentUserService;
    @Autowired
    private IFilesService filesService;
    @Autowired
    private UserFeign userFeign;

    @Autowired
    private NetworkingUtlis networkingUtlis;


    @GetMapping("getUpgradePackage")
    public Result getUpgradePackage() {
        Long userId = getUserId();
        UserInfoBo user = RedisUtil.getUser(userId);
        if (user.getType() != AccountType.PERSONAGE.value()) {
            Result result = Result.getDefaultFalse();
            result.setMsg("违规操作");
            result.setData(new ArrayList<>());
            return result;
        }
        List<UpgradeVo> upgradeListRedis = RedisUtil.getUpgradeListRedis(userId, 0);
        if (null == upgradeListRedis || upgradeListRedis.size() <= 0) {
            upgradeListRedis = equipmentUserService.getUpgradeAndDevice(userId, 0);
        }
        if (null == upgradeListRedis || upgradeListRedis.size() <= 0) {
            Result result = Result.getDefaultTrue();
            result.setMsg("没有检测出需要升级的设备");
            result.setData(new ArrayList<>());
            return result;
        }
        for (UpgradeVo b : upgradeListRedis) {
            List<DeviceVersionVo> list = b.getList();
            List list1 = new ArrayList();
            for (DeviceVersionVo deviceVersionVo : list) {
                ProgressBarVo uploadVo = RedisUtil.getUploadVo(userId, 0, deviceVersionVo.getSerialNumber());
                if (null == uploadVo) {
                    deviceVersionVo.setLength(0L);
                    deviceVersionVo.setCurrentLength(0L);
                    deviceVersionVo.setStatus(0);
                } else {
                    deviceVersionVo.setLength(uploadVo.getLength());
                    deviceVersionVo.setCurrentLength(uploadVo.getCurrentLength());
                    deviceVersionVo.setStatus(uploadVo.getStatus());
                }
                list1.add(deviceVersionVo);
            }
            b.setList(list1);
        }
        Result result = Result.getDefaultTrue();
        result.setData(upgradeListRedis);
        RedisUtil.clean(userId, 0);
        return result;
    }

    @GetMapping("ConfirmUpgrade")
    @UpgradeDetection("设备升级中 请勿操作")
    public Result confirmUpgrade() {
        Long userId = getUserId();
        Integer projectId = 0;
        List<UpgradeVo> upgradeListRedis = equipmentUserService.getUpgradeAndDevice(userId, 0);
        if (null == upgradeListRedis || upgradeListRedis.size() <= 0) {
            Result result = Result.getDefaultTrue();
            result.setMsg("没有可以升级的设备");
            return result;
        }
        List<ProgressBarVo> uploadVos = RedisUtil.getUploadVos(userId, 0);
        if (null != uploadVos) {
            Result result = Result.getDefaultTrue();
            result.setMsg("设备已经在升级中 请勿重复提交");
            return result;
        }
        RedisUtil.setUpgradeListRedis(userId, projectId, upgradeListRedis);
        log.info("此次升级的设备有：{}；申请人ID：{}", upgradeListRedis, userId);
        for (UpgradeVo b : upgradeListRedis) {
            List<DeviceVersionVo> list = b.getList();
            for (DeviceVersionVo d : list) {
                String serialNumber = d.getSerialNumber();
                Integer status = networkingUtlis.getNetworkingStatus(null, serialNumber);
                //如果当前设备不在线，直接发送失败信息
                log.info("设备在线状态：设备号：{} 状态：{}", serialNumber, status == 0 ? "离线" : "在线");
                if (0 == status) {
                    RedisUtil.setUploadVo(userId, projectId, serialNumber, new ProgressBarVo(3, serialNumber, 0L, 0L));
                    List detectionUploadVos = RedisUtil.getUploadVos(userId, projectId);
                    log.info("==>>>>>>:{}", uploadVos);
                    RabbitMqUtil.sendMsg(MSG.UPGRADE, RedisUtil.getToken(userId), detectionUploadVos);
                } else {
                    RedisUtil.setUploadVo(userId, projectId, serialNumber, new ProgressBarVo(1, serialNumber, 0L, 0L));
                }

                /**
                 * 告诉硬件更新设备
                 */
                RabbitMqUtil.sendMsg(serialNumber, b.getVersion());
                log.info("已经发送更新：{}", serialNumber);
                /**
                 * 发送死信设备
                 */
                String uuid = MyUUID.getUUID();
                Map map = new HashMap();
                map.put("serialNumber", serialNumber);
                map.put("uuid", uuid);
                RabbitMqUtil.sendMsg(JSON.toJSONString(map));
                RedisUtil.setFlag(serialNumber, uuid);
            }
        }
        return Result.getDefaultTrue();
    }


    @GetMapping("clean")
    public Result clean() {
        Integer projectId = 0;
        RedisUtil.clean(getUserId(), projectId);
        return Result.getDefaultTrue();
    }


    /**
     * 检测是否有设备升级
     */
    @GetMapping("detectionUpgrade")
    public Result detectionUpgrade() {
        List<UpgradeVo> upgradeListRedis = equipmentUserService.getUpgradeAndDevice(getUserId(), 0);
        Result result = Result.getDefaultTrue();

        if (null == upgradeListRedis || upgradeListRedis.size() <= 0) {
            result.setData(-1);
            return result;
        }

        for (UpgradeVo u : upgradeListRedis) {
            List<DeviceVersionVo> list = u.getList();
            if (null == list || list.size() <= 0) {
                result.setData(-1);
                return result;
            }
        }
        result.setData(0);
        return result;
    }
}
