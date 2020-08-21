package cn.meiot.controller.app;


import cn.meiot.aop.Log;
import cn.meiot.config.TableConfig;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IAppMeterMonthsService;
import cn.meiot.service.IAppMeterYearsService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.VaildUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-19
 */
@RestController
@RequestMapping("/app/meter-months")
public class AppMeterMonthsController extends BaseController {

    @Autowired
    private IAppMeterMonthsService appMeterMonthsService;

    @Autowired
    private IAppMeterYearsService appMeterYearsService;

    @Autowired
    private CommonUtil commonUtil;


//    /**
//     * 获取开关的月数据统计列表
//     *
//     * @param serialNumber 设备序列号
//     * @param switchSn  开关序列号sn
//     * @return
//     */
//    @RequestMapping(value = "list", method = RequestMethod.GET)
//    @Log(operateContent = "获取开关的月数据统计列表",operateModule = "统计服务")
//    public Result list( @RequestParam("serialNumber") String serialNumber, @RequestParam("switchSn") Long switchSn,
//                       @RequestParam("year") Integer year, @RequestParam("month") Integer month) {
//        Result result = VaildUtil.checkParam(serialNumber, switchSn);
//        if (!result.isResult()) {
//            return result;
//        } else {
//            result = Result.getDefaultFalse();
//        }
//        if (null == year || null == month) {
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
//                .month(month)
//                .build();
//
//
//        ParametersDto parametersDto = new ParametersDto();
//        //个人的天
//        parametersDto.setTableName(TableConfig.METER);
//        parametersDto.setProjectId(0);
//        parametersDto.setSYear(year);
//        parametersDto.setSMonth(month);
//        parametersDto.setType(1);
//        Result listApp = appMeterYearsService.getListApp(parametersDto, appMeterVo);
//        return  listApp;
//    }

    /**
     *
     * @Title: pullAppDayStatistics
     * @Description: 手动拉取数据
     * @param year
     * @param month
     * @param day
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "pullAppDayStatistics",method = RequestMethod.GET)
    @Log(operateContent = "手动拉取数据天的数据到月数据统计列表",operateModule = "统计服务")
    public Result pullAppDayStatistics(int year, int month, int day){
        return appMeterMonthsService.pullAppDayStatistics(year, month, day);
    }

}
