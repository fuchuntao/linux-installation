package cn.meiot.receive;

import cn.meiot.entity.vo.EmailVo;
import cn.meiot.utils.EmailTool;
import cn.meiot.utils.QueueConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MailMq {

    @Autowired
    private EmailTool emailTool;

    @RabbitListener(queues = QueueConstantUtil.SEND_EMAIL_QUEUE)
    public void sendMail(EmailVo emailVo) throws Exception {
        log.info("发送初始密码开始，参数：{}",emailVo);
        Map<String, Object> params = new HashMap<>();
        params.put("password", emailVo.getPassword());
        params.put("accountType",emailVo.getAccountType());
        try {
            emailTool.sendmail("德微电初始密码通知",params,emailVo.getTo());
            log.info("成功发送");
        } catch (Exception e) {
            log.info("发送邮箱失败,原因:{}",e.getMessage());
            e.printStackTrace();
        }
    }
}
