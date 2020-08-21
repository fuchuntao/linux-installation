package cn.meiot.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.meiot.entity.db.RoleEquipment;
import cn.meiot.entity.dto.pc.role.RoleDto;
import tk.mybatis.mapper.common.BaseMapper;

public interface RoleEquipmentMapper extends BaseMapper<RoleEquipment>{

	void insertRoleEquipment(RoleDto roleDto);

	/**
	 * 根据角色删除
	 * @param roleId
	 * @param projectId 
	 */
	void deleteRoleId(@Param("roleId")Integer roleId, @Param("projectId")Integer projectId);

	/**
	 * 
	 * @param roleId
	 * @return
	 */
	List<String> querySerialByRoleId(Integer roleId);

	void insertBuildingEquipment(@Param("buildingId")Long buildingId,@Param("serialNumber") String serialNumber,@Param("projectId") Integer projectId);

	/**
	 * 根据设备删除
	 * @param oldSerialNumber
	 */
	void deleteSerialnumber(String oldSerialNumber);

	/**
	 * 通过设备查询角色
	 * @param serialNumber
	 * @return
	 */
	List<Integer> queryRoleIdBySerial(String serialNumber);

	/**
	 * 
	 * @param listRole
	 * @param serialNumber
	 * @return
	 */
	Long queryBySerialAndRoleId(@Param("listRole")List<Integer> listRole,@Param("serialNumber") String serialNumber);


}
