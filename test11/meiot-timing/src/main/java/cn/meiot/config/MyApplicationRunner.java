package cn.meiot.config;

import cn.meiot.entity.vo.TaskInfo;
import cn.meiot.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 项目启动时执行
 */
@Component
@Slf4j
public class MyApplicationRunner implements ApplicationRunner {

    private TaskService taskService;

    public MyApplicationRunner(TaskService taskService){
        this.taskService = taskService;
    }

    /**
     * 需要创建的定时任务
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开启定时任务   统计每日电量");
        TaskInfo appDayStatisticsJob = TaskInfo.builder()
                .jobName("AppDayStatisticsJob")
                .jobGroup("AppDayStatisticsJob")
                .cronExpression("0 0 1 1/1 * ?*")//每天凌晨1点执行一次
                .jobDescription("统计每天的电量使用量")
                .jobStatus("1")
                .isSatrtExecute(1)
                .build();
        taskService.addJob(appDayStatisticsJob);
        log.info("开启定时任务成功     统计每日电量");

        log.info("开启定时任务    统计每月电量");
        TaskInfo appMonthStatisticsJob = TaskInfo.builder()
                .jobName("AppMonthStatisticsJob")
                .jobGroup("AppMonthStatisticsJob")
                .cronExpression("0 0 3 1 * ?")//每个月3点执行一次
                .jobDescription("统计每月的电量使用量")
                .jobStatus("1")
                .isSatrtExecute(1)
                .build();
        taskService.addJob(appMonthStatisticsJob);
        log.info("开启定时任务成功   统计每月电量");


        log.info("开启定时任务   漏电自检");
        TaskInfo timingExaminationJob = TaskInfo.builder()
                .jobName("TimingExaminationJob")
                .jobGroup("TimingExaminationJob")
                .cronExpression("0 0 0/1 * * ?")//每小时执行一次
                .jobDescription("定时漏电自检")
                .jobStatus("1")
                .isSatrtExecute(1)
                .build();
        taskService.addJob(timingExaminationJob);
        log.info("开启定时任务成功   漏电自检");

        log.info("开启定时任务   删除无用token信息");
        TaskInfo delUserTokenJob = TaskInfo.builder()
                .jobName("DelUserTokenJob")
                .jobGroup("DelUserTokenJob")
                .cronExpression("0 0 0/1 * * ?")//每小时执行一次
                .jobDescription("删除无用token信息")
                .jobStatus("1")
                .isSatrtExecute(1)
                .build();
        taskService.addJob(delUserTokenJob);
        log.info("开启定时任务成功   删除无用token信息");
        /*TaskInfo userStatisticsJob = TaskInfo.builder()
                .jobName("UserStatisticsJob")
                .jobGroup("UserStatisticsJob")
                .cronExpression("0 0 0 1 * ?")//每个月0点0分执行一次
                .jobDescription("定时拉取上一个月的用户数据")
                .jobStatus("1")
                .isSatrtExecute(1)
                .build();
        taskService.addJob(userStatisticsJob);*/

        log.info("开启定时任务    统计丢失的电量");
        TaskInfo loseMeterJob = TaskInfo.builder()
                .jobName("LoseMeterJob")
                .jobGroup("LoseMeterJob")
                .cronExpression("0 30 * * * ?")//每个天的一个小时的30分执行一次
                .jobDescription("统计丢失的电量")
                .jobStatus("1")
                .isSatrtExecute(1)
                .build();
        taskService.addJob(loseMeterJob);
        log.info("开启定时任务成功   统计丢失的电量");

        log.info("开启定时任务   浩文测试专用");
        TaskInfo haoWenTestDedicatedJob = TaskInfo.builder()
                .jobName("HaoWenTestDedicatedJob")
                .jobGroup("HaoWenTestDedicatedJob")
                .cronExpression("0 0/5 * * * ? ")//每个天的一个小时的30分执行一次
                .jobDescription("浩文测试专用")
                .jobStatus("1")
                .isSatrtExecute(1)
                .build();
        taskService.addJob(haoWenTestDedicatedJob);
        log.info("开启定时任务成功   浩文测试专用");



        log.info("开启定时任务   拉取水表数据");
        TaskInfo pullWaterMeterDataJob = TaskInfo.builder()
                .jobName("PullWaterMeterDataJob")
                .jobGroup("PullWaterMeterDataJob")
                .cronExpression("0 0 0 1/1 * ?")//每个天的一个小时的30分执行一次
                .jobDescription("拉取水表数据")
                .jobStatus("1")
                .isSatrtExecute(1)
                .build();
        taskService.addJob(pullWaterMeterDataJob);
        log.info("开启定时任务成功   拉取水表数据");


    }
}
