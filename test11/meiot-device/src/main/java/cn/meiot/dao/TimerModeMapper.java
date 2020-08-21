package cn.meiot.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.meiot.entity.db.PowerAppUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import cn.meiot.entity.db.TimerMode;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author lingzhiying
 * @title: TimerModeMapper.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月4日
 */
@Mapper
public interface TimerModeMapper extends BaseMapper<TimerMode>{

	List<TimerMode> queryTimerMode(String switchSn);

	/**
	 * 关掉所有开关状态
	 * @param switchSn
	 */
	void updateIsSwitch(String switchSn);

	//List<ExaminationBuildingDto> queryBuilding(@Param("userId")Long userId,@Param("projectId") Integer projectId);

    List<Map> querySn(@Param("switchSn") String switchSn);


	/**
	 * 通过id查询开关号
	 * @param id
	 * @return
	 */
	Set<PowerAppUser> querySwitchById(Long id);

	/**
	 * 通过设备查询定时信息
	 * @param serialNumber
	 * @return
	 */
    List<TimerMode> querySerial(String serialNumber);

	/**
	 * 修改正常线路为失效线路
	 * @param powerAppUserList
	 * @param ids
	 */
	void updateInvalidSwitch(@Param("powerAppUserList") Set<PowerAppUser> powerAppUserList, @Param("ids") List<Long> ids);

	/**
	 * 根据设备查询相应状态的 id
	 * @param serialNumber
	 * @param status
	 * @return
	 */
	List<Long> queryIdBySerial(@Param("serialNumber") String serialNumber, @Param("status") int status);

	/**
	 * 删除指定id的开关
	 * @param id
	 * @param powerAppUserList
	 */
	void deleteByIdAndSwitch(@Param("id") Long id, @Param("powerAppUserList") Set<PowerAppUser> powerAppUserList);
}
