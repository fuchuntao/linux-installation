package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.config.TableConfig;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.bo.Crcuit;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IPcCurrentHoursService;
import cn.meiot.service.IPcManagementDataStatisticsService;
import cn.meiot.service.PcStatisticsService;
import cn.meiot.utils.RedisConstantUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <p>
 * 企业平台天数据统计表 前端控制器
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21 
 */
@RestController
@RequestMapping("/pc/statistics")
public class PcStatisticsController extends BaseController{
	
	@Autowired
	private PcStatisticsService pcStatisticsService;

	@Autowired
	private IPcCurrentHoursService pcCurrentHoursService;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	
	@Autowired
	private IPcManagementDataStatisticsService pcManagementDataStatisticsService;
	
	private Calendar cal = Calendar.getInstance();


	@GetMapping("queryVoltage")
	@Log(operateContent = "查询电压数据",operateModule = "统计服务")
	public Result queryVoltage(ParametersDto parametersDto) {
		//获取当前项目id
		Integer projectId = getProjectId();
		//添加漏电流表名
		parametersDto.setTableName(TableConfig.VOLTAGE);
		parametersDto.setProjectId(projectId);

		//函数为求平均值
		parametersDto.setFunction(TableConfig.AVG);
		parametersDto.setDecimal(0);

		Map map = selectData(parametersDto);
//		Crcuit parseObject = selectWarning(parametersDto.getProjectId(), parametersDto.getSwitchSn());
//		map.put("warning", parseObject);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}


	@GetMapping("queryCurrent")
	@Log(operateContent = "查询电流数据",operateModule = "统计服务")
	public Result queryCurrent(ParametersDto parametersDto) {
		//获取当前项目id
		Integer projectId = getProjectId();
		//添加漏电流表名
		parametersDto.setTableName(TableConfig.CURRENT);
		parametersDto.setProjectId(projectId);

		//函数为求平均值
		parametersDto.setFunction(TableConfig.AVG);
		parametersDto.setDecimal(0);

		Map map = selectData(parametersDto);
		Crcuit parseObject = selectWarning(parametersDto.getProjectId(), parametersDto.getSwitchSn());
		map.put("warning", parseObject);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}



	@GetMapping("queryLeakage")
	@Log(operateContent = "查询漏电流数据",operateModule = "统计服务")
	public Result queryLeakage(ParametersDto parametersDto) {
		//获取当前项目id
		Integer projectId = getProjectId();
		//添加漏电流表名
		parametersDto.setTableName(TableConfig.LEAKAGE);
		parametersDto.setProjectId(projectId);

		//函数为求平均值
		parametersDto.setFunction(TableConfig.AVG);
		parametersDto.setDecimal(0);
		Map map = selectData(parametersDto);
		Crcuit parseObject = selectWarning(parametersDto.getProjectId(), parametersDto.getSwitchSn());
		map.put("warning", parseObject);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}
	
	@GetMapping("queryMeter")
	@Log(operateContent = "查询电量数据",operateModule = "统计服务")
	public Result queryMeter(ParametersDto parametersDto) {
		Integer projectId = getProjectId();
		//添加电量表明
		parametersDto.setTableName(TableConfig.METER);
		//设置项目id 
		parametersDto.setProjectId(getProjectId());

		//函数为求和
		parametersDto.setFunction(TableConfig.SUM);
		//如果不是通过开关号请求  则查询所有主开关设备
//		if(parametersDto.getSwitchSn() == null || parametersDto.getSwitchSn().equals(0L)) {
//			Calendar cal = Calendar.getInstance();
//			Integer type = parametersDto.getType();
//			cal.setTimeInMillis(parametersDto.getTime());
//			int year = cal.get(Calendar.YEAR);//获取年份
//			parametersDto.setYears(year);
//	        int month = cal.get(Calendar.MONTH) + 1;//获取月份
//	        parametersDto.setMonths(month);
//	        int day = cal.get(Calendar.DATE);//获取日
//	        parametersDto.setHours(day);
//	        cal.setTimeInMillis(System.currentTimeMillis());
//	        int nowYear = cal.get(Calendar.YEAR);//获取当前年份
//	        int nowMonth = cal.get(Calendar.MONTH) + 1;//获取当前月份
//	        int nowDay = cal.get(Calendar.DATE);//获取当日
//	        List<SerialNumberMasterVo> switchSnList = pcManagementDataStatisticsService.selectDataAllByNumber(projectId, year, month, day, nowYear, nowMonth, nowDay, parametersDto.getType());
//	        parametersDto.setSwitchSnList(switchSnList);
//		}
		List<Map<String,Object>> queryStatistics = pcStatisticsService.queryStatistics(parametersDto);
		Map map = new HashMap();
		map.put("data", queryStatistics);
		Crcuit parseObject = selectWarning(parametersDto.getProjectId(), parametersDto.getSwitchSn());
		map.put("warning", parseObject);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}
	
	@GetMapping("queryTemp")
	@Log(operateContent = "查询温度数据",operateModule = "统计服务")
	public Result queryTemp(ParametersDto parametersDto) {
		Integer projectId = getProjectId();
		parametersDto.setTableName(TableConfig.TEMP);
		parametersDto.setProjectId(getProjectId());

		parametersDto.setFunction(TableConfig.AVG);

		Map map = selectData(parametersDto);
		Crcuit parseObject = selectWarning(parametersDto.getProjectId(), parametersDto.getSwitchSn());
		map.put("warning", parseObject);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}
	
	@GetMapping("querySwitchTempTable")
	@Log(operateContent = "通过开关查询温度表单",operateModule = "统计服务")
	public Result querySwitchTempTable(ParametersDto parametersDto) {
		Integer projectId = getProjectId();
		parametersDto.setTableName(TableConfig.TEMP);
		parametersDto.setProjectId(getProjectId());

		parametersDto.setFunction(TableConfig.AVG);
		parametersDto.setType(2);
		parametersDto.setTime(System.currentTimeMillis());
//		List<Map<String,Object>> queryStatistics = pcStatisticsService.queryStatistics(parametersDto);
		Map map = selectData(parametersDto);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}
	
	@GetMapping("querySwitchTemp")
	@Log(operateContent = "查询开关温度数据",operateModule = "统计服务")
	public Result querySwitchTemp(ParametersDto parametersDto) {
		Integer projectId = getProjectId();
		parametersDto.setTableName(TableConfig.TEMP);
		parametersDto.setProjectId(getProjectId());

		parametersDto.setFunction(TableConfig.AVG);
//		List<Map<String,Object>> queryStatistics = pcStatisticsService.queryStatistics(parametersDto);
		Map map = selectData(parametersDto);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}
	
	@GetMapping("queryPower")
	@Log(operateContent = "查询负载数据",operateModule = "统计服务")
	public Result queryPower(ParametersDto parametersDto) {Integer projectId = getProjectId();
		parametersDto.setTableName(TableConfig.POWER);
		parametersDto.setProjectId(getProjectId());

		parametersDto.setFunction(TableConfig.AVG);
//		List<Map<String,Object>> queryStatistics = pcStatisticsService.queryStatistics(parametersDto);

		Map map = selectData(parametersDto);
		Crcuit parseObject = selectWarning(parametersDto.getProjectId(), parametersDto.getSwitchSn());
		map.put("warning", parseObject);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}
	
	@GetMapping("queryPowerDay")
	@Log(operateContent = "查询负载近两天数据",operateModule = "统计服务")
	public Result queryPowerDay(ParametersDto parametersDto) {
		Integer projectId = getProjectId();
		parametersDto.setTableName(TableConfig.POWER);
		parametersDto.setProjectId(projectId);

		parametersDto.setFunction(TableConfig.AVG);
		parametersDto.setType(2);
		parametersDto.setTime(System.currentTimeMillis());
		List<Map<String,Object>> queryStatistics = pcStatisticsService.queryStatistics(parametersDto);
		parametersDto.setTime(System.currentTimeMillis()-86400000L);
		List<Map<String,Object>> queryStatistics2 = pcStatisticsService.queryStatistics(parametersDto);
		Map map = new HashMap();
		map.put("today", queryStatistics);
		map.put("yesterday", queryStatistics2);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}
	
	@GetMapping("queryPowerYear")
	@Log(operateContent = "查询负载近两年数据",operateModule = "统计服务")
	public Result queryPowerYear(ParametersDto parametersDto) {
		Integer projectId = getProjectId();
		parametersDto.setTableName(TableConfig.POWER);
		parametersDto.setProjectId(projectId);

		parametersDto.setFunction(TableConfig.AVG);
		parametersDto.setType(0);
		parametersDto.setTime(System.currentTimeMillis());
		List<Map<String,Object>> queryStatistics = pcStatisticsService.queryStatistics(parametersDto);
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -1);
		Date y = cal.getTime();
		parametersDto.setTime(y.getTime());
		List<Map<String,Object>> queryStatistics2 = pcStatisticsService.queryStatistics(parametersDto);
		Map map = new HashMap();
		map.put("toYear", queryStatistics);
		map.put("yesterYear", queryStatistics2);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}
	
