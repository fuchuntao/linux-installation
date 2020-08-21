package cn.meiot.controller.app;


import cn.meiot.aop.Log;
import cn.meiot.config.TableConfig;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.service.IAppMeterHoursService;
import cn.meiot.service.IAppMeterYearsService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.DataUtil;
import cn.meiot.utils.MeterUtil;
import cn.meiot.utils.VaildUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-16
 */
@RestController
@RequestMapping("/app/meter-hours")
public class AppMeterHoursController extends BaseController {

    @Autowired
    private IAppMeterHoursService appMeterHoursService;

    @Autowired
    private IAppMeterYearsService appMeterYearsService;

    @Autowired
    private CommonUtil commonUtil;




    /**
     * 个人版数据报告的年月日
     * @param serialNumber 设备序列号
     * @param switchSn  开关序列号SN
     * @param time  时间戳
     * @param type  0:年 1:月 2:日
     * @return
     */
    @RequestMapping(value = "list",method = RequestMethod.GET)
    @Log(operateContent = "个人版数据报告的年月日",operateModule = "统计服务")
    public Result list(@RequestParam("serialNumber") String serialNumber, @RequestParam("switchSn") Long switchSn,
                       @RequestParam("time")Long time, @RequestParam("type")Integer type){
        Result result = VaildUtil.checkParam(serialNumber,switchSn);
        if(!result.isResult()){
            return result;
        }else{
            result = Result.getDefaultFalse();
        }
        if(null == time){
            result.setMsg("请选择完整的时间");
            return result;
        }

        if(null == type) {
            result.setMsg("请选择查询时间类型");
            return result;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        int day = cal.get(Calendar.DATE);//获取日

        //通过用户id获取主账户id = 10000002
//        Long rtuserId = 10000005L;
        Long rtuserId = commonUtil.getRtUserIdByUserId(getUserId(),serialNumber);
        if(null == rtuserId){
            return Result.faild(ResultCodeEnum.STATISTICS_MAIN_UNBIND.getCode(),ResultCodeEnum.STATISTICS_MAIN_UNBIND.getMsg());
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
        parametersDto.setProjectId(0);
        parametersDto.setSYear(year);

        //月
        if(type == 1){
            appMeterVo.setMonth(month);
            parametersDto.setSMonth(month);
            //日
        }else if(type == 2) {
            appMeterVo.setMonth(month);
            appMeterVo.setDay(day);
            parametersDto.setSMonth(month);
            parametersDto.setSDay(day);
        }
        parametersDto.setType(type);
        Result listApp = appMeterYearsService.getListApp(parametersDto, appMeterVo);
        return  listApp;

    }

    /**
     * 获取当前设备的总用电量
     * @param serialNumber
     * @return
     */
    @RequestMapping(value = "/totalMeter",method = RequestMethod.GET)
    @Log(operateContent = "获取当前设备的总用电量",operateModule = "统计服务")
    public Result totalMeter(@RequestParam("serialNumber") String serialNumber){
        //通过用户id获取主账户id
        Long rtuserId = commonUtil.getRtUserIdByUserId(getUserId(),serialNumber);
//        Long rtuserId = 10000002L;
        if(null == rtuserId){
            return Result.faild(ResultCodeEnum.STATISTICS_MAIN_UNBIND.getCode(),ResultCodeEnum.STATISTICS_MAIN_UNBIND.getMsg());
        }
        return appMeterHoursService.gettotalMeterBySerialNumber(rtuserId,serialNumber,null);

    }

}
