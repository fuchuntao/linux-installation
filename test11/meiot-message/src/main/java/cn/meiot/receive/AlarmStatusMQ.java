package cn.meiot.receive;

import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.EnterpriseUserFaultMsgAlarm;
import cn.meiot.entity.vo.SendStatusVo;
import cn.meiot.service.IAppUserFaultMsgAlarmService;
import cn.meiot.service.IEnterpriseUserFaultMsgAlarmService;
import cn.meiot.utils.QueueConstantUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Package cn.meiot.receive
 * @Description:
 * @author: 武有
 * @date: 2020/4/20 11:41
 * @Copyright: www.spacecg.cn
 */
@Slf4j
@Component
public class AlarmStatusMQ {
    @Autowired
    private IAppUserFaultMsgAlarmService appUserFaultMsgAlarmService;
    @Autowired
    private IEnterpriseUserFaultMsgAlarmService enterpriseUserFaultMsgAlarmService;

    @RabbitListener(queues = QueueConstantUtil.APP_SYNCHRONIZE_STATIC)
    public void alarmStatus(String jsonString){
        try {
            log.info("接收到队列{}的消息 内容为：{}",QueueConstantUtil.APP_SYNCHRONIZE_STATIC,jsonString);
            SendStatusVo sendStatusVo = JSONObject.parseObject(jsonString, SendStatusVo.class);
            AppUserFaultMsgAlarm appUserFaultMsgAlarm=AppUserFaultMsgAlarm.builder()
                    .id(sendStatusVo.getAlarmId())
                    .status(sendStatusVo.getType()).build();
            appUserFaultMsgAlarmService.updateById(appUserFaultMsgAlarm);
            log.info("故障消息状态同步成功===>：{}",appUserFaultMsgAlarm);
        }catch (Exception e){
            log.info("故障消息状态同步出错===>：{}",e);
        }

    }

    @RabbitListener(queues = QueueConstantUtil.QY_SYNCHRONIZE_STATIC)
    public void qyalarmStatus(String jsonString){
        try {
            log.info("接收到队列{}的消息 内容为：{}",QueueConstantUtil.APP_SYNCHRONIZE_STATIC,jsonString);
            SendStatusVo sendStatusVo = JSONObject.parseObject(jsonString, SendStatusVo.class);
            EnterpriseUserFaultMsgAlarm enterpriseUserFaultMsgAlarm=EnterpriseUserFaultMsgAlarm.builder()
                    .id(sendStatusVo.getAlarmId()).switchStatus(sendStatusVo.getType()).build();
            enterpriseUserFaultMsgAlarmService.updateById(enterpriseUserFaultMsgAlarm);
            log.info("故障消息状态同步成功===>：{}",enterpriseUserFaultMsgAlarm);
        }catch (Exception e){
            log.info("故障消息状态同步出错===>：{}",e);
        }

    }
}
