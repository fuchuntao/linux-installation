//package cn.meiot.receive;
//
//import cn.meiot.entity.FaultMessage;
//import cn.meiot.entity.bo.Crcuit;
//import cn.meiot.entity.vo.DeviceEventVo;
//import cn.meiot.entity.vo.MqVo;
//import cn.meiot.entity.vo.Result;
//import cn.meiot.feign.DeviceFeign;
//import cn.meiot.feign.UserFeign;
//import cn.meiot.jg.JPushClientExample;
//import cn.meiot.service.IFaultMessageService;
//import cn.meiot.utils.*;
//import cn.meiot.enums.FaultMsgContentEnum;
//import cn.meiot.enums.FaultTitleEnum;
//import cn.meiot.utils.enums.JpushTypeEnum;
//import com.alibaba.fastjson.JSONObject;
//import com.google.gson.Gson;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//
///**
// * 接收设备消息
// */
//@Slf4j
//@Component
//public class DeviceMq {
//
//
//        @Autowired
//        private JPushClientExample jPushClientExample;
//
//        @Autowired
//        private IFaultMessageService faultMessageService;
//
//        @Autowired
//        private DeviceFeign deviceFeign;
//
//
//        @Autowired
//        private RedisTemplate redisTemplate;
//
//        @Autowired
//        private CommonUtil commonUtil;
//
//        @Autowired
//        private UserFeign userFeign;
//        @Autowired
//        private UserInfoUtil userInfoUtil;
//
//
//    //    @RabbitListener(queues = QueueConstantUtil.MQTT_DEVICE_QUEUE)
////    public void deviceEvent(String content) {
////        try {
////            log.info("接收参数为：{}", content);
////            //String value = "{\"serial_number\":\"1\",\"timestamp\":1111111111,\"event\":8,\"switch_sn\":9555555,\"switch_index\":1}";
////            Gson gson = new Gson();
////            DeviceEventVo deviceEventVo = gson.fromJson(content, DeviceEventVo.class);
////            deviceEventVo.setTimestamp(ConstantsUtil.DF.format(new Date(Long.valueOf(deviceEventVo.getTimestamp()) * 1000l)));
////
////            log.info("对象：{}", deviceEventVo);
////            if (null == deviceEventVo || StringUtils.isEmpty(deviceEventVo.getSerialNumber()) || null == deviceEventVo.getEvent()) {
////                log.info("入参有问题，终止执行");
////                return;
////            }
////            Integer projectId = deviceFeign.getProjectIdBySerialNumber(deviceEventVo.getSerialNumber());
////            log.info("==projectId=" + projectId + "--------------------------------------");
////            FaultMessage faultMessage = FaultMessage.builder()
////                    //.userId(Integer.valueOf(userId))
////                    .sendTime(ConstantsUtil.DF.format(new Date()))
////                    .createTime(deviceEventVo.getTimestamp())
////                    .switchIndex(deviceEventVo.getSwitchIndex())
////                    .isRead(0)
////                    .faultTime(deviceEventVo.getTimestamp())
////                    .switchSn(deviceEventVo.getSwitchSn())
////                    .serialNumber(deviceEventVo.getSerialNumber())
////                    // .switchEvent(event)
////                    .projectId(projectId)
////                    .build();
////            List<String> userIds = commonUtil.getRtUserIdBySerialNumber(deviceEventVo.getSerialNumber());
////            if (null == userIds) {
////                log.info("设备号：{}未获取到主账号id，此条记录丢弃！", deviceEventVo.getSerialNumber());
////                return;
////            }
////            //循环事件
////            for (Integer event : deviceEventVo.getEvent()) {
////                if (event > 7 || event < 1) {
////                    log.info("此事件不推送，事件：{}", event);
////                    return;
////                }
//////                redisTemplate.opsForValue().set(RedisConstantUtil.FAULT_SERIALNUMER + "_" + faultMessage.getSwitchSn(),  );
////                pushMsg(faultMessage, userIds, event);
////            }
////        } catch (Exception e) {
////            MqUtils.log(e, content, QueueConstantUtil.MQTT_DEVICE_QUEUE);
////        }
////    }
////
////
////    /**
////     * 推送消息
////     *
////     * @param faultMessage
////     * @param list
////     * @param event
////     */
////    private void pushMsg(FaultMessage faultMessage, List<String> list, Integer event) throws Exception {
////        for (String userId : list) {
////            //通过用户id获取到设备的别名
////            Object deviceAlias = redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER, userId + "_" + faultMessage.getSerialNumber());//redisUtil.getHashValueByKey(RedisConstantUtil.NIKNAME_SERIALNUMBER, userId+"_"+faultMessage.getSerialNumber());
////            if (null != deviceAlias) {
////                faultMessage.setEquipmentAlias(deviceAlias.toString());
////            } else {
////                faultMessage.setEquipmentAlias(faultMessage.getSerialNumber());
////            }
////            //设置开关的别名
////            Object switchAlias = redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SWITCH, userId + "_" + faultMessage.getSwitchSn());//redisUtil.getHashValueByKey(RedisConstantUtil.NIKNAME_SWITCH, userId+"_"+faultMessage.getSwitchSn());
////            if (null != switchAlias) {
////                faultMessage.setSwitchAlias(switchAlias.toString());
////            } else {
////                faultMessage.setSwitchAlias(faultMessage.getSwitchIndex().toString());
////            }
////
////            //将事件信息存储到数据库中
////            faultMessage.setUserId(Integer.valueOf(userId));//用户id
////            faultMessage.setSwitchEvent(event);//事件
////            faultMessage.setMsgContent(faultMessage.getEquipmentAlias() + "   " + faultMessage.getSwitchAlias() + FaultMsgContentEnum.getContent(event));//故障消息内容
////            String mainUserId = list.get(0);
////            log.info("要存储的故障消息主ID:" + mainUserId + " 项目Id：" + faultMessage.getProjectId());
////            //推送 项目ID等于0 为app用户 需要极光推送
////            push(faultMessage, mainUserId);
////        }
////    }
////
////    private void push(FaultMessage faultMessage, String mainUserId) throws Exception {
////        Map<String, String> map = new HashMap<String, String>();
////        List<String> sendIds = new ArrayList<String>();
////        if (faultMessage.getProjectId() == 0) {
////            log.info("app推送");
////            faultMessageService.save(faultMessage);
////            map.put("msgType", "2");
////            map.put("faultType", String.valueOf(faultMessage.getSwitchEvent()));
////            sendIds.add(String.valueOf(faultMessage.getUserId()));
////            jPushClientExample.sendMsg(sendIds, faultMessage.getEquipmentAlias() + "发生" + FaultTitleEnum.getTitle(faultMessage.getSwitchEvent()) + ",请尽快处理", FaultTitleEnum.getTitle(faultMessage.getSwitchEvent()),
////                    faultMessage.getMsgContent(), JpushTypeEnum.NOTIFICATION.value(), map);
////            log.info("app故障消息已推送 用户为：" + faultMessage.getUserId());
////        } else if (faultMessage.getProjectId() != -1) {
////            log.info("web长连接推送");
////            log.info("项目ID：" + faultMessage.getProjectId() + " 是企业用户 故障消息需要存主用户ID，主用户id：" + mainUserId);
////            faultMessage.setUserId(Integer.parseInt(mainUserId));
////            faultMessageService.save(faultMessage);
////            log.info("数据详细信息：", JSONObject.toJSONString(faultMessage));
////        } else {
////            log.info("========= deviceFeign.getProjectIdBySerialNumber 调取失败！");
////        }
////    }
//
//
////    @RabbitListener(queues = QueueConstantUtil.SWITCH_STATUS)
////    public void Event104(String content) {
////        try {
////            //转换数据、
////            MqVo mqVo = MqUtils.getMqVo(content);
////            if (StringUtils.isEmpty(mqVo.getSerialNumber())) {
////                log.info("104队列接受到设备参数 设备号为空,此条数据不监听");
////                return;
////            }
////            List<Long> userIds = commonUtil.getRtUserIdBySerialNumber(mqVo.getSerialNumber());
////            //项目ID
////            Integer projectId = deviceFeign.getProjectIdBySerialNumber(mqVo.getSerialNumber());
////            Crcuit crcuit = JSONObject.parseObject(JSONObject.toJSONString(redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_PARAMETER + projectId)), Crcuit.class);
////            /**
////             * 根据项目ID来判断是不是企业 如果是企业调用接口查到角色 通过角色查询账号 给账号发消息
////             */
////
////            //判断是不是报警 是 报警, 不是 判断是不是预警 不是预警放过
////            if (mqVo.getMqStatusVos().get(0).getEvent().length > 0) {
////                for (int i = 0; i < mqVo.getMqStatusVos().get(0).getEvent().length; i++) {
////                    Integer event = mqVo.getMqStatusVos().get(0).getEvent()[i];
////                    //故障报警
////                    malfunctionAlarm(mqVo, userIds, projectId, event);
////                    /**
////                     * 预警 正常 企业
////                     */
////                    if (event == 0) {
////                        if (projectId > 0) {
//////                            FaultMessage faultMessage = MsgUtil.getFaultMessage(mqVo, userInfoUtil.getParamMap(mqVo,String.valueOf(userIds.get(0))), event);
//////                            faultMessageService.save(faultMessage);
////                            //通过设备Id查询角色Ids
////                            List<Long> userIdsByRoleId = getEnterpriseUserID(mqVo.getSerialNumber());
////                            if (userIdsByRoleId == null) return;
////                            //给企业APP推送的预警消息
////                            for (Long userId : userIdsByRoleId) {
//////                                ifEarlyWarning 方法判断是否预警 如果有预警则会返回装有参数的预警消息
////                                List<FaultMessage> list = ifEarlyWarning(crcuit, mqVo, String.valueOf(userId));
////                                if (null != list && list.size() > 0) {
////                                    for (FaultMessage qy : list) {
////                                        qy.setUserId(Long.valueOf(String.valueOf(userIds.get(0))));
////                                        faultMessageService.save(qy);
////                                    }
////                                    for (FaultMessage f : list) {
////                                        log.info("企业获取到的预警消息数据为：{}", f);
////                                        faultMessageService.save(f);
////                                        log.info("企业app预警消息存储成功{}", f);
////                                        MsgUtil.appAlarmPush(f, jPushClientExample);
////                                    }
////                                }
////                            }
////                            return;
////                        }
////                        if (projectId == 0) {
////                            for (Long userId : userIds) {
//////                            ifEarlyWarning 方法判断是否预警 如果有预警则会返回装有参数的预警消息
////                                List<FaultMessage> list = ifEarlyWarning(crcuit, mqVo, String.valueOf(userId));
////                                if (null != list && list.size() > 0) {
////                                    for (FaultMessage f : list) {
////                                        MsgUtil.appAlarmPush(f, jPushClientExample);
////                                        faultMessageService.save(f);
////                                    }
////                                }
////                            }
////                        } else {
////                            log.info("项目ID调用失败 熔断返回-1：{}", projectId);
////                        }
////                    }
////
////                }
////            }
////        } catch (Exception e) {
////            log.info("104消息队列异常 异常类：{}，异常内容:{}", e, e.getMessage());
////            log.info("104消息队列异常 异常类：{}，异常内容:{}", e, e.getStackTrace());
////            log.info("104消息队列异常 异常类：{}，异常内容:{}", e, e.getCause());
////            log.info("104消息队列异常 异常类：{}，异常内容:{}", e, e.getLocalizedMessage());
////            e.printStackTrace();
////        }
////
////    }
//
//    /**
//     * 故障报警
//     *
//     * @param mqVo
//     * @param userIds
//     * @param projectId
//     * @param event
//     * @throws Exception
//     */
////    private void malfunctionAlarm(MqVo mqVo, List<Long> userIds, Integer projectId, Integer event) throws Exception {
////        if (MqUtils.ifAlarm(event)) {
////            if (event == 8) {
////                redisTemplate.opsForValue().set(RedisConstantUtil.FAULT_SERIALNUMER + "_" + mqVo.getMqDeviceVos().get(0).getId(), mqVo.getMqStatusVos().get(0).getSwitchs());
////                log.info("事件8已经存储到redis 内容为{}", RedisConstantUtil.FAULT_SERIALNUMER + "_" + mqVo.getMqDeviceVos().get(0).getId(), mqVo.getMqStatusVos().get(0).getSwitchs());
////                log.info("设备号为：{}",mqVo.getSerialNumber());
////                return;
////            }
////            if (projectId == -1 || null == projectId) {
////                log.info("没有查询到项目ID此记录丢弃");
////                return;
////            }
////            if (projectId > 0) {
//////                          //企业
////                FaultMessage faultMessage = MsgUtil.getFaultMessage(mqVo, userInfoUtil.getParamMap(mqVo, String.valueOf(userIds.get(0))), event);
////                faultMessage.setFaultValue(MqUtils.getValue(event, mqVo).toString());
////                faultMessageService.save(faultMessage);
////                log.info("企业故障消息存储成功{}", faultMessage);
////                //TODO
////                //通过设备Id查询角色Ids
////                List<Long> userIdsByRoleId = getEnterpriseUserID(mqVo.getSerialNumber());
////                if (userIdsByRoleId == null) return;
////                push(mqVo, userIdsByRoleId, event);
////                return;
////            }
////            //给app用户推送故障消息的循环
////            push(mqVo, userIds, event);
////            return;
////        }
////
////    }
////
////    /**
////     * 推送
////     *
////     * @param mqVo
////     * @param userIds
////     * @param event
////     * @throws Exception
////     */
////    private void push(MqVo mqVo, List<Long> userIds, Integer event) throws Exception {
////        for (Long userId : userIds) {
////            FaultMessage faultMessage = MsgUtil.getFaultMessage(mqVo, userInfoUtil.getParamMap(mqVo, String.valueOf(userId)), event);
////            log.info("获取到的故障消息数据为：{}", faultMessage);
////            faultMessage.setFaultValue(MqUtils.getValue(event, mqVo).toString());
////            MsgUtil.appAlarmPush(faultMessage, jPushClientExample);
////            faultMessageService.save(faultMessage);
////            log.info("app故障消息存储成功{}", faultMessage);
////        }
////    }
//
////    private List<Long> getEnterpriseUserID(String serialNumber) {
////        List<Integer> roleId = deviceFeign.queryRoleIdBySerial(serialNumber);
////        if (null == roleId || roleId.size() == 0) {
////            log.info("通过设备Id查询角色Ids,没有查询到结果");
////            return null;
////        }
////        //通过角色查询人返回lIST 然后全部推送
////        Map map = new HashMap();
////        map.put("roleIds", roleId);
////        List<Long> userIdsByRoleId = userFeign.getUserIdsByRoleId(map);
////        if (null == userIdsByRoleId || userIdsByRoleId.size() == 0) {
////            log.info("通过角色查询用户List,没有查询到结果");
////            return null;
////        }
////        return userIdsByRoleId;
////    }
//
//
//    /**
//     * 判断是否预警
//     */
//
//    public List<FaultMessage> ifEarlyWarning(Crcuit crcuit, MqVo mqVo, String userId) {
//        if (null == crcuit) {
//            return null;
//        }
//        FaultMessage faultMessage = null;
//        List<FaultMessage> list = new ArrayList<>();
////        Crcuit crcuit = (Crcuit) redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_PARAMETER + projectId);
//        /**
//         * 判断当前电流是否大于redis里存的预警电流 大于返回
//         */
//        if (null != crcuit.getCurrent()) {
//            if (mqVo.getMqStatusVos().get(0).getCurrent()[0].compareTo(crcuit.getCurrent()) > 0) {
//                faultMessage = MsgUtil.getFaultMessage(mqVo, userInfoUtil.getParamMap(mqVo, userId), 10);
//                faultMessage.setFaultValue(mqVo.getMqStatusVos().get(0).getCurrent()[0].divide(new BigDecimal(1000)).toString());
//                list.add(faultMessage);
//            }
//        }        /**
//         * 判断当前电压是否大于redis里存的预警电压 大于返回
//         */
//        if (null != crcuit.getVoltage()) {
//            if (mqVo.getMqStatusVos().get(0).getVoltage()[0].compareTo(crcuit.getVoltage()) > 0) {
//                faultMessage = MsgUtil.getFaultMessage(mqVo, userInfoUtil.getParamMap(mqVo, userId), 11);
//                faultMessage.setFaultValue(mqVo.getMqStatusVos().get(0).getVoltage()[0].divide(new BigDecimal(1000)).toString());
//                list.add(faultMessage);
//            }
//        }
//        /**
//         * 判断当前电压是否小于redis里存的欠压预警 小于返回
//         */
//        if (null != crcuit.getUnderVoltage()) {
//            if (mqVo.getMqStatusVos().get(0).getVoltage()[0].compareTo(crcuit.getUnderVoltage()) < 0) {
//                faultMessage = MsgUtil.getFaultMessage(mqVo, userInfoUtil.getParamMap(mqVo, userId), 12);
//                faultMessage.setFaultValue(mqVo.getMqStatusVos().get(0).getVoltage()[0].divide(new BigDecimal(1000)).toString());
//                list.add(faultMessage);
//            }
//        }
//        /**
//         * 判断当前 功率（电压乘以电流等于功率） 大于redis里存的功率 大于返回
//         */
//        if (null != crcuit.getPower()) {
//            if (mqVo.getMqStatusVos().get(0).getVoltage()[0].multiply(mqVo.getMqStatusVos().get(0).getCurrent()[0]).compareTo(crcuit.getPower()) > 0) {
//                faultMessage = MsgUtil.getFaultMessage(mqVo, userInfoUtil.getParamMap(mqVo, userId), 13);
//                faultMessage.setFaultValue(mqVo.getMqStatusVos().get(0).getVoltage()[0].multiply(mqVo.getMqStatusVos().get(0).getCurrent()[0].divide(new BigDecimal(1000000))).toString());
//                list.add(faultMessage);
//            }
//        }
//
//        /**
//         * 判断当前漏电值是否大于redis里存的漏电值
//         */
//        if (null != crcuit.getLeakage()) {
//            if (mqVo.getMqStatusVos().get(0).getLeakage().compareTo(crcuit.getLeakage()) > 0) {
//                faultMessage = MsgUtil.getFaultMessage(mqVo, userInfoUtil.getParamMap(mqVo, userId), 14);
//                faultMessage.setFaultValue(mqVo.getMqStatusVos().get(0).getCurrent()[0].toString());
//                list.add(faultMessage);
//            }
//        }
//
//        /**
//         * 判断当前温度
//         */
//        if (null != crcuit.getTemp()) {
//            if (mqVo.getMqStatusVos().get(0).getTemp().compareTo(crcuit.getTemp()) > 0) {
//                faultMessage = MsgUtil.getFaultMessage(mqVo, userInfoUtil.getParamMap(mqVo, userId), 15);
//                faultMessage.setFaultValue(mqVo.getMqStatusVos().get(0).getCurrent()[0].toString());
//                list.add(faultMessage);
//            }
//        }
//        return list;
//    }
//
//
//}
