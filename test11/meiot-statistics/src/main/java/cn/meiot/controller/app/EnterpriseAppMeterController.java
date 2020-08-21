package cn.meiot.controller.app;

import cn.meiot.aop.Log;
import cn.meiot.config.TableConfig;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.PcCurrentHours;
import cn.meiot.entity.PcLeakageHours;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatisticsDto;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IAppMeterHoursService;
import cn.meiot.service.IAppMeterYearsService;
import cn.meiot.service.IPcCurrentHoursService;
import cn.meiot.service.IPcLeakageHoursService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.VaildUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

/**
 * @ClassName: EnterpriseAppMeterController
 * @Description: 企业版app数据电量统计
 * @author: 符纯涛
 * @date: 2020/2/18 0018
 */
@RestController
@RequestMapping("/app/enterprise")
@Slf4j
public class EnterpriseAppMeterController extends BaseController {

    @Autowired
    private IAppMeterYearsService appMeterYearsService;

    @Autowired
    private IAppMeterHoursService appMeterHoursService;

    @Autowired
    private IPcLeakageHoursService pcLeakageHoursService;

    @Autowired
    private IPcCurrentHoursService pcCurrentHoursService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private UserFeign userFeign;


    /**
     * @param
     * @Title: getInformation
     * @Description: 运维报告基本信息
     * @return: cn.meiot.entity.vo.Result
     */
//    @RequestMapping(value = "information", method = RequestMethod.GET)
//    @Log(operateContent = "企业版获取运维报告基本信息",operateModule = "统计服务")
    public Result getInformation() {
        Long userId = getUserId();
        Integer projectId = getProjectId();
        Long rtuserId = userFeign.getMainUserId(userId);

        if (rtuserId == null) {
            log.info("获取企业当前主账号用户 rtuserId 为空!!!");
            return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(), ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
        }

        Result information = appMeterYearsService.getInformation(rtuserId, projectId);
        return information;

    }

    /**
     * @param startTime
     * @param type      年 月 日 （0 1 2）
     * @Title: getMonthlyMeter
     * @Description: 企业版app的每月用电量(电量趋势)
     * @return: cn.meiot.entity.vo.Result
     */
//    @RequestMapping(value = "getMonthlyMeter", method = RequestMethod.GET)
//    @Log(operateContent = "企业版app的每月用电量",operateModule = "统计服务")
    public Result getMonthlyMeter(@RequestParam("startTime") Long startTime,
                                  @RequestParam("type") Integer type) {

//        Long userId = 10000995L;
//        Integer projectId = 23;

        Long userId = getUserId();
        Integer projectId = getProjectId();
        Long rtuserId = userFeign.getMainUserId(userId);

        if (rtuserId == null) {
            log.info("获取企业当前主账号用户 rtuserId 为空!!!");
            return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(), ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
        }


        Result information = appMeterYearsService.getMonthlyMeter(rtuserId, projectId, startTime, type);
        return information;

    }


    /**
     * @param startTime
     * @param type
     * @Title: getPeakValleyMeter
     * @Description: 企业版app年度用电谷峰
     * @return: cn.meiot.entity.vo.Result
     */
//    @RequestMapping(value = "getPeakValleyMeter", method = RequestMethod.GET)
//    @Log(operateContent = "企业版app年度用电谷峰",operateModule = "统计服务")
    public Result getPeakValleyMeter(@RequestParam("startTime") Long startTime,
                                     @RequestParam("type") Integer type) {

//        Long userId = 10000145L;
//        Integer projectId = 24;

        Long userId = getUserId();
        Integer projectId = getProjectId();
        Long rtuserId = userFeign.getMainUserId(userId);

        if (rtuserId == null) {
            log.info("获取企业当前主账号用户 rtuserId 为空!!!");
            return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(), ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
        }

        Result information = appMeterYearsService.getPeakValleyMeter(rtuserId, projectId, startTime, type);
        return information;

    }


