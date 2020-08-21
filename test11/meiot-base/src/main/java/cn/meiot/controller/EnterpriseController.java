package cn.meiot.controller;

import cn.meiot.aop.Log;
import cn.meiot.config.MSG;
import cn.meiot.entity.Firmware;
import cn.meiot.entity.bo.UserInfoBo;
import cn.meiot.entity.equipment.Updata;
import cn.meiot.entity.vo.DeviceVersionVo;
import cn.meiot.entity.vo.ProgressBarVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.UpgradeVo;
import cn.meiot.enums.AccountType;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IEquipmentUserService;
import cn.meiot.service.IFilesService;
import cn.meiot.service.IFirmwareService;
import cn.meiot.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @date: 2019/11/27 15:59
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("Enterprise")
@Slf4j
@SuppressWarnings("all")
public class EnterpriseController extends BaseBaseController {

    @Autowired
    private IEquipmentUserService equipmentUserService;
    @Autowired
    private IFilesService filesService;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private NetworkingUtlis networkingUtlis;

    @GetMapping("getUpgradePackage")
    @Log(operateModule = "公共服务",operateContent = "获取升级列表")
    public Result getUpgradePackage() {
        Long userId = getUserId();
        Integer projectId=getProjectId();
        UserInfoBo user = RedisUtil.getUser(userId);
        if (user.getType() != AccountType.ENTERPRISE.value()) {
            Result result = Result.getDefaultFalse();
            result.setMsg("违规操作");
            return result;
        }
        List<UpgradeVo> upgradeListRedis = RedisUtil.getUpgradeListRedis(userId, projectId);
        if (null == upgradeListRedis || upgradeListRedis.size() <= 0) {
            upgradeListRedis=equipmentUserService.getUpgradeAndDevice(userId, projectId);
        }
        if (null == upgradeListRedis || upgradeListRedis.size() <= 0) {
            Result result = Result.getDefaultTrue();
            result.setMsg("没有检测出需要升级的设备");
            return result;
        }
        for (UpgradeVo b : upgradeListRedis) {
            List<DeviceVersionVo> list = b.getList();
            List list1 = new ArrayList();
            for (DeviceVersionVo deviceVersionVo : list) {
                ProgressBarVo uploadVo = RedisUtil.getUploadVo(userId, projectId, deviceVersionVo.getSerialNumber());
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
        RedisUtil.clean(userId, projectId);
        return result;
    }

    @GetMapping("ConfirmUpgrade")
    @Log(operateModule = "公共服务",operateContent = "确认升级")
    public Result confirmUpgrade() {
        Long userId = getUserId();
        Integer projectId = getProjectId();
        List<UpgradeVo> upgradeListRedis = equipmentUserService.getUpgradeAndDevice(userId, projectId);
        if (null == upgradeListRedis || upgradeListRedis.size() <= 0) {

            return Result.faild("没有可以升级的设备");
        }
        List<ProgressBarVo> uploadVos = RedisUtil.getUploadVos(userId, projectId);
        if (null != uploadVos) {
            Result result = Result.getDefaultTrue();
            result.setMsg("设备已经在升级中 请勿重复提交");
            return result;
        }
        RedisUtil.setUpgradeListRedis(userId, projectId, upgradeListRedis);
        for (UpgradeVo b : upgradeListRedis) {
            List<DeviceVersionVo> list = b.getList();
            for (DeviceVersionVo d : list) {
                String serialNumber = d.getSerialNumber();
                Integer status = networkingUtlis.getNetworkingStatus(null, serialNumber);
                log.info("设备在线状态：设备号：{} 状态：{}",serialNumber,status==0?"离线":"在线");
                //如果当前设备不在线，直接发送失败信息
                if (0==status){
                    RedisUtil.setUploadVo(userId, projectId, serialNumber, new ProgressBarVo(3, serialNumber, 0L, 0L));
                    List detectionUploadVos = RedisUtil.getUploadVos(userId, projectId);
                    log.info("==>>>>>>:{}", uploadVos);
                    RabbitMqUtil.sendMsg(MSG.UPGRADE, RedisUtil.getToken(userId), detectionUploadVos);
                }else{
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
                Map map=new HashMap();
                map.put("serialNumber",serialNumber);
                map.put("uuid",uuid);
                RabbitMqUtil.sendMsg(JSON.toJSONString(map));
                RedisUtil.setFlag(serialNumber,uuid);
            }
        }
        if (upgradeListRedis.isEmpty()) {
            return Result.faild("没有可以升级的设备");
        }
        return Result.getDefaultTrue();
    }

    @GetMapping("clean")
    public Result clean(){
        Integer projectId=getProjectId();
        Long mainUserId = userFeign.getMainUserIdByUserId(getUserId());
        RedisUtil.clean(mainUserId,projectId);
        return Result.getDefaultTrue();
    }

    @GetMapping("continueToUpgrade")
    @Log(operateModule = "公共服务",operateContent = "继续升级")
    public Result continueToUpgrade(){
        Result result = getUpgradePackage();
        confirmUpgrade();
        return result;
    }
}
