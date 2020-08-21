package cn.meiot.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.meiot.entity.db.Building;
import cn.meiot.entity.dto.pc.BuildingRecursionDto;
import cn.meiot.entity.dto.pc.BuildingRespDto;
import cn.meiot.entity.dto.pc.examination.SerialDto;
import tk.mybatis.mapper.common.BaseMapper;

public interface BuildingMapper extends BaseMapper<Building>{
	
	/**
	 * 递归查询该用户的组织架构
	 * @param buildingRespDto
	 * @return
	 */
	List<BuildingRecursionDto> queryBuilding(BuildingRespDto buildingRespDto);

	/**
	 * 递归查询pid
	 * @param pId
	 * @return
	 */
	List<BuildingRecursionDto> queryBuildingDg(long pId);

	List<SerialDto> querySerialAndSwitchByBuilding(@Param("id")Long id,@Param("userId") Long userId, @Param("projectId") Integer projectId);

	/**
	 * 通过项目id查询所有组织架构
	 * @param projectId
	 * @return
	 */
    List<Long> queryBuildingIdByProjectId(Integer projectId);

	/**
	 * 删除节点
	 * @param longs
	 */
	void deleteIds(@Param("ids") List<Long> longs);

	/**
	 * 修改设备总数
	 * @param ids
	 * @param type
	 */
    void updateBuildingSerialToal(@Param("ids") List<Long> ids, @Param("type") Integer type);

    BuildingRecursionDto defaultBuildingByPorjectId(Integer projectId);
}
