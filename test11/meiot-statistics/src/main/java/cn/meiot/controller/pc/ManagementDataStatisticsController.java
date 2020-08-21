package cn.meiot.controller.pc;

import cn.meiot.aop.Log;
import cn.meiot.config.TableConfig;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SerialNumberMasterVo;
import cn.meiot.service.IPcManagementDataStatisticsService;
import cn.meiot.service.PcStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @ClassName: ManagementDataStatisticsController
 * @Description: 管理平台首页数据统计
 * @author: 符纯涛
 * @date: 2019/9/20
 */
@RestController
@RequestMapping("/pc/Management")
@Slf4j
@SuppressWarnings("all")
public class ManagementDataStatisticsController extends BaseController{

    @Autowired
    private IPcManagementDataStatisticsService pcManagementDataStatisticsService;


    @Autowired
    private PcStatisticsService pcStatisticsService;


    private Calendar calendar = Calendar.getInstance();
    /**
     *
     * @Title: selectDataStatistics
     * @Description: 首页数据统计
     * @param
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "/selectDataStatistics",method = RequestMethod.GET)
    @Log(operateContent = "管理平台首页数据统计",operateModule = "统计服务")
    public Result selectDataStatistics(){
       return pcManagementDataStatisticsService.selectDataStatistics();
    }

    /**
     *
     * @Title: selectPcDataAll
     * @Description: 根据项目统计企业设备的数据
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "/selectPcDataAll", method = RequestMethod.GET)
    @Log(operateContent = "项目企业设备的数据统计",operateModule = "统计服务")
    public Result selectPcDataAll(Long startTime,Integer type){
        Integer projectId = getProjectId();
        return pcManagementDataStatisticsService.selectPcDataAll(projectId, startTime,type);
    }


    /**
     *
     * @Title: selectPcMonthMeter
     * @Description: 统计近12月的用电峰谷
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "/selectPcMonthMeter", method = RequestMethod.GET)
    @Log(operateContent = "项目企业设备的统计近12月的用电峰谷",operateModule = "统计服务")
    public Result selectPcMonthMeter(){
        Integer projectId = getProjectId();
        return pcManagementDataStatisticsService.selectPcMonthMeter(projectId);
    }

    /**
     *
     * @Title: selectPcMonthMeter
     * @Description: 统计项目企业设备的能效
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "/selectPcEnergy", method = RequestMethod.GET)
    @Log(operateContent = "统计项目企业设备的能效",operateModule = "统计服务")
    public Result selectPcEnergy(){
        Integer projectId = getProjectId();
        return pcManagementDataStatisticsService.selectPcEnergy(projectId);
    }

    /**
     *
     * @Title: selectPcPlatformMeter
     * @Description: 企业平台的首页
     * @param
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "/selectPcPlatformMeter", method = RequestMethod.GET)
    @Log(operateContent = "企业平台的首页",operateModule = "统计服务")
    public Result selectPcPlatformMeter(ParametersDto parametersDto){
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);//获取年份
        parametersDto.setYears(year);
        int month = calendar.get(Calendar.MONTH) + 1;//获取月份
        parametersDto.setMonths(month);
        int day = calendar.get(Calendar.DATE);//获取日


        Integer projectId = getProjectId();
        parametersDto.setTableName(TableConfig.METER);
        parametersDto.setProjectId(projectId);
        parametersDto.setFunction(TableConfig.SUM);
        parametersDto.setType(0);
        parametersDto.setTime(System.currentTimeMillis());
//        List<SerialNumberMasterVo> list = pcManagementDataStatisticsService.selectDataAllByNumber(projectId, year,
//                1, 1, year, month, day, 0);
//        parametersDto.setSwitchSnList(list);
        List<Map<String,Object>> queryStatistics = pcStatisticsService.queryStatistics(parametersDto);
        Map map = new HashMap();
        map.put("data", queryStatistics);
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(map);
        return defaultTrue;
    }












}
