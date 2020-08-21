package cn.meiot.receive;

import cn.meiot.common.PermissionStatic;
import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.EnterpriseUserFaultMsgAlarm;
import cn.meiot.entity.FaultMessage;
import cn.meiot.entity.Wss;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.equipment2.upwarn.WarnInfo;
import cn.meiot.entity.vo.AlarmVo;
import cn.meiot.entity.vo.FaultMessageVo;
import cn.meiot.entity.vo.MqVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.jg.JPushClientExample;
import cn.meiot.service.AlarmVoManager;
import cn.meiot.service.IAppUserFaultMsgAlarmService;
import cn.meiot.service.IEnterpriseUserFaultMsgAlarmService;
import cn.meiot.service.IFaultMessageService;
import cn.meiot.service.impl.EnterpriseAlarmVoManager;
import cn.meiot.service.impl.PersonalAlarmVoManager;
import cn.meiot.utils.*;
import cn.meiot.utils.enums.TypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Package cn.meiot.receive
 * @Description:
 * @author: 武有
 * @date: 2019/10/22 9:36
 * @Copyright: www.spacecg.cn
 */
@Slf4j
@SuppressWarnings("all")
@Component
public class AlarmMq {
    @Autowired
    private JPushClientExample jPushClientExample;

    @Autowired
    private IFaultMessageService faultMessageService;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private UserFeign userFeign;
    @Autowired
    private UserInfoUtil userInfoUtil;

    @Autowired
    private IAppUserFaultMsgAlarmService appUserFaultMsgAlarmService;
    @Autowired
    private IEnterpriseUserFaultMsgAlarmService enterpriseUserFaultMsgAlarmService;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @RabbitListener(queues = QueueConstantUtil.ALARM_QUEUE)
    public void ALARM_QUEUE(String context) {
        log.info(">>>报警预警内容：{}", context);

        try {
            AlarmVo alarmVo = new AlarmVo(context);
            log.info("故障报警：{}", alarmVo);
            List<Long> userIds = commonUtil.getRtUserIdBySerialNumber(alarmVo.getDeviceid());
            //项目ID
            Integer projectId = deviceFeign.getProjectIdBySerialNumber(alarmVo.getDeviceid());
//            Integer projectId= 24;
            selectPush(alarmVo, userIds, projectId);
        } catch (Exception e) {
            log.error("故障报警队列错误：{}", e);
        }


    }

    private void selectPush(AlarmVo alarmVo, List<Long> userIds, Integer projectId) {
        if (alarmVo.getSwitchInfo() == null || alarmVo == null || alarmVo.getSwitchInfo().getType()==null) {
            return;
        }
        try {
            if (null == projectId || projectId < 0) {
                log.info("远程获取项目ID失败 接口为：getProjectIdBySerialNumber，获取到的内容为：{}", projectId);
                return;
            }
//            log.info("\r\n收到一条：《{}》信息！\n平台：{}\n消息内容为：{},\n推送目标：{}", TypeEnum.getName(alarmVo.getSwitchInfo().getType()), projectId > 0 ? "企业" : "个人", alarmVo, userIds);
            //个人
            if (projectId == 0) {
                AlarmVoManager alarmVoManager = null;
                //预警
                if (alarmVo.getSwitchInfo().getType() == 2) {
                    alarmVoManager = SpringUtil.getBean("personalEarlyWarningManager");
                }
                //报警
                if (alarmVo.getSwitchInfo().getType() == 1) {
                    alarmVoManager = SpringUtil.getBean("personalAlarmVoManager");
                }
                alarmVoManager.init(userIds, projectId, null, null);
                alarmVoManager.push(alarmVo);
                return;
            }

            //企业
            if (projectId > 0) {

                List<Long> user = getEnterpriseUserID(alarmVo.getDeviceid());
                //如果是企业账号 该userids里只有一个主账户 需要通过设备号查询角色 通过角色去用户查询出对应的用户id添加到userids
                for (Long userId : user) {
                    //判断用户有没有权限
                    boolean checkPermission = userFeign.checkPermission(userId, PermissionStatic.MESSAGE_LIST, projectId);
                    if (checkPermission) {
                        userIds.add(userId);
                    }
                }


                String projectName = (String) redisTemplate.opsForHash().get(RedisConstantUtil.PROJECT_NAMES, projectId.toString());
                String address = deviceFeign.getAddressBySerialNumber(alarmVo.getDeviceid());
                AlarmVoManager alarmVoManager = null;
                //预警
                if (alarmVo.getSwitchInfo().getType() == 2) {
                    alarmVoManager = SpringUtil.getBean("enterpriseEarlyWarningManager");
                }

                //报警
                if (alarmVo.getSwitchInfo().getType() == 1) {
                    alarmVoManager = SpringUtil.getBean("enterpriseAlarmVoManager");
                }
                log.info("推送人数：{}", userIds.size());
                log.info("推送人为：{}", userIds);
                alarmVoManager.init(userIds, projectId, address, projectName);
                alarmVoManager.push(alarmVo);
            }
        } catch (Exception e) {
            log.error("故障异常==>:", e);
        }
    }


