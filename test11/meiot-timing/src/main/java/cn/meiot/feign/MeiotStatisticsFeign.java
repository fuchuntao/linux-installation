package cn.meiot.feign;


import cn.meiot.entity.vo.Result;
import cn.meiot.feign.impl.MeiotStatisticsHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "meiot-statistics", fallback = MeiotStatisticsHystrix.class)
public interface MeiotStatisticsFeign {


    /**
     * 将昨天的电流总数统计出来存放到月度表中
     *
     * @return
     */
    @RequestMapping(value = "/task/dayStatistics", method = RequestMethod.GET)
    Result dayStatistics();


    /**
     * 将上个月的电流总数统计出来存放到年度表中
     *
     * @return
     */
    @RequestMapping(value = "/task/monthStatistics", method = RequestMethod.GET)
    Result monthStatistics();
}
