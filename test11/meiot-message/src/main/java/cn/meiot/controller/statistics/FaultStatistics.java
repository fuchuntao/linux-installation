package cn.meiot.controller.statistics;

import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatisticsEventTimeVo;
import cn.meiot.service.IFaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Package cn.meiot.controller.statistics
 * @Description:
 * @author: 武有
 * @date: 2019/10/21 10:06
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("/fault-statistics")
public class FaultStatistics extends BaseController {
    @Autowired
    private IFaultMessageService faultMessageService;

//    @RequestMapping("StatisticsTemperatureAlarm")
//    public Result getStatisticsTemperatureAlarm(@RequestParam("time") String time,
//                                                @RequestParam("event") Integer event){
//        List<StatisticsEventTimeVo> countByEventAndTime = faultMessageService.getCountByEventAndTime(time, event, getUserId(), getProjectId());
//        supplementaryData(countByEventAndTime);
//        Result result=Result.getDefaultTrue();
//        result.setData(countByEventAndTime);
//        return result;
//    }


}
