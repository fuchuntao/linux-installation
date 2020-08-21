package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.YearAndMonth;
import cn.meiot.enums.AccountType;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IPcDeviceStatisticsService;
import cn.meiot.utils.DateUtil;
import cn.meiot.utils.StatisticsCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * <p>
 * 设备数据统计表 前端控制器
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@RestController
@RequestMapping("/pc-device")
@Slf4j
public class PcDeviceStatisticsController extends BaseController {

    @Autowired
    private IPcDeviceStatisticsService pcDeviceStatisticsService;


    @Autowired
    private UserFeign userFeign;
    /**
     * 统计设备信息
     * @return
     */
    @GetMapping(value = "queryDeviceInfo")
    @Log(operateContent = "查询设备以及负载率信息",operateModule = "统计服务")
    public Result queryDeviceInfo(){
        Result result = pcDeviceStatisticsService.queryDeviceInfo(getUserId(), getProjectId());
    /*    if(result.isResult()){
            Map<String,Object> map = (Map<String, Object>) result.getData();
            map.put("deivceSum",(int)map.get("deivceSum")+ FalseDataUtil.DEIVCE_SUM);
            map.put("onLineDevice",(int)map.get("onLineDevice")+ FalseDataUtil.ONLINE_DEVICE);
        }*/
        return result;
    }


    /**
     * 获取设备在线率
     * @param userId   用户id
     * @param projectId   项目id ，项目id为空或者0时表示个人
     * @return
     */
    @RequestMapping(value = "/getDeviceLine",method = RequestMethod.GET)
    @Log(operateContent = "获取设备在线率",operateModule = "统计服务")
    public BigDecimal getDeviceLine(@RequestParam("userId") Long userId,@RequestParam(value = "projectId",required = false) Integer projectId ){

        return pcDeviceStatisticsService.getDeviceLine(userId,projectId);
    }


    /**
     * 设备用电top(个人)
     * @param startTime 查询的时间
     * @param type   类型   年：0月：1日：2
     * @return
     */
    @GetMapping(value = "/perMeterTop")
    @Log(operateContent = "设备用电top(个人)",operateModule = "统计服务")
    public Result perMeterTop(@RequestParam(value = "startTime",required = false) Long startTime,
                           @RequestParam(value = "type",required = false) Integer type){
        YearAndMonth date = getDate(startTime, type);
        //获取当前用户id
        Long userId = getUserId();
        //判断用户是否企业类型(1 运营 2 企业 3 代理商 4 维修 5 个人)
        Integer userType = getUserType(userId);
        if(AccountType.PERSONAGE.value().equals(userType)) {
            return pcDeviceStatisticsService.meterTop(getUserId(),date.getYear(),date.getMonth(),null) ;
        }else if(AccountType.ENTERPRISE.value().equals(userType)) {
            log.info("设备用电top(个人)获取项目id：{}, 当前用户id:{}",getProjectId(), getUserId());
            return pcDeviceStatisticsService.meterTop(getUserId(),date.getYear(),date.getMonth(),getProjectId()) ;
        }else {
            throw new MyServiceException(StatisticsCodeUtil.USER_TYPE_ERROR);
        }

    }



    private YearAndMonth getDate(Long startTime, Integer type ){
        YearAndMonth y = new YearAndMonth();
        Integer year =  null ;
        if(null == startTime){
            year = DateUtil.getYear();
            y.setYear(year);
            return y;
        }
        y = DateUtil.getYearByTimestamp(startTime);
        if(type == 0){
            y.setMonth(null);
        }
        return y;
    }


    /**
     * 设备用电top(企业)
     * @param startTime
     * @param type
     * @return
     */
//    @GetMapping(value = "/entMeterTop")
//    @Log(operateContent = "设备用电top(企业)",operateModule = "统计服务")
//    public Result entMeterTop(@RequestParam(value = "startTime",required = false) Long startTime,
//                              @RequestParam(value = "type",required = false) Integer type){
//        YearAndMonth date = getDate(startTime, type);
//        return pcDeviceStatisticsService.meterTop(getUserId(),date.getYear(),date.getMonth(),getProjectId()) ;
//    }


    /**
     * 年度用电量统计（个人版）
     * @param startTime
     * @return
     */
    @GetMapping(value = "/meterToYear")
    @Log(operateContent = "年度用电量统计（个人版）",operateModule = "统计服务")
    public Result meterToYear(@RequestParam(value = "startTime",required = false) Long startTime){
        YearAndMonth date = getDate(startTime, 0);
        log.info("获取到的年月信息：{}",date);
//        Long rtuserId = 10000986L;
        //获取当前用户id
        Long userId = getUserId();
        //判断用户是否企业类型(1 运营 2 企业 3 代理商 4 维修 5 个人)
        Integer userType = getUserType(userId);
        if(AccountType.PERSONAGE.value().equals(userType)) {
            return pcDeviceStatisticsService.meterToYear(getUserId(),date.getYear(),0);
        }else if(AccountType.ENTERPRISE.value().equals(userType)) {
            Integer projectId = getProjectId();
            //获取主账号id
            Long mainUserId = userFeign.getMainUserId(userId);
            log.info("年度用电量统计（个人版）获取项目id：{}, 主用户id:{}",projectId, mainUserId);
            if(mainUserId == null) {
                log.info("获取企业当前主账号用户 mainUserId 为空!!!" );
                return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(),ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
            }
            return pcDeviceStatisticsService.meterToYear(mainUserId,date.getYear(),projectId);
        }else {
            throw new MyServiceException(StatisticsCodeUtil.USER_TYPE_ERROR);
        }

    }

}
