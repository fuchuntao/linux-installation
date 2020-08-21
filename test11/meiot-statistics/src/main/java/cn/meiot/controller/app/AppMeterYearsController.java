package cn.meiot.controller.app;


import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.AccountType;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IAppMeterYearsService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.StatisticsCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-19
 */
@RestController
@RequestMapping("/app/meter-years")
@Slf4j
public class AppMeterYearsController extends BaseController {

    @Autowired
    private IAppMeterYearsService appMeterYearsService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private UserFeign userFeign;


    @Autowired
    private EnterpriseAppMeterController enterpriseAppMeterController;






//    /**
//     * 获取开关的月数据统计列表
//     * @param serialNumber 设备序列号
//     * @param switchSn  开关序列号SN
//     * @return
//     */
//    @RequestMapping(value = "list",method = RequestMethod.GET)
//    @Log(operateContent = "获取开关的月数据统计列表",operateModule = "统计服务")
//    public Result list(@RequestParam("serialNumber") String serialNumber, @RequestParam("switchSn") Long switchSn,
//                       @RequestParam("year")Integer year) {
//        Result result = VaildUtil.checkParam(serialNumber,switchSn);
//        if(!result.isResult()){
//            return result;
//        }else{
//            result = Result.getDefaultFalse();
//        }
//        if (null == year) {
//            result.setMsg("请选择完整的时间");
//            return result;
//        }
//        //通过用户id获取主账户id
//        Long rtuserId = 10000002L;
////        Long rtuserId = commonUtil.getRtUserIdByUserId(getUserId(),serialNumber);
////        if(null == rtuserId){
////            result.setMsg("未获取到此账户的主账户id");
////            return result;
////        }
//        AppMeterVo appMeterVo = AppMeterVo.builder()
//                .serialNumber(serialNumber)
//                .switchSn(switchSn)
//                .userId(rtuserId)
//                .year(year)
//                .build();
//
//        ParametersDto parametersDto = new ParametersDto();
//        //个人的小时
//        parametersDto.setTableName(TableConfig.METER);
//        parametersDto.setProjectId(0);
//        parametersDto.setSYear(year);
//        parametersDto.setType(0);
//        Result listApp = appMeterYearsService.getListApp(parametersDto, appMeterVo);
//        return  listApp;
//    }

    /**
     * 获取开关的月数据统计列表
     * @param serialNumber 设备序列号
     * @param switchSn  开关序列号
     * @return
     */
    @RequestMapping(value = "listDevice",method = RequestMethod.GET)
    public Result listDevice(@RequestParam("serialNumber") String serialNumber, @RequestParam("switchSn") Long switchSn,
                       @RequestParam("year")Integer year,@RequestParam("userId")Long userId) {
        //通过用户id获取主账户id
        Long rtuserId = commonUtil.getRtUserIdByUserId(userId,serialNumber);
        AppMeterVo appMeterVo = AppMeterVo.builder()
                .serialNumber(serialNumber)
                .switchSn(switchSn)
                .userId(rtuserId)
                .year(year)
                .build();
        return appMeterYearsService.getList(appMeterVo);
    }

    /**
     * 首页中的电量统计
     * @param serialNumber 设备序列号
     * @param type 类型  1：6个月之内    2：一年之内
     * @return
     */
    @GetMapping(value = "/batteryLeft")
	@Log(operateContent = "企业版首页中的近6个月的电量统计",operateModule = "统计服务")
    public Result batteryLeft(@RequestParam("serialNumber") String serialNumber,@RequestParam("type") Integer type){
        if(StringUtils.isEmpty(serialNumber)){
            log.info("设备序列号不能为空");
            return new Result().Faild("设备序列号不能为空");
        }
        if(null == type){
            log.info("请选择需要查询的类型");
            return new Result().Faild("请选择需要查询的类型");
        }
        if(type != 1){
            log.info("请选择正确的类型");
            return new Result().Faild("请选择正确的类型");
        }
        //通过用户id获取主账户id
//        Long rtuserId = 10000980L;
//        Long rtuserId = commonUtil.getRtUserIdByUserId(getUserId(),serialNumber);
        Long rtuserId = userFeign.getMainUserId(getUserId());
        log.info("企业主账户id：{}",rtuserId);
        if(null == rtuserId){
           return Result.faild(ResultCodeEnum.STATISTICS_MAIN_UNBIND.getCode(),ResultCodeEnum.STATISTICS_MAIN_UNBIND.getMsg());
        }

//        Integer projectId = 24;
        Integer projectId = getProjectId();
        if(null == projectId){
            return new Result().Faild("企业app首页近6个月的电量未获取到的项目id");
        }
        return appMeterYearsService.batteryLeft(serialNumber,rtuserId,type,projectId);

    }

