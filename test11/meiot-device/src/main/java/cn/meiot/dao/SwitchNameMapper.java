package cn.meiot.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.meiot.entity.db.SwitchName;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author lingzhiying
 * @title: SwitchNameMapper.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月2日
 */
public interface SwitchNameMapper extends BaseMapper<SwitchName>{

	void updateSwitchSn(@Param("oldSn")String oldSn,@Param("newSn") String newSn);

	/**
	 * 根据设备 删除开关名
	 * @param serialNumber
	 * @param userId 可为null
	 */
	void deleteSwitchName(@Param("serialNumber")String serialNumber, @Param("userId")Long userId);


    List<Map> querySwitchAll(@Param("serialNumber")String serialNumber,@Param("userId") Long mainUserId);
}
