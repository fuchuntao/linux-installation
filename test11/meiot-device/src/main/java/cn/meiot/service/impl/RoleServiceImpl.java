package cn.meiot.service.impl;

import java.util.List;

import cn.meiot.dao.BuildingMapper;
import cn.meiot.dao.EquipmentUserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.meiot.dao.RoleEquipmentMapper;
import cn.meiot.dao.RoleMapper;
import cn.meiot.entity.db.RoleBuilding;
import cn.meiot.entity.dto.pc.BuildingRespDto;
import cn.meiot.entity.dto.pc.role.RoleDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService{
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private RoleEquipmentMapper roleEquipmentMapper;

	@Autowired
	private BuildingMapper buildingMapper;

	@Autowired
	private EquipmentUserMapper equipmentUserMapper;

	@Override
	public Result insert(RoleDto roleDto) {
		//roleMapper.insertRoleDto(roleDto);
		return Result.getDefaultTrue();
	}

	@Override
	public Result delete(RoleBuilding roleBuilding) {
		roleMapper.delete(roleBuilding);
		//先删除
		roleEquipmentMapper.deleteRoleId(roleBuilding.getRoleId(),null);
		return Result.getDefaultTrue();
	}


	@Override
	public List<Long> load(BuildingRespDto buildingRespDto) {
		return roleMapper.load(buildingRespDto);
	}

	@Override
	@Transactional
	public Result update(RoleDto roleDto) {
		RoleBuilding roleBuilding = new RoleBuilding();
		roleBuilding.setProjectId(roleDto.getProjectId());
		roleBuilding.setRoleId(roleDto.getRoleId());
		//先全删
		roleMapper.delete(roleBuilding);
		//先删除
		roleEquipmentMapper.deleteRoleId(roleDto.getRoleId(),roleDto.getProjectId());
		//再全加
		if(CollectionUtils.isNotEmpty(roleDto.getListBuilding())) {
			//加上设备
			roleEquipmentMapper.insertRoleEquipment(roleDto);
			roleMapper.insertRoleDtoBuilding(roleDto);
		}
		if(CollectionUtils.isNotEmpty(roleDto.getListSelection())) {
			roleMapper.insertRoleDtoSelection(roleDto);
			
			roleDto.setListBuilding(roleDto.getListSelection());
			roleEquipmentMapper.insertRoleEquipment(roleDto);
		}
		return Result.getDefaultTrue();
	}
	

	@Override
	@Transactional
	public Result batchUpdate(List<RoleDto> roleDto) {
		roleDto.forEach( this::update);
		return Result.getDefaultTrue();
	}

	@Override
	public List<Long> queryBuilding(Integer projectId, List<Integer> listRole) {
		List<Long> listBuilding = roleMapper.queryBuilding(projectId,listRole);
		return listBuilding;
	}

	@Override
	public List<String> queryEquipment(Integer projectId, List<Integer> listRole) {
		return roleMapper.queryEquipment(projectId,listRole);
	}

	@Override
	public List<Long> queryBuildingIdByProjectId(Integer projectId) {
		return buildingMapper.queryBuildingIdByProjectId(projectId);
	}

	@Override
	public List<String> querySerialNumberByProjectId(Integer projectId) {
		return equipmentUserMapper.querySerialNumberByProjectId(projectId);
	}

	@Override
	public List<Long> queryEquipmentUserByProjectId(Integer projectId) {
		return equipmentUserMapper.queryEquipmentUserByProjectId(projectId);
	}

	@Override
	public List<String> querySerialByRoleId(Integer roleId) {
		return roleEquipmentMapper.querySerialByRoleId(roleId);
	}

	@Override
	public List<Integer> queryRoleIdBySerial(String serialNumber) {
		return roleEquipmentMapper.queryRoleIdBySerial(serialNumber);
	}


}