    /**
     *
     * @Title: pullAppMonthStatistics
     * @Description: 手动拉取app的月的数据
     * @param year
     * @param month
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "pullAppMonthStatistics", method = RequestMethod.GET)
    @Log(operateContent = "手动拉取app的月的数据",operateModule = "统计服务")
    public Result pullAppMonthStatistics(int year, int month){

        return appMeterYearsService.pullAppMonthStatistics(year,month);
    }



    /**
     *
     * @Title: list
     * @Description: 获取个人app当月的电量数据统计
     * @param serialNumber
     * @param startTime
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "nowMonthMeter", method = RequestMethod.GET)
    @Log(operateContent = "获取主开关当月电量数据",operateModule = "统计服务")
    public Result list(@RequestParam("serialNumber") String serialNumber,
                       @RequestParam("startTime")Long startTime) {
        log.info("获取主开关当月电量数据,getUserId():{}",getUserId());
//        Result result = VaildUtil.checkParam(serialNumber, switchSn);
//        if (!result.isResult()) {
//            return result;
//        } else {
//            result = Result.getDefaultFalse();
//        }
        if(StringUtils.isBlank(serialNumber)) {
            Result result = Result.getDefaultFalse();
            result.setMsg("设备号为空");
            return result;
        }
        if (null == startTime) {
            Result result = Result.getDefaultFalse();
            result.setMsg("请选择完整的时间");
            return result;
        }
        //开始时间
        Calendar calStartDataUtil = Calendar.getInstance();
        calStartDataUtil.setTimeInMillis(startTime);
        //开始时间的年
        int year = calStartDataUtil.get(Calendar.YEAR);
        //月
        int month = calStartDataUtil.get(Calendar.MONTH) + 1;
        log.info("获取主开关当月电量数据,year:{},month:{},serialNumber:{},startTime:{}",year,month,serialNumber,startTime);
        //通过用户id获取主账户id
        Long rtuserId = commonUtil.getRtUserIdByUserId(getUserId(),serialNumber);
        log.info("获取主开关当月电量数据,rtuserId:{}",rtuserId);
//        Long rtuserId = 10000005L;
        if(null == rtuserId){
            return Result.faild(ResultCodeEnum.STATISTICS_MAIN_UNBIND.getCode(),ResultCodeEnum.STATISTICS_MAIN_UNBIND.getMsg());
        }
        //通过设备序列号查询你主开关编号
        Long masterSn = commonUtil.getMasterSn(serialNumber);
        AppMeterVo appMeterVo = AppMeterVo.builder()
                .serialNumber(serialNumber)
                .switchSn(masterSn)
                .userId(rtuserId)
                .year(year)
                .month(month)
                .build();
        return appMeterYearsService.getNowMonth(appMeterVo);
    }


    /**
     *
     * @Title: getInformation
     * @Description: 运维报告基本信息
     * @param
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "information", method = RequestMethod.GET)
    @Log(operateContent = "获取运维报告基本信息",operateModule = "统计服务")
    public Result getInformation() {
        //获取当前用户id
        Long userId = getUserId();
        //判断用户是否企业类型(1 运营 2 企业 3 代理商 4 维修 5 个人)
        Integer userType = getUserType(userId);
        log.info("用户类型：{}", userType);
        if(AccountType.PERSONAGE.value().equals(userType)) {
            Result information = appMeterYearsService.getInformation(userId, 0);
            return information;
        }else if(AccountType.ENTERPRISE.value().equals(userType)) {
            return enterpriseAppMeterController.getInformation();
        }else {
            throw new MyServiceException(StatisticsCodeUtil.USER_TYPE_ERROR);
        }

    }

    /**
     *
     * @Title: getMonthlyMeter
     * @Description: 个人app的每月用电量(电量趋势)
     * @param startTime
     * @param type 年 月 日 （0 1 2）
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "getMonthlyMeter", method = RequestMethod.GET)
    @Log(operateContent = "个人app的每月用电量",operateModule = "统计服务")
    public Result getMonthlyMeter(@RequestParam("startTime")Long startTime,
                                  @RequestParam("type") Integer type) {
//        log.info("9999999999999999");
//        Long rtuserId = 10000986L;
        //获取当前用户id
        Long userId = getUserId();
        //判断用户是否企业类型(1 运营 2 企业 3 代理商 4 维修 5 个人)
        Integer userType = getUserType(userId);
        if(AccountType.PERSONAGE.value().equals(userType)) {
            Result information = appMeterYearsService.getMonthlyMeter(getUserId(), 0, startTime,type);
            return information;
        }else if(AccountType.ENTERPRISE.value().equals(userType)) {
            return enterpriseAppMeterController.getMonthlyMeter(startTime,type);
        }else {
            throw new MyServiceException(StatisticsCodeUtil.USER_TYPE_ERROR);
        }
    }




    /**
     *
     * @Title: getPeakValleyMeter
     * @Description: 个人app年度用电谷峰
     * @param startTime
     * @param type
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "getPeakValleyMeter", method = RequestMethod.GET)
    @Log(operateContent = "个人app年度用电谷峰",operateModule = "统计服务")
    public Result getPeakValleyMeter(@RequestParam("startTime")Long startTime,
                                  @RequestParam("type") Integer type) {
//        Long rtuserId = 10000986L;
        //获取当前用户id
        Long userId = getUserId();
        //判断用户是否企业类型(1 运营 2 企业 3 代理商 4 维修 5 个人)
        Integer userType = getUserType(userId);
        if(AccountType.PERSONAGE.value().equals(userType)) {
            Result information = appMeterYearsService.getPeakValleyMeter(getUserId(), 0, startTime,type);
            return information;
        }else if(AccountType.ENTERPRISE.value().equals(userType)) {
            return enterpriseAppMeterController.getPeakValleyMeter(startTime, type);
        }else {
            throw new MyServiceException(StatisticsCodeUtil.USER_TYPE_ERROR);
        }
    }





}
