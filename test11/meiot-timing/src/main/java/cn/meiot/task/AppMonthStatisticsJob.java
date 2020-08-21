package cn.meiot.task;

import cn.meiot.entity.vo.Result;
import cn.meiot.feign.MeiotStatisticsFeign;
import cn.meiot.utils.FeignUtil;
import cn.meiot.utils.QueueConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppMonthStatisticsJob implements Job {

    @Autowired
    private MeiotStatisticsFeign meiotStatisticsFeign;

    @Autowired
    private FeignUtil feignUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("任务执行了，任务名称：AppMonthStatisticsJob");
//        Result result = meiotStatisticsFeign.dayStatistics();
//        feignUtil.chechResult(result,QueueConstantUtil.STATISTICS_DAY_QUEUE);
        //个人用电量拉取月的数据到年表中
        rabbitTemplate.convertAndSend(QueueConstantUtil.STATISTICS_DAY_QUEUE,2);

        //企业用电量拉取月的数据到年表中
        rabbitTemplate.convertAndSend(QueueConstantUtil.STATISTICS_DAY_QUEUE_PC,2);
        log.info("统计完成");
    }
}
