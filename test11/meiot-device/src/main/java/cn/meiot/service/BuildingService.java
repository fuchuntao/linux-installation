package cn.meiot.service;

import java.util.List;
import java.util.Map;

import cn.meiot.entity.db.Building;
import cn.meiot.entity.dto.pc.BuildingRecursionDto;
import cn.meiot.entity.dto.pc.BuildingRespDto;
import cn.meiot.entity.dto.pc.examination.ExaminationBuildingDto;
import cn.meiot.entity.vo.Result;

public interface BuildingService {
	
	/**
	 * 查询组织架构递归
	 */
	public List<BuildingRecursionDto> queryRecursion(BuildingRespDto buildingRespDto);

	/**
	 * 查找组织结构
	 * @param buildingRespDto
	 * @return
	 */
	Result queryBuilding(BuildingRespDto buildingRespDto);

	/**
	 * 添加组织架构
	 * @param building
	 * @return
	 */
	Result insert(Building building);

	/**
	 * 删除节点
	 * @param building
	 * @return
	 */
	Result delete(Building building);

	/**
	 * 修改组织架构
	 * @param building
	 * @return
	 */
	Result update(Building building);

	/**
	 * 查询设备组织架构
	 * @param buildingRespDto
	 * @return
	 */
	Result queryEquipment(BuildingRespDto buildingRespDto);
	
	/**
	 * 递归往外查询地址  获取地址名
	 */
	String queryAddress(Long id);

	/**
	 * 	情景模式 处理数据
	 * @param listData
	 * @param id
	 * @param userId
	 * @param projectId
	 * @param listBuilding
	 * @param listEquiment
	 * @param listSwitch
	 */
	public void queryDg(List<ExaminationBuildingDto> listData, Long id, Long userId, Integer projectId, List<Long> listBuilding, List<Long> listEquiment, List<String> listSwitch,Integer flag);

	/**
	 * 查询情景模式所需数据
	 * @param userId
	 * @param projectId
	 * @return
	 */
	List<ExaminationBuildingDto> queryScenarioData( Long userId, Integer projectId,Long currentUserId);

	/**
	 *查询全部组织架构
	 * @param
	 * @return
	 */
	List<Building> getBuildings(Integer projectId,Long userId);

	/**
	 * 根据组织架构id查询该组织下所有组织id
	 * @param building
	 * @return
	 */
    List<Long> queryBuildingIds(Long buildingId,Integer projectId,Long mainUserId,Long userId);

	/**
	 *
	 * @param ids
	 * @return
	 */
	Result querySerialByBuildingIds(List<Long> ids);

	/**
	 * 查询该节点下一层级的名称以及所有id
	 * @param id
	 * @param projectId
	 * @param mainUserId
	 * @return
	 */
    List<Map> querSubName(Long id, Integer projectId, Long mainUserId);

	/**
	 * 通过组织架构查询水表号
	 * @param id
	 * @param projectId
	 * @param mainUserId
	 * @return
	 */
	List<String> querySubBuildingWaterId(Long id, Integer projectId, Long mainUserId);

	/**
	 * 修改组织架构是否有设备
	 */
	void saveSerial(Long id,Long mainUserId,Integer projectId);

	/**
	 * 删除设备
	 */
	void deleteSerial(Long id,Long mainUserId,Integer projectId);

	/**
	 *
	 * @param projectId
	 * @param mainUserId
	 * @param userId
	 * @return
	 */
    Result homeBuilding(Integer projectId, Long mainUserId, Long userId);

	/**
	 *
	 * @param projectId
	 * @return
	 */
	Result defaultBuildingId(Integer projectId);
}
