package cn.meiot.receive;

import cn.meiot.entity.SystemMessage;
import cn.meiot.entity.message.DeviceMessage;
import cn.meiot.entity.vo.DeviceSwitchVo;
import cn.meiot.enums.AccountType;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.jg.JPushClientExample;
import cn.meiot.service.ISystemMessageService;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.RedisUtils;
import cn.meiot.utils.enums.JpushTypeEnum;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Package cn.meiot.receive
 * @Description:
 * @author: 武有
 * @date: 2020/5/7 10:16
 * @Copyright: www.spacecg.cn
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class DeviceMassageMq {
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private DeviceFeign deviceFeign;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ISystemMessageService systemMessageService;
    @Autowired
    private JPushClientExample jPushClientExample;

    /**
     * 开关控制队列
     * @param deviceMessage
     */
    @RabbitListener(queues = QueueConstantUtil.ProjectMessage.SWITCH_CONTROL)
    public void deviceSwitch(DeviceMessage deviceMessage) {
        log.info("接受到开关控制信息，信息内容：{}", deviceMessage);
        try {
            //操作人ID
            Long userId = deviceMessage.getUserId();
            Integer projectId = deviceMessage.getProjectId();
            //设备号
            String serialNumber = (String) deviceMessage.getMap().get("serialNumber");
            //状态 0关 1开
            Integer status = (Integer) deviceMessage.getMap().get("status");
            //开关IDs
            List<String> switchNmbers = (List<String>) deviceMessage.getMap().get("switchList");
            //主账号ID
            Long mainUserId = deviceMessage.getMainUserId();
            // 编辑人
            String name = userFeign.getNiknameByUserId(userId);
            //角色名称
            String roleName = userFeign.getRoleNameByUserId(userId).get(0);
            // 内容
            String content = "控制设备开关-" + getOperating(status);
            //设备名称
            String deviceName = redisUtils.getDeviceName(mainUserId, serialNumber);
            //设备位置
            String address = deviceFeign.getAddressBySerialNumber(serialNumber);
            // 线路名称
            List<Object> switchName = new ArrayList<>();
            for (String switchNmber : switchNmbers) {
                String switchAlias = redisUtils.getSwitchAlias(mainUserId, switchNmber);
                switchName.add(switchAlias);
            }
            DeviceSwitchVo deviceSwitchVo = DeviceSwitchVo.builder()
                    .deviceName(deviceName)
                    .switchName(switchName)
                    .address(address)
                    .content(content)
                    .name(name)
                    .roleName(roleName)
                    .status(status)
                    .build();
            log.info("DeviceSwitchVo:{}", deviceSwitchVo);
            SystemMessage systemMessage = SystemMessage.builder()
                    .isRead(0)
                    .subtitle("通知")
                    .serialNumber(serialNumber)
                    .type(5)
                    .content("设备开关-您的设备信息被编辑了，请及时查看")
                    .projectId(projectId)
                    .serialName(deviceName)
                    .extras(JSONObject.toJSONString(deviceSwitchVo)).build();
            log.info("SystemMessage:{}", systemMessage);
            List<Long> user = getEnterpriseUserID(serialNumber);
            user.add(mainUserId);
            log.info("需要推送的用户：{}", user);
            for (Long pushUserId : user) {
                //判断用户有没有权限
//            boolean checkPermission = userFeign.checkPermission(pushUserId, PermissionStatic.MESSAGE_LIST, projectId);
//            if (checkPermission) {
//                userIds.add(userId);
//            }
                if (pushUserId.equals(userId)) {
                    //自己不管
                    continue;
                }
                systemMessage.setUserId(pushUserId);
                systemMessageService.save(systemMessage);
                push(pushUserId,systemMessage);
            }
        } catch (Exception ee) {
            log.error("控制开关通知错误：{}", ee);
        }
    }

    /**
     * 漏电自检队列
     */
    @RabbitListener(queues = QueueConstantUtil.ProjectMessage.EXAMINATION)
    public void messageExamination(DeviceMessage deviceMessage) {
        log.info("接收到漏电自检信息，信息内容：{}",deviceMessage);
        try {
            //操作人ID
            Long userId = deviceMessage.getUserId();
            Integer projectId = deviceMessage.getProjectId();
            //设备号
            List<String> serialNumbers = (List<String>) deviceMessage.getMap().get("serialNumber");
            //状态 0关 1开
            Integer status = (Integer) deviceMessage.getMap().get("status");
            //主账号ID
            Long mainUserId = deviceMessage.getMainUserId();
            // 编辑人
            String name = userFeign.getNiknameByUserId(userId);
            //角色名称
            String roleName = userFeign.getRoleNameByUserId(userId).get(0);
            // 内容
            String content = getOperating(status)+"-漏电自检";
            //自检时间
            String time= (String) deviceMessage.getMap().get("time");
            if (StringUtils.isNotEmpty(time)) {

            time="每月"+time.replace(" ","日");
            }


            //pushUserIds装着要推送的用户 和他关联的设备 一对多
            Map<Long,List<String>> pushUserIds=new HashMap<>();
            for (String serialNumber : serialNumbers) {
                //1.先查询出拥有这台设备权限的用户
                List<Long> user = getEnterpriseUserID(serialNumber);
                user.add(mainUserId);
                //2.以用户ID为key存储当前这个设备  1对多
                for (Long aLong : user) {
                    List<String> s=pushUserIds.get(aLong);
                    if (null == s) {
                        s = new ArrayList<>();
                        pushUserIds.put(aLong, s);
                    }
                    pushUserIds.get(aLong).add(serialNumber);
                }
            }
            log.info("需要推送的用户：{}", pushUserIds);
            //3.遍历用户key 发送消息
            for (Long pushUserId : pushUserIds.keySet()) {
                //拿到用户关联的设备组
                List<String> devs = pushUserIds.get(pushUserId);
                if (null == devs || devs.size() == 0) {
                    continue;
                }
                if (pushUserId.equals(userId)) {
                    //自己不发
                    continue;
                }
                List<Object> list=new ArrayList<>();
                for (String dev : devs) {
                    //设备名称
                    String deviceName = redisUtils.getDeviceName(mainUserId, dev);
                    //设备位置
                    String address = deviceFeign.getAddressBySerialNumber(dev);
                    Map<String,String> map=new HashMap<>();
                    map.put("deviceName",deviceName);
                    map.put("address",address);
                    list.add(map);
                }
                Map<String,String> extend=new HashMap<>();
                extend.put("device",devs.size()+"台设备");
                extend.put("time",time);
                DeviceSwitchVo deviceSwitchVo = DeviceSwitchVo.builder()
                        .deviceName("")
                        .switchName(list)
                        .address("")
                        .content(content)
                        .name(name)
                        .roleName(roleName)
                        .extend(extend)
                        .status(status)
                        .build();
                log.info("DeviceSwitchVo:{}", deviceSwitchVo);

                SystemMessage systemMessage = SystemMessage.builder()
                        .isRead(0)
                        .subtitle("通知")
                        .serialNumber("")
                        .type(7)
                        .content("漏电自检-您的设备信息被编辑了，请及时查看")
                        .projectId(projectId)
                        .serialName("")
                        .extras(JSONObject.toJSONString(deviceSwitchVo)).build();
                log.info("SystemMessage:{}", systemMessage);
                systemMessage.setUserId(pushUserId);
                systemMessageService.save(systemMessage);
                push(pushUserId,systemMessage);
            }

        } catch (Exception e) {
            log.error("漏电自检队列异常，异常内容：{}",e);

        }

    }

    /**
     * 功率限定消息
     * @param deviceMessage
     */
    @RabbitListener(queues = QueueConstantUtil.ProjectMessage.LOADMAX_ONE)
    public void loadmaxOne(DeviceMessage deviceMessage) {
        log.info("接收到功率限定消息，信息内容：{}",deviceMessage);
        try {
            //操作人ID
            Long userId = deviceMessage.getUserId();
            Integer projectId = deviceMessage.getProjectId();
            //设备号
            String serialNumber = (String) deviceMessage.getMap().get("serialNumber");
            //状态 0关 1开
            Integer status = (Integer) deviceMessage.getMap().get("status");
            //开关IDs
            List<String> switchNmbers = (List<String>) deviceMessage.getMap().get("switchList");
            //主账号ID
            Long mainUserId = deviceMessage.getMainUserId();
            // 编辑人
            String name = userFeign.getNiknameByUserId(userId);
            //角色名称
            String roleName = userFeign.getRoleNameByUserId(userId).get(0);
            // 内容
            String content = "功率限定-" +getOperating(status);
            //功率值
            String loadMax= String.valueOf(deviceMessage.getMap().get("loadMax"));
            // 线路名称
            List<Object> switchName = new ArrayList<>();
            //设备名称
            String deviceName = redisUtils.getDeviceName(mainUserId, serialNumber);
            //设备位置
            String address = deviceFeign.getAddressBySerialNumber(serialNumber);
            for (String switchNmber : switchNmbers) {
                String switchAlias = redisUtils.getSwitchAlias(mainUserId, switchNmber);
                switchName.add(switchAlias);
            }
            Map<String,String> map=new HashMap<>();
            map.put("loadMax",loadMax);
            map.put("device","1台设备/"+switchNmbers.size()+"条线路");
            DeviceSwitchVo deviceSwitchVo = DeviceSwitchVo.builder()
                    .deviceName(deviceName)
                    .switchName(switchName)
                    .address(address)
                    .content(content)
                    .name(name)
                    .roleName(roleName)
                    .extend(map)
                    .status(status)
                    .build();
            log.info("DeviceSwitchVo:{}", deviceSwitchVo);
            SystemMessage systemMessage = SystemMessage.builder()
                    .isRead(0)
                    .subtitle("通知")
                    .serialNumber(serialNumber)
                    .type(6)
                    .content("功率限定-您的设备信息被编辑了，请及时查看")
                    .projectId(projectId)
                    .serialName(deviceName)
                    .extras(JSONObject.toJSONString(deviceSwitchVo)).build();
            log.info("SystemMessage:{}", systemMessage);
            List<Long> user = getEnterpriseUserID(serialNumber);
            user.add(mainUserId);
            log.info("需要推送的用户：{}", user);
            for (Long pushUserId : user) {
                //判断用户有没有权限
//            boolean checkPermission = userFeign.checkPermission(pushUserId, PermissionStatic.MESSAGE_LIST, projectId);
//            if (checkPermission) {
//                userIds.add(userId);
//            }
                if (pushUserId.equals(userId)) {
                    //自己不管
                    continue;
                }
                systemMessage.setUserId(pushUserId);
                systemMessageService.save(systemMessage);
                push(pushUserId,systemMessage);
            }

        } catch (Exception e) {
            log.error("功率限定消息发生异常，异常内容：{}",e);
        }
    }

    /**
     * 功率限定统一设置消息
     * @param deviceMessage
     */
    @RabbitListener(queues = QueueConstantUtil.ProjectMessage.LOADMAX_ALL)
    public void loadmaxAll(DeviceMessage deviceMessage) {
        log.info("接收到功率限定消息，信息内容：{}",deviceMessage);
        try {
            //操作人ID
            Long userId = deviceMessage.getUserId();
            Integer projectId = deviceMessage.getProjectId();
            //设备号
            String serialNumber = (String) deviceMessage.getMap().get("serialNumber");
            //状态 0关 1开
            Integer status = (Integer) deviceMessage.getMap().get("status");
            //开关IDs
            List<String> switchNmbers = (List<String>) deviceMessage.getMap().get("switchList");
            //主账号ID
            Long mainUserId = deviceMessage.getMainUserId();
            // 编辑人
            String name = userFeign.getNiknameByUserId(userId);
            //角色名称
            String roleName = userFeign.getRoleNameByUserId(userId).get(0);
            // 内容
            String content = "统一设置功率限定";
            //功率值
            String loadMax= String.valueOf( deviceMessage.getMap().get("loadMax"));
            // 线路名称
            List<Object> switchName = new ArrayList<>();
            //设备名称
            String deviceName = redisUtils.getDeviceName(mainUserId, serialNumber);
            //设备位置
            String address = deviceFeign.getAddressBySerialNumber(serialNumber);
            for (String switchNmber : switchNmbers) {
                String switchAlias = redisUtils.getSwitchAlias(mainUserId, switchNmber);
                switchName.add(switchAlias);
            }
            Map<String,String> map=new HashMap<>();
            map.put("loadMax",loadMax);
            map.put("device","1台设备/"+switchNmbers.size()+"条线路");
            DeviceSwitchVo deviceSwitchVo = DeviceSwitchVo.builder()
                    .deviceName(deviceName)
                    .switchName(switchName)
                    .address(address)
                    .content(content)
                    .name(name)
                    .roleName(roleName)
                    .status(status)
                    .extend(map)
                    .build();
            log.info("DeviceSwitchVo:{}", deviceSwitchVo);
            SystemMessage systemMessage = SystemMessage.builder()
                    .isRead(0)
                    .subtitle("通知")
                    .serialNumber(serialNumber)
                    .type(6)
                    .content("功率限定-您的设备信息被编辑了，请及时查看")
                    .projectId(projectId)
                    .serialName(deviceName)
                    .extras(JSONObject.toJSONString(deviceSwitchVo)).build();
            log.info("SystemMessage:{}", systemMessage);
            List<Long> user = getEnterpriseUserID(serialNumber);
            user.add(mainUserId);
            log.info("需要推送的用户：{}", user);
            for (Long pushUserId : user) {
                //判断用户有没有权限
//            boolean checkPermission = userFeign.checkPermission(pushUserId, PermissionStatic.MESSAGE_LIST, projectId);
//            if (checkPermission) {
//                userIds.add(userId);
//            }
                if (pushUserId.equals(userId)) {
                    //自己不管
                    continue;
                }
                systemMessage.setUserId(pushUserId);
                systemMessageService.save(systemMessage);
                push(pushUserId,systemMessage);
            }

        } catch (Exception e) {
            log.error("功率限定消息发生异常，异常内容：{}",e);
        }
    }

    public String getOperating(Integer status) {
        switch (status) {
            case 0:
                return "关";
            case 1:
                return "开";
            default:
                return "未知操作";
        }
    }

    /**
     * 根据设备号查询 拥有管理这台设备的人
     * @param serialNumber
     * @return
     */
    private List<Long> getEnterpriseUserID(String serialNumber) {
        List<Integer> roleId = deviceFeign.queryRoleIdBySerial(serialNumber);
        if (null == roleId || roleId.size() == 0) {
            log.info("通过设备Id查询角色Ids,没有查询到结果");
            return null;
        }
        //通过角色查询人返回lIST 然后全部推送
        Map map = new HashMap();
        map.put("roleIds", roleId);
        List<Long> userIdsByRoleId = userFeign.getUserIdsByRoleId(map);
        if (null == userIdsByRoleId || userIdsByRoleId.size() == 0) {
            log.info("通过角色查询用户List,没有查询到结果");
            return null;
        }
        log.info("查询到子账户个数：{} ,分别为：{}", userIdsByRoleId.size(), userIdsByRoleId);
        return userIdsByRoleId;
    }


    public void push(Long pushUserId,SystemMessage systemMessage) {
        try {
            Map pushMap = new HashMap();
            pushMap.put("msgType", "2");
            jPushClientExample.sendMsg(Arrays.asList(String.valueOf(pushUserId)), systemMessage.getContent(), systemMessage.getContent(),
                    systemMessage.getContent(), JpushTypeEnum.NOTIFICATION.value(), pushMap, AccountType.ENTERPRISE.value());
        } catch (Exception jce) {
            log.error("极光推送失败:{}",jce);
            jce.printStackTrace();
        }
    }
}
