package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IPcMeterYearsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 企业平台年电量统计 前端控制器
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@RestController
@RequestMapping("/pc-meter-years")
public class PcMeterYearsController extends BaseController {

    @Autowired
    private IPcMeterYearsService pcMeterYearsService;

   /**
    *
    * @Title: pullMonthStatisticsPc
    * @Description: 手动拉取pc端数据
    * @param year
    * @param month
    * @return: cn.meiot.entity.vo.Result
    */
    @RequestMapping(value = "pullMonthStatisticsPc", method = RequestMethod.GET)
    @Log(operateContent = "手动拉取企业的月的数据到年表中",operateModule = "统计服务")
    public Result pullMonthStatisticsPc(int year, int month){

        return pcMeterYearsService.pullMonthStatisticsPc(year,month);
    }


    /**
     * 获取当前与去年每个月的用电量信息
     * @return
     */
    @GetMapping(value = "/queryYearData")
    @Log(operateContent = "查询用电综合管理", operateModule = "统计服务")
    public Result queryYearData(){
        return pcMeterYearsService.queryYearData(getProjectId());

    }



}