    /**
     * 企业版数据报告的年月日
     *
     * @param serialNumber 设备序列号
     * @param switchSn     开关序列号SN
     * @param time         时间戳
     * @param type         0:年 1:月 2:日
     * @return
     */
    @RequestMapping(value = "meterList", method = RequestMethod.GET)
    @Log(operateContent = "企业版数据报告的年月日", operateModule = "统计服务")
    public Result meterList(@RequestParam("serialNumber") String serialNumber, @RequestParam("switchSn") Long switchSn,
                            @RequestParam("time") Long time, @RequestParam("type") Integer type) {
        Result result = VaildUtil.checkParam(serialNumber, switchSn);
        if (!result.isResult()) {
            return result;
        } else {
            result = Result.getDefaultFalse();
        }
        if (null == time) {
            result.setMsg("请选择完整的时间");
            return result;
        }

        if (null == type) {
            result.setMsg("请选择查询时间类型");
            return result;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        int day = cal.get(Calendar.DATE);//获取日

        //通过用户id获取主账户id = 10000002
//        Long rtuserId = 10000980L;
//        Long rtuserId = commonUtil.getRtUserIdByUserId(getUserId(),serialNumber);

        //获取主账号id
        Long rtuserId = userFeign.getMainUserId(getUserId());

        if (null == rtuserId) {
            return Result.faild(ResultCodeEnum.STATISTICS_MAIN_UNBIND.getCode(), ResultCodeEnum.STATISTICS_MAIN_UNBIND.getMsg());
        }

        //获取项目id
//        Integer projectId = 38;
        Integer projectId = getProjectId();
        if (null == projectId) {
            result.setMsg("企业app数据报告未获取到的项目id");
            return result;
        }

        AppMeterVo appMeterVo = AppMeterVo.builder()
                .serialNumber(serialNumber)
                .switchSn(switchSn)
                .userId(rtuserId)
                .year(year)
                .build();

        ParametersDto parametersDto = new ParametersDto();
        //个人的小时
        parametersDto.setTableName(TableConfig.METER);
        parametersDto.setProjectId(projectId);
        parametersDto.setSYear(year);

        //月
        if (type == 1) {
            appMeterVo.setMonth(month);
            parametersDto.setSMonth(month);
            //日
        } else if (type == 2) {
            appMeterVo.setMonth(month);
            appMeterVo.setDay(day);
            parametersDto.setSMonth(month);
            parametersDto.setSDay(day);
        }
        parametersDto.setType(type);
        Result listApp = appMeterYearsService.getListApp(parametersDto, appMeterVo);
        return listApp;

    }


    /**
     * 获取当前设备的总用电量
     *
     * @param serialNumber
     * @return
     */
    @RequestMapping(value = "/totalMeter", method = RequestMethod.GET)
    @Log(operateContent = "获取当前设备的总用电量", operateModule = "统计服务")
    public Result totalMeter(@RequestParam("serialNumber") String serialNumber) {
        //通过用户id获取主账户id
        Long rtuserId = userFeign.getMainUserId(getUserId());
//        Long rtuserId = 10000121L;
        //获取项目id
        Integer projectId = getProjectId();
//        Integer projectId = 23;

        if (null == projectId) {
            Result result = Result.getDefaultFalse();
            result.setMsg("企业app数据报告未获取到的项目id");
            return result;
        }
        if (null == rtuserId) {
            return Result.faild(ResultCodeEnum.STATISTICS_MAIN_UNBIND.getCode(), ResultCodeEnum.STATISTICS_MAIN_UNBIND.getMsg());
        }
        return appMeterHoursService.gettotalMeterBySerialNumber(rtuserId, serialNumber, projectId);

    }


    /**
     * @param serialNumber
     * @param time
     * @Title: leakageData
     * @Description: 企业首页中的近12个小时的电流
     * @return: cn.meiot.entity.vo.Result
     */
    @GetMapping(value = "/leakageData")
    @Log(operateContent = "企业首页中的近12个小时的电流", operateModule = "统计服务")
    public Result leakageData(@RequestParam("serialNumber") String serialNumber,
                              @RequestParam("time") Long time) {
        if (StringUtils.isEmpty(serialNumber)) {
            log.info("设备序列号不能为空");
            return new Result().Faild("设备序列号不能为空");
        }
        if (null == time) {
            log.info("查询进12个小时的电流，时间戳为空");
            return new Result().Faild("查询进12个小时的电流，时间戳不能为空！！！");
        }
        //获取主开关编号sn
        Long switchSn = commonUtil.getMasterSn(serialNumber);
        if (null == switchSn) {
            log.info("主开关编号为空");
            return new Result().Faild("主开关编号不能为空！！！");
        }

        //通过用户id获取主账户id
//        Long rtuserId = 10000121L;
        Long rtuserId = userFeign.getMainUserId(getUserId());
        log.info("企业主账户id：{}", rtuserId);
        if (null == rtuserId) {
            return Result.faild(ResultCodeEnum.STATISTICS_MAIN_UNBIND.getCode(), ResultCodeEnum.STATISTICS_MAIN_UNBIND.getMsg());
        }
//        Integer projectId = 23;
        Integer projectId = getProjectId();
        if (null == projectId) {
            return new Result().Faild("企业首页中的近12个小时的电流未获取到的项目id");
        }
        //近12个小时的开始时间戳
        Long startTime = time - 43200000;
        PcCurrentHours pcCurrentHours = PcCurrentHours.builder()
                .projectId(Long.valueOf(projectId))
                .serialNumber(serialNumber)
                .switchSn(switchSn)
                .userId(rtuserId)
                .build();
        return pcCurrentHoursService.currentData(pcCurrentHours,startTime, time);

    }

}
