package cn.meiot.service;

import java.util.List;

import cn.meiot.entity.db.RoleBuilding;
import cn.meiot.entity.dto.pc.BuildingRespDto;
import cn.meiot.entity.dto.pc.role.RoleDto;
import cn.meiot.entity.vo.Result;

public interface RoleService {

	Result insert(RoleDto roleDto);

	Result delete(RoleBuilding RoleBuilding);

	Result update(RoleDto roleDto);

	List<Long> load(BuildingRespDto buildingRespDto);

	/**
	 * 根据设备查询角色
	 * @param roleId
	 * @return
	 */
	List<String> querySerialByRoleId(Integer roleId);

	List<Integer> queryRoleIdBySerial(String serialNumber);

	Result batchUpdate(List<RoleDto> roleDto);

	/**
	 * 查询该角色拥有的组织架构
	 * @param projectId
	 * @param roleId
	 * @return
	 */
	List<Long> queryBuilding(Integer projectId,List<Integer> roleId);

	/**
	 * 查询该角色拥有的组织架构
	 * @param projectId
	 * @param roleId
	 * @return
	 */
	List<String> queryEquipment(Integer projectId,List<Integer> roleId);

	/**
	 * 查询该项目下的组织架构
	 */
	List<Long> queryBuildingIdByProjectId(Integer projectId);

	/**
	 * 查询该项目下的设备
	 */
	List<String> querySerialNumberByProjectId(Integer projectId);

	/**
	 * 查询该项目下的设备id
	 */
	List<Long> queryEquipmentUserByProjectId(Integer projectId);
}

