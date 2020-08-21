package cn.meiot.service.impl;

import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.TroubleTicketVo;
import cn.meiot.entity.UserAlarm;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.enums.EarlyWarning;
import cn.meiot.entity.vo.AlarmVo;
import cn.meiot.entity.vo.WXMessageVo;
import cn.meiot.enums.FaultMsgContentEnum;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.exception.AlarmException;
import cn.meiot.feign.UserFeign;
import cn.meiot.jg.JPushClientExample;
import cn.meiot.service.AlarmVoManager;
import cn.meiot.service.IAppUserFaultMsgAlarmService;
import cn.meiot.service.UserAlarmService;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.UserInfoUtil;
import cn.meiot.utils.enums.AlarmStatusEnum;
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
 * @date: 2019/12/28 17:05
 * @Copyright: www.spacecg.cn
 */
@Component
@SuppressWarnings("all")
@Slf4j
public class PersonalAlarmVoManager implements AlarmVoManager {
    @Autowired
    private IAppUserFaultMsgAlarmService appUserFaultMsgAlarmService;
    @Autowired
    private UserInfoUtil userInfoUtil;
    @Autowired
    private JPushClientExample jPushClientExample;
    private List<Long> userIds;
    private Integer projectId;
    @Autowired
    private UserAlarmService userAlarmService;


