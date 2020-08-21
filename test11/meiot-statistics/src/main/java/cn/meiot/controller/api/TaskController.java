package cn.meiot.controller.api;

import cn.meiot.aop.Log;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IAppMeterMonthsService;
import cn.meiot.service.IAppMeterYearsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供给定时任务调用的controller
 */
@RestController
@Slf4j
@RequestMapping("/task")
public class TaskController {

    private IAppMeterMonthsService appMeterMonthsService;

    private IAppMeterYearsService appMeterYearsService;

    public TaskController(IAppMeterMonthsService appMeterMonthsService,IAppMeterYearsService appMeterYearsService){

        this.appMeterMonthsService = appMeterMonthsService;
        this.appMeterYearsService = appMeterYearsService;
    }

    /**
     * 将昨天的电流总数统计出来存放到月度表中
     * @return
     */
    /*@RequestMapping(value = "/dayStatistics",method = RequestMethod.GET)
    @Log(operateContent = "将昨天的电流总数统计出来存放到月度表中")
    public Result dayStatistics(){

        return appMeterMonthsService.dayStatistics();

    }*/

    /**
     * 将上个月的电流总数统计出来存放到年度表中
     * @return
     */
   /* @RequestMapping(value = "/monthStatistics",method = RequestMethod.GET)
    @Log(operateContent = "将上个月的电流总数统计出来存放到年度表中")
    public Result monthStatistics(){

        return appMeterYearsService.monthStatistics();
    }*/


}
