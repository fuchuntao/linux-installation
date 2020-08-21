package cn.meiot.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.meiot.entity.db.Equipment;
import cn.meiot.entity.dto.EquipmentRespDto;
import cn.meiot.entity.dto.TimingExamination;
import cn.meiot.entity.dto.pc.equipment.EquipmentPcDto;
import cn.meiot.entity.dto.pc.equipmentUser.EquipmentUserInsert;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author lingzhiying
 * @title: EquipmentMapper.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月16日
 */
public interface EquipmentMapper extends BaseMapper<Equipment>{ 

	EquipmentRespDto querySerialNumber(@Param("serialNumber")String serialNumber,@Param("userId")Long userId);

	/**
	 * 修改设备状态
	 * @param status
	 * @param serialNumber 
	 */
	void updateEquipmentStatus(@Param("status")Integer status, @Param("serialNumber")String serialNumber);

	/**
	 * 查询自检信息
	 * @return
	 */
	List<TimingExamination> queryExamination(String time);

	/**
	 * 查询自检信息
	 * @return
	 */
	TimingExamination queryExaminationBySerial(@Param("serialNumber") String serialNumber);

	/**
	 * 查找building的设备和设备名称
	 * @param id
	 * @param mainUserId
	 * @param listRole 
	 * @return
	 */
	List<EquipmentPcDto> queryBuilding(@Param("id")Long id, @Param("userId")Long mainUserId, @Param("listRole")List<Integer> listRole);

	/**
	 * 修改设备building
	 * @param equipmentUserInsert
	 * @return
	 */
	int updateBuildingIdBySn(EquipmentUserInsert equipmentUserInsert);


	/**
	 *
	 * @Title: allExaminationByMonth
	 * @Description: 设备近一年增长量
	 * @param year
	 * @param startMonth
	 * @param endMonth
	 * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
	 */
	List<Map<String, Object>> allExaminationByMonth(@Param("year") int year,
													@Param("startMonth") int startMonth,
													@Param("endMonth") int endMonth);


	/**
	 * 通过开关号查询设备号名字等
	 * @param oldSwitchSn
	 * @return
	 */
    Map queryEquipmentBySwitch(Long oldSwitchSn);

	/**
	 * 通过设备号查询所属公司
	 * @param serialNumber
	 * @return
	 */
	Integer queryCompanyBySerialNumber(String serialNumber);

	/**
	 * 通过组织架构查询所有
	 * @param ids
	 * @return
	 */
    List<Map> querySerialByBuildingIds(@Param("list") List<Long> ids);

	/**
	 * 批量设置漏电自检
	 * @param serialList
	 * @param examinationStatus
	 * @param examinationTime
	 */
	void batchExamination(@Param("serialList") List<String> serialList, @Param("examinationStatus") Integer examinationStatus, @Param("examinationTime") String examinationTime);

	/**
	 * 通过组织架构查询第一个设备号
	 * @param ids
	 * @return
	 */
    String findOneSerialByBuildingIds(@Param("ids") List<Long> ids);

	/**
	 * 查询第几代产品
	 * @param serialNumber
	 * @return
	 */
	Integer queryAgreementVersion(String serialNumber);

	/**
	 * 查询第几代产品
	 * @param switchSn
	 * @return
	 */
    Integer queryAgreementVersionBySwitchSn(String switchSn);

	/**
	 * 查询二代漏电自检
	 * @return
	 */
	List<TimingExamination> queryExamination2(String time);

	/**
	 * 查询二代漏电自检
	 * @return
	 */
	List<TimingExamination> queryExamination2BySerial(String time);

	/**
	 * 查询设备数量
	 * @param longs
	 * @return
	 */
	List<String> listSerialByBuildingIds(@Param("list") List<Long> longs);
}
