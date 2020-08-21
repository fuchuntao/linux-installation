package cn.meiot.service.impl;

import cn.meiot.entity.EnterpriseAlarm;
import cn.meiot.entity.EnterpriseUserFaultMsgAlarm;
import cn.meiot.entity.TroubleTicketVo;
import cn.meiot.entity.Wss;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.enums.EarlyWarning;
import cn.meiot.entity.vo.AlarmVo;
import cn.meiot.entity.vo.FaultMessageVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.WXMessageVo;
import cn.meiot.enums.AccountType;
import cn.meiot.enums.FaultMsgContentEnum;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.feign.UserFeign;
import cn.meiot.jg.JPushClientExample;
import cn.meiot.service.AlarmVoManager;
import cn.meiot.service.EnterpriseAlarmService;
import cn.meiot.service.IEnterpriseUserFaultMsgAlarmService;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.UserInfoUtil;
import cn.meiot.utils.enums.JpushTypeEnum;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Package cn.meiot.service.impl
 * @Description:
 * @author: 武有
 * @date: 2019/12/30 10:14
 * @Copyright: www.spacecg.cn
 */

@SuppressWarnings("all")
@Slf4j
@Component
public class EnterpriseAlarmVoManager implements AlarmVoManager {
    private List<Long> userIds;
    private Integer projectId;
    private String projectName;
    private String address;
    private String uuid;//当生成故障工单时，该id表示 那些故障工单属于一个故障
    @Autowired
    private UserInfoUtil userInfoUtil;
    @Autowired
    private JPushClientExample jPushClientExample;
    @Autowired
    private IEnterpriseUserFaultMsgAlarmService enterpriseUserFaultMsgAlarmService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${wx.alarm.template_id}")
    private String template_id;
    @Value("${wx.message.url}")
    private String wxMessage;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EnterpriseAlarmService enterpriseAlarmService;


