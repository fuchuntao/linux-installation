package cn.meiot.service;

import java.util.List;
import java.util.Map;

import cn.meiot.entity.vo.DeviceVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;

public interface PcStatisticsService {

	List<Map<String, Object>> queryStatistics(ParametersDto parametersDto);

	List<Map<String, Object>> queryStatisticsWek(ParametersDto parametersDto);
	
	/**
	 * 
	 * @Title: updateMeterChangeSwitch  
	 * @Description: 更换开关统计电量
	 * @param deviceVo    
	 * @return: void     
	 */
	void updateMeterChangeSwitch(DeviceVo deviceVo);
	
	
	/**
	 * 
	 * @Title: updateleakageChangeSwitch  
	 * @Description: 更换开关统计电流
	 * @param deviceVo    
	 * @return: void     
	 */
	void updateleakageChangeSwitch(DeviceVo deviceVo);
	
	
	/**
	 * 
	 * @Title: updateTempChangeSwitch  
	 * @Description: 更换开关统计温度
	 * @param deviceVo    
	 * @return: void     
	 */
	void updateTempChangeSwitch(DeviceVo deviceVo);



	/**
	 *
	 * @Title: updateTempChangeSwitch
	 * @Description: 更换开关统计负载
	 * @param deviceVo
	 * @return: void
	 */
	void updatePowerChangeSwitch(DeviceVo deviceVo);


}