	@GetMapping("queryPowerWek")
	@Log(operateContent = "查询负载近两周数据",operateModule = "统计服务")
	public Result queryPowerWek(ParametersDto parametersDto) {
		Integer projectId = getProjectId();
		parametersDto.setTableName(TableConfig.POWER);
		parametersDto.setProjectId(projectId);

		parametersDto.setFunction(TableConfig.AVG);
		parametersDto.setTime(System.currentTimeMillis());
		List<Map<String,Object>> queryStatistics = pcStatisticsService.queryStatisticsWek(parametersDto);
		parametersDto.setTime(System.currentTimeMillis()-604800000L);
		List<Map<String,Object>> queryStatistics2 = pcStatisticsService.queryStatisticsWek(parametersDto);
		Map map = new HashMap();
		map.put("toWek", queryStatistics);
		map.put("yesterWek", queryStatistics2);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}


	//
	/**
	 *
	 * @Title: selectWarning
	 * @Description: 根据开关获取数据的警告和预警设置
	 * @param projectId
	 * @param switchSn
	 * @return: cn.meiot.entity.bo.Crcuit
	 */
	public Crcuit selectWarning(Integer projectId, Long switchSn) {
		Crcuit parseObject = JSONObject.parseObject(JSONObject.toJSONString(redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_PARAMETER + projectId)), Crcuit.class);
		if(parseObject == null) {
			parseObject = new Crcuit();
		}
		return parseObject;
	}


	/**
	 *
	 * @Title: selectData
	 * @Description: 统计数据
	 * @param parametersDto
	 * @return: java.util.Map
	 */
	public Map selectData(ParametersDto parametersDto) {
		List<Map<String,Object>> queryStatistics = null;
		if(parametersDto.getType().equals(2)) {
			queryStatistics = pcCurrentHoursService.queryStatisticsByDay(parametersDto);
		}else {
			queryStatistics = pcStatisticsService.queryStatistics(parametersDto);
		}

		Map map = new HashMap();
		map.put("data", queryStatistics);
		return map;
	}

}
