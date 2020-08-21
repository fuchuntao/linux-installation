package cn.meiot.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.meiot.config.TableConfig;
import cn.meiot.entity.vo.DeviceVo;
import cn.meiot.entity.vo.SerialNumberMasterVo;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.RedisConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.PcLeakageHoursMapper;
import cn.meiot.mapper.PcLeakageMonthsMapper;
import cn.meiot.mapper.PcPowerHoursMapper;
import cn.meiot.service.PcStatisticsService;
import cn.meiot.utils.DataUtil;


@Service
@Slf4j
public class PcStatisticsServiceImpl implements PcStatisticsService{

	
	
	@Autowired
	private PcLeakageHoursMapper pcLeakageHoursMapper;
	@Autowired
	private PcLeakageMonthsMapper pcLeakageMonthsMapper;
	
	@Autowired
	private PcPowerHoursMapper pcPowerHoursMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private DataUtil dataUtil;
	
	@Override
	public List<Map<String, Object>> queryStatistics(ParametersDto parametersDto) {
		Calendar cal = Calendar.getInstance();
		Integer type = parametersDto.getType();
		cal.setTimeInMillis(parametersDto.getTime());
		int year = cal.get(Calendar.YEAR);//获取年份
		parametersDto.setYears(year);
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        parametersDto.setMonths(month);
        int day = cal.get(Calendar.DATE);//获取日
        parametersDto.setHours(day);
        cal.setTimeInMillis(System.currentTimeMillis());
        int nowYear = cal.get(Calendar.YEAR);//获取当前年份
        int nowMonth = cal.get(Calendar.MONTH) + 1;//获取当前月份
        int nowDay = cal.get(Calendar.DATE);//获取当日
		List<Map<String, Object>> listData = new ArrayList<>();
		if(TableConfig.SUM.equals(parametersDto.getFunction()) && parametersDto.getSwitchSn() == null) {
			List<SerialNumberMasterVo> switchSnList = dataUtil.getMasterIndexByProjectId(parametersDto.getProjectId(), year, null);
			if(!CollectionUtils.isEmpty(switchSnList)) {
				parametersDto.setSwitchSnList(switchSnList);
			}
			listData = pcLeakageHoursMapper.queryStatisticsSum(parametersDto);
		}else {
			listData = pcLeakageHoursMapper.queryStatistics(parametersDto);
		}
        if(!parametersDto.getTableName().equals(TableConfig.METER)) {
			if(type == 0) {
				//查询年等于当前年
				if(year == nowYear) {
					parametersDto.setMonths(nowMonth);
					Map<String, Object> map = null;
					if(TableConfig.SUM.equals(parametersDto.getFunction()) && parametersDto.getSwitchSn() == null) {
						map = pcLeakageMonthsMapper.queryStatisticsMonthsSum(parametersDto);
					}else {
						map = pcLeakageMonthsMapper.queryStatisticsMonths(parametersDto);
					}
//					Map<String, Object> map = pcLeakageMonthsMapper.queryStatisticsMonths(parametersDto);
					if(map != null) {
						map.put("name", nowMonth);
						listData.add(map);
					}
				}
			}else if(type == 1) {
				if(nowMonth == month) {
					parametersDto.setHours(nowDay);

					Map<String, Object> map = null;
					if(TableConfig.SUM.equals(parametersDto.getFunction()) && parametersDto.getSwitchSn() == null) {
						map = pcLeakageMonthsMapper.queryStatisticsMonthsSum(parametersDto);
					}else {
						map = pcLeakageMonthsMapper.queryStatisticsMonths(parametersDto);
					}
//					Map<String, Object> map = pcLeakageMonthsMapper.queryStatisticsMonths(parametersDto);
					if(map != null) {
						map.put("name", nowDay);
						listData.add(map);
					}
				}
			}
		}

        return DataUtil.toDataHour(parametersDto.getTime(), type, listData,parametersDto.getDecimal());
	}

	//获取周数据
	@Override
	public List<Map<String, Object>> queryStatisticsWek(ParametersDto parametersDto) {
		//周末时间
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(parametersDto.getTime());
		int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
  	  	if (day_of_week == 0)
  	  		day_of_week = 7;
  	  	cal.add(Calendar.DATE, -day_of_week + 7);
        int monYear = cal.get(Calendar.YEAR);//获取当前年份
        int monMonth = cal.get(Calendar.MONTH) + 1;//获取当前月份
        int monDay = cal.get(Calendar.DATE);//获取当日
        //开始时间
        //周一时间
        cal.setTimeInMillis(parametersDto.getTime());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        int sunYear = cal.get(Calendar.YEAR);//获取当前年份
        int sunMonth = cal.get(Calendar.MONTH) + 1;//获取当前月份
        int sunDay = cal.get(Calendar.DATE);//获取当日
        List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
        Integer projectId = parametersDto.getProjectId();
        //如果跨年了
        if(monYear != sunYear || monMonth != sunMonth) {
        	//添加周一到31号的数据
        	listData.addAll(pcPowerHoursMapper.queryStatisticsWek(sunYear,sunMonth,sunDay,31,projectId));
        	//添加1号到周末数据
        	listData.addAll(pcPowerHoursMapper.queryStatisticsWek(monYear,monMonth,1,monDay,projectId));
        }else {
        	listData.addAll(pcPowerHoursMapper.queryStatisticsWek(sunYear,sunMonth,sunDay,monDay,projectId));
        }
        return DataUtil.toDataWek(parametersDto.getTime(), listData);
	}



