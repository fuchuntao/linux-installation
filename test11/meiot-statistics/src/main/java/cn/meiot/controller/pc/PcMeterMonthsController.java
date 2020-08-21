package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IAppMeterMonthsService;
import cn.meiot.service.IPcMeterMonthsService;
import cn.meiot.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 企业平台月统计表 前端控制器
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@RestController
@RequestMapping("/pc-meter-months")
public class PcMeterMonthsController extends BaseController {

    @Autowired
    private IPcMeterMonthsService pcMeterMonthsService;

    @Autowired
    private CommonUtil commonUtil;
    /**
     *
     * @Title: pullPcDayStatistics
     * @Description: 手动拉取月表数据
     * @param year
     * @param month
     * @param day
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "pullPcDayStatistics",method = RequestMethod.GET)
    @Log(operateContent = "手动拉取企业的天的数据到月表中",operateModule = "统计服务")
    public Result pullPcDayStatistics(int year, int month, int day){
        return pcMeterMonthsService.pullPcDayStatistics(year, month, day);
    }


    /**
     *
     * @Title: pcMonthAndDayStatistics
     * @Description: 查询当月和当天的电量
     * @param serialNumber
     * @param masterSn
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "pcMonthAndDayStatistics",method = RequestMethod.GET)
    @Log(operateContent = "设备服务调用,查询当月和当天的电量",operateModule = "统计服务")
    public Map<String, Object> pcMonthAndDayStatistics(String serialNumber, Long masterSn, Integer projectId){
        return pcMeterMonthsService.pcMonthAndDayStatistics(serialNumber, masterSn, projectId);
    }


    /**
     *
     * @Title: appMonthStatistics
     * @Description: 查询企业app的当月电量
     * @param serialNumber
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     */
    @RequestMapping(value = "appMonthStatistics",method = RequestMethod.GET)
    @Log(operateContent = "查询企业app当月电量",operateModule = "统计服务")
    public Result appMonthStatistics(@RequestParam("serialNumber") String serialNumber,
                                     @RequestParam("startTime")Long startTime){
        //获取企业id
        Integer projectId = getProjectId();
        return pcMeterMonthsService.appMonthStatistics(serialNumber, projectId,startTime);
    }




}
