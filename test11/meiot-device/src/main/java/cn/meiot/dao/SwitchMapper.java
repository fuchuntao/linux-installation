package cn.meiot.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.meiot.entity.db.Switch;
import cn.meiot.entity.db.SwitchName;
import cn.meiot.entity.dto.SwitchRespDto;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.examination.SerialDto;
import cn.meiot.entity.dto.pc.examination.SwitchDto;
import cn.meiot.entity.dto.sw.SendSwitch;
import cn.meiot.entity.vo.SerialNumberMasterVo;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author lingzhiying
 * @title: SwitchMapper.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月19日
 */
public interface SwitchMapper extends BaseMapper<Switch>{

	/**
	 * 
	 * @param userId 
	 * @param serialNumber
	 * @return
	 */
	List<SwitchRespDto> querySwitchIndexByNumber(@Param("userId")Long userId, @Param("serialNumber")String serialNumber, @Param("deleted") Integer deleted);

	/**
	 * 查询该设备有多少开关
	 * @param serialNumber
	 * @return
	 */
	List<Switch> selectCountSerialNumber(String serialNumber);
	
	/**
	 * 查询设备的开关
	 * @param serialNumber
	 * @return
	 */
	List<SendSwitch> querySwitchBySerialNumber(String serialNumber);

	/**
	 * 查询主开关
	 * @param serialNumber
	 * @return
	 */
	Integer getMasterIndex(String serialNumber);

	/**
	 * 获取主开关编号
	 * @param serialNumber
	 * @return
	 */
	String getMasterSn(String serialNumber);

	/**
	 * 根据BuildingId查询设备
	 * @param id
	 * @return
	 */
	List<Switch> querySwitchByBuildingId(Long id);

	/**
	 * 根据设备获取全部开关并且按开关号倒排
	 * @param serialNumber
	 * @return
	 */
	List<SwitchName> querySwitch(String serialNumber);

	/**
	 * 查询设备下所有开关
	 * @param serialNumber
	 * @return
	 */
	List<SendSwitch> querySerialNumber(String serialNumber);

	List<SerialDto> querySerialNumberAndSwitch(PcEquipmentUserCond cond);

	/**
	 * 根据用户 项目 开关号查询  关联id
	 * @param userId
	 * @param projectId
	 * @param switchSn
	 * @return
	 */
	Long querySwitchJurisdiction(Long userId, Integer projectId, String switchSn);

	/**
	 * 通过设备查询开关
	 * @param serialNumbers
	 * @return
	 */
	List<SerialNumberMasterVo> queryMasterIndexBySerialNUmber(@Param("list")List<String> serialNumbers);

	/**
	 * 通过设备查询开关
	 * @param long1 
	 * @param serialNumbers
	 * @return
	 */
	List<SwitchDto> querySwitchBySerial(@Param("serialNumber")String serial, @Param("userId")Long userId);

	/**
	 * 通过building查询设备名和主开关
	 * @param buildingId
	 * @param projectId
	 * @return
	 */
	List<Map> querySwitchByBuilding(@Param("buildingId")Long buildingId,@Param("projectId") Integer projectId);


	/**
	 * 通过设备
	 * @param userId
	 * @param serialNumber
	 * @return
	 */
	List<Map> querySwitchBySerialAndUserId(Long userId, String serialNumber);

	/**
	 * 查询设备的全部开关
	 * @param serialNumber
	 * @return
	 */
	List<SendSwitch> querySwitchAllBySerialNumber(String serialNumber);

	/**
	 * 根据设备和开关型号查询数据
	 * @param projectId
	 * @param mode
	 * @return
	 */
	List<SerialDto> querySerialSwtichByProjectMode(@Param("projectId") Integer projectId, @Param("mode") String mode);

	/**
	 * 设置为子开关
	 * @param serialNumber
	 */
    void updateSonSwitch(@Param("serialNumber") String serialNumber, @Param("switchSn") String switchSn);

	/**
	 * 设置为子开关
	 * @param serialNumber
	 */
	void updateMainSwitch(@Param("serialNumber") String serialNumber, @Param("switchSn") String switchSn);
}
