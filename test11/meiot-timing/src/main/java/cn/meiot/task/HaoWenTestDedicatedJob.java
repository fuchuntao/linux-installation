package cn.meiot.task;

import cn.meiot.feign.DeviceFeign;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HaoWenTestDedicatedJob implements Job {


    @Autowired
    private DeviceFeign deviceFeign;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("浩文专用测试，执行开始");

        deviceFeign.haowenTest();

        log.info("浩文专用测试，执行完毕");

    }
}
