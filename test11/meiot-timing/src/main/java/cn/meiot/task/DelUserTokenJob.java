package cn.meiot.task;

import cn.meiot.utils.RedisConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DelUserTokenJob implements Job {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public void execute(JobExecutionContext context){
        log.info("删除多余token信息开始");
        rabbitTemplate.convertAndSend(RedisConstantUtil.DEL_USER_TOKEN,"");
    }
}
