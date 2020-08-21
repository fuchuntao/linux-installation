package cn.meiot.task;

import cn.meiot.entity.vo.Result;
import cn.meiot.feign.MeiotMessageFeign;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RepetitionSendJob implements Job {

    @Autowired
    private MeiotMessageFeign meiotMessageFeign;

    /*public RepetitionSendJob(MeiotMessageFeign meiotMessageFeign){
        this.meiotMessageFeign = meiotMessageFeign;
    }*/

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("任务执行了，任务名称：RepetitionSendJob");
        Result result = meiotMessageFeign.repetitionSend();
        log.info("返回结果：{}",result.getMsg());
    }
}
