package cn.meiot.service;

import java.util.List;
import java.util.Map;

import cn.meiot.entity.dto.UpdateName;
import cn.meiot.entity.dto.sw.SendSwitch;
import cn.meiot.entity.vo.Result;

/**
 * @author lingzhiying
 * @title: SwitchService.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月3日
 */
public interface SwitchNameService {
	
	/**
	 * 给用户添加默认设备名
	 * @param serialNumber
	 * @param userId
	 */
	void insertSwitchName(String serialNumber,Long userId,Integer projectId);
	
	/**
	 * 解绑删除默认设备名
	 * @param serialNumber
	 * @param userId
	 */
	void deleteSwitchName(String serialNumber,Long userId);

	/**
	 * 查询该用户的开关名以及开关类型
	 * @param serialNumber
	 * @param mainUserId
	 * @return
	 */
    List<Map> querySwitchAll(String serialNumber, Long mainUserId);
}
