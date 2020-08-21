package cn.meiot.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MsgReceive {

    @RabbitListener(queues = "saveMsg")
    public void saveMsg(String msg){
        log.info("已收到消息，内容："+msg);
    }
}