	/**
	 *
	 * @Title: updateMeterChangeSwitch
	 * @Description: 更换开关统计电量
	 * @param deviceVo
	 * @return: void
	 */
	@Override
	public void updateMeterChangeSwitch(DeviceVo deviceVo) {

		if(deviceVo != null && StringUtils.isNotBlank(deviceVo.getSerialNumber())) {
			String serialNumber = deviceVo.getSerialNumber();
			Integer userType = (Integer)redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_SERIALNUMER+serialNumber);
			if(userType == null) {
				userType = commonUtil.getRtUserTypeBySerialNumber(serialNumber);
			}
			log.info("获取项目id为：{}", userType);
			if(userType == null) {
				return;
			}
			ParametersDto parametersDto = new ParametersDto();
			parametersDto.setSerialNumber(serialNumber);
			parametersDto.setTableName(TableConfig.METER);
			parametersDto.setType(2);
			parametersDto.setProjectId(userType);
			parametersDto.setOldSwitchSn(deviceVo.getOldSwitchSn());
			parametersDto.setNewSwitchSn(deviceVo.getNewSwitchSn());
			//查询小时表
			commonUtil.updateStatistChangeSwitch(parametersDto);
			//查询天表
			parametersDto.setType(1);
			commonUtil.updateStatistChangeSwitch(parametersDto);
			//更换年表
			parametersDto.setType(0);
			commonUtil.updateStatistChangeSwitch(parametersDto);
		}

	}



	/**
	 *
	 * @Title: updateleakageChangeSwitch
	 * @Description: 更换开关统计电流
	 * @param deviceVo
	 * @return: void
	 */
	@Override
	public void updateleakageChangeSwitch(DeviceVo deviceVo) {
		if(deviceVo != null && StringUtils.isNotBlank(deviceVo.getSerialNumber())) {
			String serialNumber = deviceVo.getSerialNumber();
			Integer userType = (Integer)redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_SERIALNUMER+serialNumber);
			if(userType == null) {
				userType = commonUtil.getRtUserTypeBySerialNumber(serialNumber);
			}
			log.info("获取项目id为：{}", userType);
			if(userType == null) {
				return;
			}

			ParametersDto parametersDto = new ParametersDto();
			parametersDto.setSerialNumber(serialNumber);
			parametersDto.setTableName(TableConfig.LEAKAGE);
			parametersDto.setType(2);
			parametersDto.setProjectId(userType);
			parametersDto.setOldSwitchSn(deviceVo.getOldSwitchSn());
			parametersDto.setNewSwitchSn(deviceVo.getNewSwitchSn());
			//查询小时表
			commonUtil.updateStatistChangeSwitch(parametersDto);
			//查询天表
			parametersDto.setType(1);
			commonUtil.updateStatistChangeSwitch(parametersDto);
			//更换年表
			parametersDto.setType(0);
			commonUtil.updateStatistChangeSwitch(parametersDto);
		}
	}



	/**
	 *
	 * @Title: updateTempChangeSwitch
	 * @Description: 更换开关统计温度
	 * @param deviceVo
	 * @return: void
	 */
	@Override
	public void updateTempChangeSwitch(DeviceVo deviceVo) {
		if(deviceVo != null && StringUtils.isNotBlank(deviceVo.getSerialNumber())) {
			String serialNumber = deviceVo.getSerialNumber();
			Integer userType = (Integer)redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_SERIALNUMER+serialNumber);
			if(userType == null) {
				userType = commonUtil.getRtUserTypeBySerialNumber(serialNumber);
			}
			log.info("获取项目id为：{}", userType);
			if(userType == null) {
				return;
			}

			ParametersDto parametersDto = new ParametersDto();
			parametersDto.setSerialNumber(serialNumber);
			parametersDto.setTableName(TableConfig.TEMP);
			parametersDto.setType(2);
			parametersDto.setProjectId(userType);
			parametersDto.setOldSwitchSn(deviceVo.getOldSwitchSn());
			parametersDto.setNewSwitchSn(deviceVo.getNewSwitchSn());
			//查询小时表
			commonUtil.updateStatistChangeSwitch(parametersDto);
			//查询天表
			parametersDto.setType(1);
			commonUtil.updateStatistChangeSwitch(parametersDto);
			//更换年表
			parametersDto.setType(0);
			commonUtil.updateStatistChangeSwitch(parametersDto);
		}
	}

	/**
	 *
	 * @Title: updatePowerChangeSwitch
	 * @Description: 更换开关统计负载
	 * @param deviceVo
	 * @return: void
	 */
	@Override
	public void updatePowerChangeSwitch(DeviceVo deviceVo) {
		if(deviceVo != null && StringUtils.isNotBlank(deviceVo.getSerialNumber())) {
			String serialNumber = deviceVo.getSerialNumber();
			Integer userType = (Integer)redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_SERIALNUMER+serialNumber);
			if(userType == null) {
				userType = commonUtil.getRtUserTypeBySerialNumber(serialNumber);
			}
			log.info("获取项目id为：{}", userType);
			if(userType == null) {
				return;
			}

			ParametersDto parametersDto = new ParametersDto();
			parametersDto.setSerialNumber(serialNumber);
			parametersDto.setTableName(TableConfig.POWER);
			parametersDto.setType(2);
			parametersDto.setProjectId(userType);
			parametersDto.setOldSwitchSn(deviceVo.getOldSwitchSn());
			parametersDto.setNewSwitchSn(deviceVo.getNewSwitchSn());
			//查询小时表
			commonUtil.updateStatistChangeSwitch(parametersDto);
			//查询天表
			parametersDto.setType(1);
			commonUtil.updateStatistChangeSwitch(parametersDto);
			//更换年表
			parametersDto.setType(0);
			commonUtil.updateStatistChangeSwitch(parametersDto);
		}
	}

}
