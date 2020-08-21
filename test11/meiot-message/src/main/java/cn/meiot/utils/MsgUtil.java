package cn.meiot.utils;

import cn.meiot.entity.FaultMessage;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.MqVo;
import cn.meiot.enums.FaultMsgContentEnum;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.jg.JPushClientExample;
import cn.meiot.utils.enums.JpushTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/10/16 14:20
 * @Copyright: www.spacecg.cn
 */
@Slf4j
@SuppressWarnings("all")
public class MsgUtil {
    public static void appAlarmPush(FaultMessage faultMessage, JPushClientExample jPushClientExample,UserInfoUtil userInfoUtil) throws Exception {
        Map<String, String> map = new HashMap<>();
        List<String> sendIds = new ArrayList<>();
       AuthUserBo authUserBo = userInfoUtil.getAuthUserBo(faultMessage.getUserId());
        if (null==authUserBo){
            log.info("该账户为登录不推送");
            return;
        }
        log.info("app推送");
            map.put("msgType", "2");
            map.put("faultType", String.valueOf(faultMessage.getSwitchEvent()));
            sendIds.add(String.valueOf(faultMessage.getUserId()));
            jPushClientExample.sendMsg(sendIds, faultMessage.getEquipmentAlias() + "发生" + FaultTitleEnum.getTitle(faultMessage.getSwitchEvent()) + ",请尽快处理", FaultTitleEnum.getTitle(faultMessage.getSwitchEvent()),
                    faultMessage.getMsgContent(), JpushTypeEnum.NOTIFICATION.value(), map, authUserBo.getUser().getType());
            log.info("app故障消息已推送 用户为：" + faultMessage.getUserId());

    }

    public static void appAlarmPush(FaultMessage faultMessage, JPushClientExample jPushClientExample,UserInfoUtil userInfoUtil,String projectName,String addressId) throws Exception {
        Map<String, String> map = new HashMap<>();
        List<String> sendIds = new ArrayList<>();
        AuthUserBo authUserBo = userInfoUtil.getAuthUserBo(faultMessage.getUserId());
        if (null==projectName || null==addressId){
            log.info("没有查询到项目名称或者没有查询到地址ID,此条推送放弃");
            return;
        }
        if (null==authUserBo){
            log.info("该账户为登录不推送");
            return;
        }
        log.info("app推送");
        map.put("msgType", "2");
        map.put("faultType", String.valueOf(faultMessage.getSwitchEvent()));
        map.put("addressId",addressId);
        map.put("projctId",String.valueOf(faultMessage.getProjectId()));
        map.put("serialNumber",faultMessage.getSerialNumber());

        sendIds.add(String.valueOf(faultMessage.getUserId()));
        jPushClientExample.sendMsg(sendIds, projectName+faultMessage.getEquipmentAlias() + "发生" + FaultTitleEnum.getTitle(faultMessage.getSwitchEvent()) + ",请尽快处理", FaultTitleEnum.getTitle(faultMessage.getSwitchEvent()),
                faultMessage.getMsgContent(), JpushTypeEnum.NOTIFICATION.value(), map, authUserBo.getUser().getType());
        log.info("##################################推送的数据：{}",map);
        log.info("-------------**************////////////////··········企业故障消息已推送 用户为：" + faultMessage.getUserId());

    }

    public static synchronized FaultMessage getFaultMessage(MqVo mqVo, Map<String, String> param, Integer event) {
        String equipmentAlias = param.get("deviceAlias") == null ? mqVo.getSerialNumber() : param.get("deviceAlias");
        String switchAlias = param.get("switchAlias") == null ? String.valueOf(mqVo.getMqDeviceVos().get(0).getIndex()) : param.get("switchAlias");
        FaultMessage faultMessage = FaultMessage.builder()
                .sendTime(ConstantsUtil.DF.format(new Date()))
                .createTime(ConstantsUtil.DF.format(new Date()))
                .switchIndex(mqVo.getMqDeviceVos().get(0).getIndex())
                .isRead(0)
                .faultTime(mqVo.getTimestamp())
                .switchSn(String.valueOf(mqVo.getMqDeviceVos().get(0).getId()))
                .serialNumber(mqVo.getSerialNumber())
                .projectId(Integer.valueOf(param.get("projectId")))
                .equipmentAlias(equipmentAlias)
                .switchAlias(switchAlias)
                .switchEvent(event)
                .userId(Long.valueOf(param.get("userId")))
                .msgContent(equipmentAlias + "   " + switchAlias + FaultMsgContentEnum.getContent(event))
                .build();
        return faultMessage;
    }

    /**
     * 企业返回true app返回false
     *
     * @param projectId
     * @return
     */
    public static Boolean ifEnterprise(Integer projectId) {
        if (projectId == null) return null;
        if (projectId == 0) return false;
        if (projectId > 0) return true;
        return null;
    }


    /**
     * 获取对应的实体类
     * @param faultMessage
     * @param c
     * @return
     */
    public static <V> V getBean(FaultMessage faultMessage, DeviceFeign deviceFeign, Class c) {
        Object bean = null;
        String address=deviceFeign.getAddressBySerialNumber(faultMessage.getSerialNumber());
        try {
            bean = c.newInstance();
            BeanUtils.copyProperties(faultMessage, bean);
            Method setIsShow = c.getDeclaredMethod("setIsShow", Integer.class);
            setIsShow.invoke(bean, 0);
            Method setEvent = c.getDeclaredMethod("setEvent", Integer.class);
            setEvent.invoke(bean, faultMessage.getSwitchEvent());
            Method setSwitchStatus = c.getDeclaredMethod("setSwitchStatus", Integer.class);
            setSwitchStatus.invoke(bean, 1);
            Method setAddress = c.getDeclaredMethod("setAddress", String.class);
            setAddress.invoke(bean,address);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return (V) bean;
    }

}
