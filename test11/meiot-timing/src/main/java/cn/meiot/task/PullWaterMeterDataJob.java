package cn.meiot.task;


import cn.meiot.feign.DeviceFeign;
import cn.meiot.utils.DateUtil;
import io.lettuce.core.GeoArgs;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.management.counter.Units;

import java.util.concurrent.TimeUnit;

/**
 * 定时拉取水表数据
 */
@Component
@Slf4j
public class PullWaterMeterDataJob implements Job {


    @Autowired
    private DeviceFeign deviceFeign;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("拉取水表记录的定时任务以执行==============>");
        Boolean aBoolean = deviceFeign.pullWaterMeterData();
        if(aBoolean){
            log.info("拉取水表记录的定时任务执行完毕==============>");
            return ;
        }
        //获取一个随机值
        Integer count = 0;
        //调用失败的情况下
        while (true){
            int num = DateUtil.getRandomNum(10);
            try {
                log.info("休眠时间：{}",num);
                TimeUnit.MINUTES.sleep(num);
                aBoolean = deviceFeign.pullWaterMeterData();
            } catch (InterruptedException e) {
                log.info("休眠失败");
                aBoolean = deviceFeign.pullWaterMeterData();
            }
            count++;
            if(aBoolean){
                log.info("拉取水表记录的定时任务执行完毕==============>,调用了：{}次",count);
                return ;
            }
            if(count > 3){
                log.info("拉取水表记录的定时任务执行失败==============>,调用了：{}次",count);
                return ;
            }
        }


    }
}
