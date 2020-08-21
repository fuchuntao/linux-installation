package cn.meiot.task;

import cn.meiot.feign.DeviceFeign;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TimingExaminationJob implements Job {

    @Autowired
    private DeviceFeign deviceFeign;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("请求设备接口开始");
        deviceFeign.timingExamination();
        log.info("请求已发出！！！！！！！！");

    }
}