    @Override
    public void push(AlarmVo alarmVo) {
        if (null == userIds || userIds.size() == 0) {
            return;
        }
        Long mainUserId = userIds.get(0);
        if (null == alarmVo.getSwitchInfo().getEvents() || alarmVo.getSwitchInfo().getEvents().size() == 0) {
            return;
        }
        List<AlarmVo.Event> events = alarmVo.getSwitchInfo().getEvents();
        for (AlarmVo.Event e : events) {
            if (e.getEvent() == 0) {
                return;
            }
            Map<String, String> map = userInfoUtil.getParamMap(alarmVo, mainUserId);
            String deviceAlias = map.get("deviceAlias") == null ? alarmVo.getDeviceid() : map.get("deviceAlias");
            String switchAlias = map.get("switchAlias") == null ? String.valueOf(alarmVo.getSwitchInfo().getId()) : map.get("switchAlias");

            EnterpriseUserFaultMsgAlarm enterpriseUserFaultMsgAlarm = new EnterpriseUserFaultMsgAlarm();
            enterpriseUserFaultMsgAlarm.setCreateTime(SD.format(new Date()));
            enterpriseUserFaultMsgAlarm.setFaultTime(alarmVo.getTime());
            enterpriseUserFaultMsgAlarm.setEvent(e.getEvent());
            enterpriseUserFaultMsgAlarm.setSendTime(SD.format(new Date()));
            enterpriseUserFaultMsgAlarm.setProjectId(projectId);
            enterpriseUserFaultMsgAlarm.setCreateTime(SD.format(new Date()));
            enterpriseUserFaultMsgAlarm.setIsShow(0);
            enterpriseUserFaultMsgAlarm.setIsRead(0);
            enterpriseUserFaultMsgAlarm.setEquipmentAlias(deviceAlias);
            enterpriseUserFaultMsgAlarm.setSwitchAlias(switchAlias);
            enterpriseUserFaultMsgAlarm.setMsgContent(deviceAlias + "   " + switchAlias + FaultMsgContentEnum.getContent(e.getEvent()));
            enterpriseUserFaultMsgAlarm.setFaultValue(e.getValue().toString());
            if (e.getEvent() == 6 || e.getEvent() == 7) {
                enterpriseUserFaultMsgAlarm.setFaultValue(e.getValue().divide(new BigDecimal(1000)).toString());
            }
            enterpriseUserFaultMsgAlarm.setSerialNumber(alarmVo.getDeviceid());
            enterpriseUserFaultMsgAlarm.setSwitchSn(alarmVo.getSwitchInfo().getId().toString());
            enterpriseUserFaultMsgAlarm.setSwitchIndex(alarmVo.getSwitchInfo().getIndex());
            enterpriseUserFaultMsgAlarm.setSwitchStatus(0);
            enterpriseUserFaultMsgAlarm.setType(1);
            enterpriseUserFaultMsgAlarm.setUserId(mainUserId);
            enterpriseUserFaultMsgAlarm.setAddress(address);
            enterpriseUserFaultMsgAlarmService.save(enterpriseUserFaultMsgAlarm);

            for (int i=0;i<userIds.size();i++) {
                Long userId = userIds.get(i);
                EnterpriseAlarm userAlarm = new EnterpriseAlarm();
                userAlarm.setAlarmId(enterpriseUserFaultMsgAlarm.getId());
                userAlarm.setUserId(userId);
                userAlarm.setIsRead(0);
                enterpriseAlarmService.insert(userAlarm);
            try {
                WXMessageVo wxMessageVo = new WXMessageVo();
                WXMessageVo.Entry first = wxMessageVo.new Entry("您的设备发生" + FaultTitleEnum.getTitle(e.getEvent()) + ",请尽快处理。检修设备时,请注意安全，谨防触电。", "#363636");
                WXMessageVo.Entry keyword1 = wxMessageVo.new Entry(deviceAlias, "#363636");
                WXMessageVo.Entry keyword2 = wxMessageVo.new Entry(switchAlias, "#363636");
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                WXMessageVo.Entry keyword3 = wxMessageVo.new Entry(decimalFormat.format(Double.valueOf(enterpriseUserFaultMsgAlarm.getFaultValue())) + wxMessageVo.get(enterpriseUserFaultMsgAlarm.getEvent()), "#363636");
                WXMessageVo.Entry keyword4 = wxMessageVo.new Entry(enterpriseUserFaultMsgAlarm.getFaultTime(), "#363636");
                WXMessageVo.Entry remark = null;
                WXMessageVo.WXData wxData = wxMessageVo.new WXData(first, keyword1, keyword2, keyword3, keyword4, remark);
                wxMessageVo.setData(wxData);
                wxMessageVo.setTemplate_id(template_id);
                wxMessageVo.setTouser(userFeign.getOpenid(userId));
                wxPush(wxMessageVo);
            } catch (Exception wxe) {
                log.info("微信推送失败,{}", wxe);
                wxe.printStackTrace();
            }

            try {
                    Map pushMap = new HashMap();
                    pushMap.put("msgType", "2");
                    pushMap.put("faultType", String.valueOf(e.getEvent()));
                    jPushClientExample.sendMsg(Arrays.asList(String.valueOf(userId)), deviceAlias + "发生" + FaultTitleEnum.getTitle(e.getEvent()) + ",请尽快处理", FaultTitleEnum.getTitle(e.getEvent()),
                            enterpriseUserFaultMsgAlarm.getMsgContent(), JpushTypeEnum.NOTIFICATION.value(), pushMap, AccountType.ENTERPRISE.value());
            } catch (Exception jce) {
                log.info("极光推送失败");
                jce.printStackTrace();
            }
            push(enterpriseUserFaultMsgAlarm);
        }
    }

}

    private void push(EnterpriseUserFaultMsgAlarm enterpriseUserFaultMsgAlarm) {
        try {
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

    @Override
    public void init(List<Long> userIds, Integer projectId, String address, String projectName) {
        this.userIds = userIds;
        this.projectId = projectId;
        this.projectName = projectName;
        this.address = address;
        this.uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
    }


    @Override
    public void wxPush(WXMessageVo wxMessageVo) {
        String accessToken = userFeign.getAccessToken();
        if (StringUtils.isEmpty(accessToken)) {
            log.info("accessToken获取失败");
            return;
        }
        String openid = wxMessageVo.getTouser();
        if (StringUtils.isEmpty(openid)) {
            log.info("openid获取失败");
            return;
        }
        String url = wxMessage + accessToken;
        ResponseEntity<String> entity = restTemplate.postForEntity(url, JSONObject.toJSONString(wxMessageVo), String.class);
        String body = entity.getBody();
        log.info("获取ticket结果：{}", body);
    }


}
