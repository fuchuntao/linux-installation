package cn.meiot.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.meiot.entity.db.RoleBuilding;
import cn.meiot.entity.dto.pc.BuildingRespDto;
import cn.meiot.entity.dto.pc.role.RoleDto;
import tk.mybatis.mapper.common.BaseMapper;

public interface RoleMapper extends BaseMapper<RoleBuilding>{

	void insertRoleDtoBuilding(RoleDto roleDto);

	List<Long> load(BuildingRespDto buildingRespDto);

	/**
	 * 根据项目id查找选中教室
	 * @param projectId
	 * @param listRole
	 * @return
	 */
	List<Long> queryBuilding(@Param("projectId")long projectId,@Param("listRole") List<Integer> listRole);

	void insertRoleDtoSelection(RoleDto roleDto);

	/**
	 *  根据情境模式查询设备
	 * @param projectId
	 * @param listRole
	 * @return
	 */
	List<String> queryEquipment(@Param("projectId") Integer projectId, @Param("listRole") List<Integer> listRole);

	/**
	 * 根据组织架构查询id
	 * @param longs
	 * @return
	 */
    Integer findOneIdByBuildingIds(@Param("ids") List<Long> longs);
}