    @Value("${wx.alarm.template_id}")
    private String template_id;
    @Value("${wx.message.url}")
    private String wxMessage;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void push(AlarmVo alarmVo) {
        if (null == userIds || userIds.size() == 0) {
            return;
        }

        if (null == alarmVo.getSwitchInfo().getEvents() || alarmVo.getSwitchInfo().getEvents().size() == 0) {
            return;
        }
//        AuthUserBo authUserBo = userInfoUtil.getAuthUserBo(userId);
        List<AlarmVo.Event> events = alarmVo.getSwitchInfo().getEvents();
        for (AlarmVo.Event e : events) {
            if (e.getEvent() == 0) {
                return;
            }
            AppUserFaultMsgAlarm appUserFaultMsgAlarm = new AppUserFaultMsgAlarm();
            appUserFaultMsgAlarm.setCreateTime(SD.format(new Date()));
            appUserFaultMsgAlarm.setFaultTime(alarmVo.getTime());
//            appUserFaultMsgAlarm.setEquipmentAlias(deviceAlias);
            appUserFaultMsgAlarm.setEvent(e.getEvent());
            appUserFaultMsgAlarm.setSendTime(SD.format(new Date()));
            appUserFaultMsgAlarm.setProjectId(projectId);
//            appUserFaultMsgAlarm.setUserId(userId);
            appUserFaultMsgAlarm.setCreateTime(SD.format(new Date()));
            appUserFaultMsgAlarm.setIsShow(0);
            appUserFaultMsgAlarm.setIsRead(0);
            appUserFaultMsgAlarm.setFaultValue(e.getValue().toString());
            if (e.getEvent() == 6 || e.getEvent() == 7) {
                appUserFaultMsgAlarm.setFaultValue(e.getValue().divide(new BigDecimal(1000)).toString());
            }
            appUserFaultMsgAlarm.setSerialNumber(alarmVo.getDeviceid());
//            appUserFaultMsgAlarm.setSwitchAlias(switchAlias);
            appUserFaultMsgAlarm.setSwitchSn(alarmVo.getSwitchInfo().getId().toString());
//            appUserFaultMsgAlarm.setMsgContent(deviceAlias + "   " + switchAlias + FaultMsgContentEnum.getContent(e.getEvent()));
            appUserFaultMsgAlarm.setSwitchIndex(alarmVo.getSwitchInfo().getIndex());
            appUserFaultMsgAlarm.setSwitchStatus(1);
            appUserFaultMsgAlarm.setType(1);
            appUserFaultMsgAlarm.setStatus(AlarmStatusEnum.YIJIANBAOXIU.value());
            appUserFaultMsgAlarm.setNote("");
            appUserFaultMsgAlarmService.save(appUserFaultMsgAlarm);
            log.info("报警内容:{}", appUserFaultMsgAlarm);
            for (Long userId : userIds) {
                Map<String, String> map = userInfoUtil.getParamMap(alarmVo, userId);
                String deviceAlias = map.get("deviceAlias") == null ? alarmVo.getDeviceid() : map.get("deviceAlias");
                String switchAlias = map.get("switchAlias") == null ? String.valueOf(alarmVo.getSwitchInfo().getId()) : map.get("switchAlias");

                UserAlarm userAlarm = new UserAlarm();
                userAlarm.setAlarmId(appUserFaultMsgAlarm.getId());
                userAlarm.setUserId(userId);
                userAlarm.setIsRead(0);
                userAlarm.setDeviceAlias(deviceAlias);
                userAlarm.setSwitchAlias(switchAlias);
                userAlarm.setMsgContent(deviceAlias + "   " + switchAlias + FaultMsgContentEnum.getContent(EarlyWarning.get(e.getEvent())));
                userAlarmService.insert(userAlarm);


                try {
                    WXMessageVo wxMessageVo = new WXMessageVo();
                    WXMessageVo.Entry first = wxMessageVo.new Entry("您的设备发生" + FaultTitleEnum.getTitle(e.getEvent()) + ",请尽快处理。检修设备时，请注意安全，谨防触电。", "#363636");
                    WXMessageVo.Entry keyword1 = wxMessageVo.new Entry(deviceAlias, "#363636");
                    WXMessageVo.Entry keyword2 = wxMessageVo.new Entry(switchAlias, "#363636");
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    WXMessageVo.Entry keyword3 = wxMessageVo.new Entry(decimalFormat.format(Double.valueOf(appUserFaultMsgAlarm.getFaultValue())) + wxMessageVo.get(appUserFaultMsgAlarm.getEvent()), "#363636");
                    WXMessageVo.Entry keyword4 = wxMessageVo.new Entry(appUserFaultMsgAlarm.getFaultTime(), "#363636");
                    WXMessageVo.Entry remark = null;
                    WXMessageVo.WXData wxData = wxMessageVo.new WXData(first, keyword1, keyword2, keyword3, keyword4, remark);
                    wxMessageVo.setData(wxData);
                    wxMessageVo.setTemplate_id(template_id);
                    wxMessageVo.setTouser(userFeign.getOpenid(userId));
                    wxPush(wxMessageVo);
                    log.info("微信报警推送成功");
                } catch (Exception wxe) {
                    log.info("微信推送失败,{}", wxe);
                    wxe.printStackTrace();
                }
                try {
//                    if (null != authUserBo) {
                        Map pushMap = new HashMap();
                        pushMap.put("msgType", "2");
                        pushMap.put("faultType", String.valueOf(e.getEvent()));
                        jPushClientExample.sendMsg(Arrays.asList(String.valueOf(userId)), deviceAlias + "发生" + FaultTitleEnum.getTitle(e.getEvent()) + ",请尽快处理", FaultTitleEnum.getTitle(e.getEvent()),
                                userAlarm.getMsgContent(), JpushTypeEnum.NOTIFICATION.value(), pushMap, 5); //userType:5 个人
                    log.info("报警极光推送成功");
//                    }
                } catch (Exception wxe) {
                    log.info("极光推送失败");
                    wxe.printStackTrace();
                }

            }
        }

    }

    @Override
    public void init(List<Long> userIds, Integer projectId, String address, String projectName) {
        if (null == userIds || userIds.size() == 0) {
            throw new AlarmException("用户组不能为空");
        }
        if (null == projectId || projectId < 0) {
            throw new AlarmException("项目ID不能为空 或者小于0");
        }
        this.userIds = userIds;
        this.projectId = projectId;
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