    private void push(EnterpriseUserFaultMsgAlarm enterpriseUserFaultMsgAlarm, String tocken) {
        rabbitTemplate.convertAndSend(QueueConstantUtil.WSS_CMD_21, QueueConstantUtil.WSS_KEY, JSONObject.toJSONString(new Wss(ConstantsUtil.SocketType.MSG, tocken, enterpriseUserFaultMsgAlarm)));
        log.info("web端消息推送，已推送");
    }


    private void push(EnterpriseUserFaultMsgAlarm enterpriseUserFaultMsgAlarm) {
        try {
            /**
             * 需要推送的用户组
             */
            List<Long> userIds = userFeign.getAllUserIdByMainUser(enterpriseUserFaultMsgAlarm.getUserId());
            Long userId = enterpriseUserFaultMsgAlarm.getUserId();
            Integer projectId = enterpriseUserFaultMsgAlarm.getProjectId();
            /**
             * 不同类型的统计图表 查询当前企业的当前项目的所有故障统计
             */
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("projectId", projectId);

            Result statisticalAlarmAll = enterpriseUserFaultMsgAlarmService.getStatisticalAlarmAll(map);
            log.info("不同类型的统计图表 查询当前企业的当前项目的所有故障统计{}", statisticalAlarmAll);
            /**
             * 报警总条数 包括已经删除的设备
             */
            Integer total = enterpriseUserFaultMsgAlarmService.getStatisticalAlarmTotal(map);
            log.info("报警总条数 包括已经删除的设备{}", total);
            /**
             * 待处理
             */
            map.put("switchStatus", 1);
            Integer total1 = enterpriseUserFaultMsgAlarmService.getStatisticalAlarmTotal(map);
            log.info("待处理{}", total1);

            /**
             * 处理中
             */
            map.put("switchStatus", 2);
            Integer total2 = enterpriseUserFaultMsgAlarmService.getStatisticalAlarmTotal(map);
            log.info("处理中{}", total2);
            /**
             * 已处理
             */
            map.put("switchStatus", 3);
            Integer total3 = enterpriseUserFaultMsgAlarmService.getStatisticalAlarmTotal(map);
            log.info("已处理{}", total3);
            /**
             *  当前故障 enterpriseUserFaultMsgAlarm
             */
            log.info("当前故障 enterpriseUserFaultMsgAlarm{}", enterpriseUserFaultMsgAlarm);

            /**
             *故障设备总数
             */
            Integer faultNumber = enterpriseUserFaultMsgAlarmService.getFaultNumber(map);
            log.info("故障设备总数", faultNumber);

            Map<String, Object> totalMap = new HashMap<>();
            totalMap.put("total1", total3);
            totalMap.put("total2", total2);
            totalMap.put("total3", total1);

            log.info("当前故障 enterpriseUserFaultMsgAlarm{}", enterpriseUserFaultMsgAlarm);
            FaultMessageVo faultMessageVo = new FaultMessageVo();
            faultMessageVo.setSwitchName(enterpriseUserFaultMsgAlarm.getSwitchAlias());
            faultMessageVo.setDistributionBoxName(enterpriseUserFaultMsgAlarm.getEquipmentAlias());
            faultMessageVo.setFaultStatus(String.valueOf(enterpriseUserFaultMsgAlarm.getSwitchStatus()));
            faultMessageVo.setFaultTypeId(String.valueOf(enterpriseUserFaultMsgAlarm.getEvent()));
            faultMessageVo.setFaultTypeName(FaultTitleEnum.getTitle(enterpriseUserFaultMsgAlarm.getEvent()));
            faultMessageVo.setFaultTime(enterpriseUserFaultMsgAlarm.getFaultTime());
            faultMessageVo.setSerialNumber(enterpriseUserFaultMsgAlarm.getSerialNumber());
            faultMessageVo.setSwitchSn(enterpriseUserFaultMsgAlarm.getSwitchSn());
            faultMessageVo.setAddress(enterpriseUserFaultMsgAlarm.getAddress());

            rabbitTemplate.convertAndSend(QueueConstantUtil.WSS_CMD_21, QueueConstantUtil.WSS_KEY, JSONObject.toJSONString(new Wss(1, "BigData-" + enterpriseUserFaultMsgAlarm.getProjectId(), total)));
            rabbitTemplate.convertAndSend(QueueConstantUtil.WSS_CMD_21, QueueConstantUtil.WSS_KEY, JSONObject.toJSONString(new Wss(2, "BigData-" + enterpriseUserFaultMsgAlarm.getProjectId(), statisticalAlarmAll.getData())));
            rabbitTemplate.convertAndSend(QueueConstantUtil.WSS_CMD_21, QueueConstantUtil.WSS_KEY, JSONObject.toJSONString(new Wss(3, "BigData-" + enterpriseUserFaultMsgAlarm.getProjectId(), totalMap)));
            rabbitTemplate.convertAndSend(QueueConstantUtil.WSS_CMD_21, QueueConstantUtil.WSS_KEY, JSONObject.toJSONString(new Wss(4, "BigData-" + enterpriseUserFaultMsgAlarm.getProjectId(), faultMessageVo)));
            rabbitTemplate.convertAndSend(QueueConstantUtil.WSS_CMD_21, QueueConstantUtil.WSS_KEY, JSONObject.toJSONString(new Wss(5, "BigData-" + enterpriseUserFaultMsgAlarm.getProjectId(), faultNumber)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 根据设备号查询 拥有管理这台设备的人
     *
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


    @RabbitListener(queues = QueueConstantUtil.ALARM_QUEUE_TWO)
    public void alarmQueueTwo(String content) {

        try {


            Map parseObject = JSON.parseObject(content, Map.class);
            //设备号
            String serialNumber = MqttUtil.findSerialNumber(parseObject);
//            Long time = MqttUtil.findTime(parseObject);
            Long time = MqttUtil.findTime(parseObject);
            List<Long> userIds = commonUtil.getRtUserIdBySerialNumber(serialNumber);
            //项目ID
            Integer projectId = deviceFeign.getProjectIdBySerialNumber(serialNumber);
            List<WarnInfo> objectList = MqttUtil.findData2(parseObject, WarnInfo.class);
            for (WarnInfo warnInfo : objectList) {
                log.info("WarnInfo:{}",warnInfo);
                redisTemplate.opsForValue().set(RedisConstantUtil.FAULT_SERIALNUMER + "_" + warnInfo.getSid(), warnInfo.getFaultStatus(), 7, TimeUnit.DAYS);
                AlarmVo alarmVo = new AlarmVo(warnInfo);
                alarmVo.setDeviceid(serialNumber);
                alarmVo.setTime(ConstantsUtil.getSimpleDateFormat().format(new Date(time * 1000)));
                log.info("alarmVo:{}",alarmVo);
                selectPush(alarmVo, userIds, projectId);
            }
        } catch (Exception e) {
            log.error("协议二报警错误：{}", e);
        }

    }


}
