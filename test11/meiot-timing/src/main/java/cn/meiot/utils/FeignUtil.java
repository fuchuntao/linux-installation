package cn.meiot.utils;

import cn.meiot.entity.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignUtil {

    private RabbitTemplate rabbitTemplate;

    public FeignUtil(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate =rabbitTemplate;
    }

    /**
     * 发送消息
     * @param result
     * @param queueName
     */
    public  void chechResult(Result result, String queueName){

        if(result.isResult()){
            log.info("统计成功！！！！！");
            return ;
        }
        log.info("失败原因：{}",result.getMsg());
        if(result.getCode().equals("-2")){
            log.info("发送消息进行统计");
            rabbitTemplate.convertAndSend(queueName);
        }else{
            // TODO 将失败的时间记录到数据库
        }

    }
}
