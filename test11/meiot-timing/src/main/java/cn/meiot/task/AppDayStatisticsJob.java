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
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppDayStatisticsJob implements Job {

    @Autowired
    private MeiotStatisticsFeign meiotStatisticsFeign;
    @Autowired
    private FeignUtil feignUtil;


    @Autowired
    private RabbitTemplate rabbitTemplate;
   /* public AppDayStatisticsJob(MeiotStatisticsFeign meiotStatisticsFeign,FeignUtil feignUtil){
        this.meiotStatisticsFeign = meiotStatisticsFeign;
        this.feignUtil = feignUtil;
    }*/

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("任务执行了，任务名称：AppDayStatisticsJob");
//        Result result = meiotStatisticsFeign.dayStatistics();
//        log.info("返回结果：{}",result);
//        feignUtil.chechResult(result,QueueConstantUtil.STATISTICS_DAY_QUEUE);
        //个人用户拉取天的数据到月表中
        rabbitTemplate.convertAndSend(QueueConstantUtil.STATISTICS_DAY_QUEUE,1);
        //企业用户拉取天的数据到月表中
        rabbitTemplate.convertAndSend(QueueConstantUtil.STATISTICS_DAY_QUEUE_PC,1);
        log.info("统计完成");

    }
}
