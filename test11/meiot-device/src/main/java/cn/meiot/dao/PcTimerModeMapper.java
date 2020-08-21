package cn.meiot.dao;

import java.util.List;
import java.util.Map;

import cn.meiot.entity.dto.pc.examination.PcExamination;
import org.apache.ibatis.annotations.Param;

import cn.meiot.entity.db.PcTimerMode;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.examination.ExaminationBuildingDto;
import tk.mybatis.mapper.common.BaseMapper;

public interface PcTimerModeMapper extends BaseMapper<PcTimerMode>{

	List<PcTimerMode> queryPage(PcEquipmentUserCond cond);


	//List<ExaminationBuildingDto> queryBuilding(Long userId, Integer projectId);

	List<Long> queryTimeBuilding(Long id);

	List<Long> queryTimeEquiment(Long id);

	List<String> queryTimeSwitch(Long id);

	List<ExaminationBuildingDto> queryBuilding(Long userId, Integer projectId);

	void insertBuilding(@Param("id")Long id, @Param("list") List<Long> listBuilding, @Param("table") String table);

	void insertEquiment(@Param("id")Long id, @Param("list")List<Long> listEquiment, @Param("table") String table);

	void insertSwitch(@Param("id")Long id, @Param("list")List<String> listSwitch, @Param("table") String table);


	/**
	 * 
	 * @param cond
	 * @return
	 */
	List<PcTimerMode> querySwitch(PcEquipmentUserCond cond);


	/**
	 * 
	 * @param pcTimerMode
	 * @return
	 */
	int insertPc(PcTimerMode pcTimerMode);


	Long selectId();


	/**
	 * 查找id里面的父节点
	 * @param listBuilding
	 * @return
	 */
	List<Long> selectPid(@Param("list")List<Long> listBuilding);


	List<Map> querySwitchList(PcEquipmentUserCond cond);

	/**
	 * 根据定时开关或者定时功率查询设备和开关数据
	 * @param id
	 * @return
	 */
    List<PcExamination> queryEquipment(@Param("id") Long id, @Param("userId") Long userId);

	/**
	 * 查询不等于id的开关号
	 * @param id
	 * @return
	 */
	List<String> queryNoTimeSwitch(Long id,Integer projectId);

	/**
	 * 查询开关定时的数量
	 * @param switchSn
	 * @return
	 */
	List<Integer> querySwitchNum(@Param("switchSn") String switchSn, @Param("flag") Integer flag);

	/**
	 * 添加开关定时
	 * @param switchSn
	 * @param i
	 * @param id
	 */
	void insertSwitchNum(@Param("switchSn") String switchSn, @Param("num") Integer num, @Param("id") Long id);

	/**
	 * 查询旧开关的id及对应的数字
	 * @param id
	 * @return
	 */
	List<Map<String,Object>> queryTimeSwitchById(Long id);
}
