package cn.meiot.receive;

import cn.meiot.entity.vo.SmsVo;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.SmsService;
import cn.meiot.utils.QueueConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsMq {

    private SmsService smsService;

    public SmsMq(SmsService smsService){
        this.smsService = smsService;
    }

    @RabbitListener(queues = QueueConstantUtil.SEND_SMS_MSG)
    public void sendSms(SmsVo smsVo) throws Exception {
        //发送短信验证码
        try {
            smsService.sendSms(smsVo);
            log.info("短信发送完成，账号信息：{}",smsVo);
        }catch (Exception e){
            e.printStackTrace();
            log.info(e.getMessage());
            log.info("发生错误在：{}",e.getStackTrace());
        }

    }

    @RabbitListener(queues = QueueConstantUtil.DEL_SMS_CODE)
    public void delCode(SmsVo smsVo) {
        //发送短信验证码
        smsService.delCode(smsVo);
        log.info("验证码已删除：{}",smsVo);
    }
}
