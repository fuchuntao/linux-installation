package cn.meiot.client;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.meiot.client.hystrix.StatisticsHystrix;
import cn.meiot.entity.vo.Result;

@FeignClient(value = "meiot-statistics",fallback = StatisticsHystrix.class)
public interface StatisticsClient {
	
	@RequestMapping(value = "/app/meter-years/listDevice",method = RequestMethod.GET)
    public Result listDevice(@RequestParam("serialNumber") String serialNumber, @RequestParam("switchSn") Long switchIndex,
                       @RequestParam("year")Integer year, @RequestParam("userId")Long userId) ;
	
	@RequestMapping(value = "/app/meter-years/listDevice",method = RequestMethod.GET)
    public Map<String,BigDecimal> queryDayAndMonth(@RequestParam("serialNumber") String serialNumber, @RequestParam("switchSn")String switchSn ,@RequestParam("switchIndex")Integer switchIndex) ;

	@RequestMapping(value ="/pc-meter-months/pcMonthAndDayStatistics",method = RequestMethod.GET)
	public Map<String, Object> pcMonthAndDayStatistics(@RequestParam("serialNumber") String serialNumber, @RequestParam("masterSn")Long masterIndex, @RequestParam("projectId")Integer projectId);
}
