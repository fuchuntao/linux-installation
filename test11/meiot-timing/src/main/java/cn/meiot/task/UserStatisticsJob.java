package cn.meiot.task;

import cn.meiot.feign.MeiotStatisticsFeign;
import cn.meiot.utils.FeignUtil;
import cn.meiot.utils.QueueConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserStatisticsJob implements Job {

    @Autowired
    private MeiotStatisticsFeign meiotStatisticsFeign;

    @Autowired
    private FeignUtil feignUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("任务执行了，任务名称：UserStatisticsJob");
//        Result result = meiotStatisticsFeign.dayStatistics();
//        feignUtil.chechResult(result,QueueConstantUtil.STATISTICS_DAY_QUEUE);
        //拉取月的数据到用户表中
        rabbitTemplate.convertAndSend(QueueConstantUtil.STATISTICS_USER_QUEUE);
        log.info("统计完成");
    }
}
