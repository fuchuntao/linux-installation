package cn.meiot.client.hystrix;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.meiot.client.StatisticsClient;
import cn.meiot.entity.vo.Result;

@Component
public class StatisticsHystrix implements StatisticsClient {


	@Override
	public Result listDevice(String serialNumber, Long switchIndex, Integer year, Long userId) {
		return Result.getDefaultFalse();
	}

	@Override
	public Map<String, BigDecimal> queryDayAndMonth(String serialNumber, String switchSn,Integer switchIndex) {
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		map.put("day", BigDecimal.ZERO);
		map.put("month", BigDecimal.ZERO);
		return map;
	}

	@Override
	public Map<String, Object> pcMonthAndDayStatistics(String serialNumber, Long masterIndex, Integer projectId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("day", 0);
		map.put("month", 0);
		return map;
	}
}
