package cn.meiot.mq;

import cn.meiot.config.MSG;
import cn.meiot.config.MqConfig;
import cn.meiot.entity.EquipmentUser;
import cn.meiot.entity.vo.*;
import cn.meiot.service.IEquipmentUserService;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.RabbitMqUtil;
import cn.meiot.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Package cn.meiot.mq
 * @Description:
 * @author: 武有
 * @date: 2019/11/29 17:27
 * @Copyright: www.spacecg.cn
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class Mq {

    @Autowired
    private IEquipmentUserService equipmentUserService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = QueueConstantUtil.PROGRESS_BAR)
    public void progressBar(UpVo upVo) {
        log.info("进度条收到消息：{}",upVo);
        try {
            EquipmentUser equipmentUser = null;
            Long userId = RedisUtil.getUserIdBySerialNumber(upVo.getSerialNumber());
            Integer projectId = RedisUtil.getProjectIdBySerialNumber(upVo.getSerialNumber());
            if (null == userId) {
                equipmentUser = equipmentUserService.getUserIdAndProjectIdBySerialNumber(upVo.getSerialNumber());
                RedisUtil.setUserIdBySerialNumber(upVo.getSerialNumber(), equipmentUser.getUserId());
                userId = equipmentUser.getUserId();
            }
            if (null == projectId) {
                equipmentUser = equipmentUserService.getUserIdAndProjectIdBySerialNumber(upVo.getSerialNumber());
                RedisUtil.setProjectIdBySerialNumber(upVo.getSerialNumber(), equipmentUser.getProjectId());
                projectId = equipmentUser.getProjectId();
            }
            Long currentLength = upVo.getUploadVo().getSkip();
            ProgressBarVo progressBarVo = new ProgressBarVo(1, upVo.getSerialNumber(), currentLength, upVo.getUploadVo().getFile());
            RedisUtil.setUploadVo(userId, projectId, upVo.getSerialNumber(), progressBarVo);
            //通过设备好获取用户ID与用户项目 放在redis里
            List uploadVos = RedisUtil.getUploadVos(userId, projectId);
            log.info("==>>>>>>:{}", uploadVos);
            RabbitMqUtil.sendMsg(MSG.UPGRADE, RedisUtil.getToken(userId), uploadVos);
            log.info("进度条消息已经发送");
        } catch (Exception e) {
            log.info("进度条异常：{}",e);
            e.printStackTrace();
        }

    }

    @RabbitListener(queues = MqConfig.DEVICE_101)
    public void showMessage101(String message) {
        Map parseObject = JSON.parseObject(message, Map.class);
        Map map = (Map) parseObject.get("payload");
        String serialNumber = (String) map.get("deviceid");
        JSONObject content = (JSONObject) map.get("desired");
        //版本号存在
        try {
            String version = (String) content.get("firmware");
            if (StringUtils.isNotEmpty(version)) {
                Long userId = RedisUtil.getUserIdBySerialNumber(serialNumber);
                Integer projectId = RedisUtil.getProjectIdBySerialNumber(serialNumber);
                EquipmentUser equipmentUser = null;
                if (null == userId) {
                    equipmentUser = equipmentUserService.getUserIdAndProjectIdBySerialNumber(serialNumber);
                    userId = equipmentUser.getUserId();
                }
                if (null == projectId) {
                    equipmentUser = equipmentUserService.getUserIdAndProjectIdBySerialNumber(serialNumber);
                    projectId = equipmentUser.getProjectId();
                }

                ProgressBarVo uploadVo = RedisUtil.getUploadVo(userId, projectId, serialNumber);
                if (null == uploadVo) {
                    log.info("没有查询到此设备在更新：{}", serialNumber);
                    return;
                }
                List<UpgradeVo> upgradeVos = RedisUtil.getUpgradeListRedis(userId, projectId);
                for (UpgradeVo u : upgradeVos) {

                    List<DeviceVersionVo> list = u.getList();
                    for (DeviceVersionVo j : list) {
                        String s = j.getSerialNumber();
                        log.info("~~~~~~~~~上传设备号:{},自己的设备号：{}", serialNumber, s);
                        if (s.equals(serialNumber)) {
                            log.info("===>>上传的版本号：{},===>>>当前最新的版本号：{}", version, u.getVersion());
                            if (version.equals(u.getVersion())) {
                                uploadVo.setStatus(2);
                            } else {
                                uploadVo.setStatus(3);
                            }
                            RedisUtil.setUploadVo(userId, projectId, serialNumber, uploadVo);
                            RabbitMqUtil.sendMsg(MSG.UPGRADE, RedisUtil.getToken(userId), RedisUtil.getUploadVos(userId, projectId));
                        }
                    }
                }
                System.out.println("````````````:" + message);
            }
        } catch (Exception e) {
            log.info("*****************"+e+"****************************");
            log.info("**********************比较版本号异常:{}", message);
        }
    }

    @RabbitListener(queues = {MqConfig.UPGRADE_QUEUE_NAME})
    public void orderDelayQueue(String serialNumber) {
        try {
            log.info("【延时队列】{}",serialNumber);
            Map map = JSON.parseObject(serialNumber, Map.class);
            map.get("serialNumber");
            serialNumber= (String) map.get("serialNumber");
            String uuid= (String) map.get("uuid");
            String flag = RedisUtil.getFlag(serialNumber);
            log.info("【延时队列】   {}",flag);
            if (null == uuid || null == flag) {
                log.info("延时队列重要参数丢失");
                return;
            }
            if (!uuid.equals(flag)){
                log.info("延时队列过期数据");
                return;
            }
            log.info("###########################################");
            Long userId = RedisUtil.getUserIdBySerialNumber(serialNumber);
            Integer projectId = RedisUtil.getProjectIdBySerialNumber(serialNumber);
            EquipmentUser equipmentUser = null;
            if (null == userId) {
                equipmentUser = equipmentUserService.getUserIdAndProjectIdBySerialNumber(serialNumber);
                userId = equipmentUser.getUserId();
            }
            if (null == projectId) {
                equipmentUser = equipmentUserService.getUserIdAndProjectIdBySerialNumber(serialNumber);
                projectId = equipmentUser.getProjectId();
            }
            List<UpgradeVo> upgradeVos = RedisUtil.getUpgradeListRedis(userId, projectId);
            ProgressBarVo uploadVo = RedisUtil.getUploadVo(userId, projectId, serialNumber);
            if (null == uploadVo) {
                log.info("没有查询到此设备在更新：{}", serialNumber);
                return;
            }
            if (uploadVo.getStatus()==3){
                log.info("当前设备升级已经是升级失败状态，不发消息通知失败");
                return;
            }
            if (uploadVo.getStatus() != 2 && uploadVo.getStatus()!=3) {
                uploadVo.setStatus(3);
            }


            RedisUtil.setUploadVo(userId, projectId, serialNumber, uploadVo);
            RabbitMqUtil.sendMsg(MSG.UPGRADE, RedisUtil.getToken(userId), RedisUtil.getUploadVos(userId, projectId));
            log.info("###########################################");
        }catch (Exception e){

        }

    }

//    private synchronized void  clean(Long userId,Integer projectId){
//
//        List<ProgressBarVo> uploadVos = RedisUtil.getUploadVos(userId, projectId);
//        int i=0;
//        for (ProgressBarVo vo :uploadVos) {
//            if (vo.getStatus()==2 || vo.getStatus()==3){
//                i++;
//            }
//        }
//        if (i==uploadVos.size()){
//            RedisUtil.cleanUpgradeListRedis(userId, projectId);
//        }
//    }


}
