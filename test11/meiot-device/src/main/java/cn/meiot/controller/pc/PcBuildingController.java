package cn.meiot.controller.pc;

import cn.meiot.constart.RedisConstart;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.RoleService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.meiot.aop.Log;
import cn.meiot.dao.EquipmentMapper;
import cn.meiot.entity.db.Building;
import cn.meiot.entity.db.Equipment;
import cn.meiot.entity.dto.pc.BuildingRespDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.BuildingService;

import java.util.List;

@RestController
@RequestMapping("pc/building")
public class PcBuildingController extends PcBaseController{
	
	@Autowired
	private BuildingService buildingService;
	
	@Autowired
	private EquipmentMapper equipmentMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private RoleService roleService;
	
	/**
	 * 查询组织结构
	 * @param buildingRespDto
	 * @return
	 */
	@GetMapping("queryBuilding")
	@Log(operateContent = "查询组织架构",operateModule="设备服务")
	public Result queryBuilding(BuildingRespDto buildingRespDto) {
		Long mainUserId = getMainUserId();
		//设置当前用户
		buildingRespDto.setCurrentUserId(getUserId());
		//添加主用户
		buildingRespDto.setUserId(mainUserId);
		//添加项目id
		buildingRespDto.setProjectId(getProjectId());
		return buildingService.queryBuilding(buildingRespDto);
	}
	
	/**
	 * 查询组织结构
	 * @param buildingRespDto
	 * @return
	 */
	@GetMapping("queryBuildingAdmin")
	@Log(operateContent = "管理员查询组织架构",operateModule="设备服务")
	public Result queryBuildingAdmin(BuildingRespDto buildingRespDto) {
		Long mainUserId = getMainUserId();
		//设置当前用户
		buildingRespDto.setCurrentUserId(mainUserId);
		//添加主用户
		buildingRespDto.setUserId(mainUserId);
		//添加项目id
		buildingRespDto.setProjectId(getProjectId());
		return buildingService.queryBuilding(buildingRespDto);
	}
	
	/**
	 * 查询组织结构
	 * @param buildingRespDto
	 * @return
	 */
	@GetMapping("queryEquipment")
	@Log(operateContent = "查询楼层数据",operateModule="设备服务")
	public Result queryEquipment(BuildingRespDto buildingRespDto) {
		buildingRespDto.setCurrentUserId(getUserId());
		Long mainUserId = getMainUserId();
		//添加主用户
		buildingRespDto.setUserId(mainUserId);
		//添加项目id
		buildingRespDto.setProjectId(getProjectId());
		return buildingService.queryEquipment(buildingRespDto);
	}
	

	/**
    * [新增]
    * @author lingzhiying
    * @date 2019/09/17
    **/
    @PostMapping("insert")
    @Log(operateContent = "组织架构--新增",operateModule="设备服务")
    public Result insert(@RequestBody Building building){
    	Long mainUserId = getMainUserId();
		//添加主用户
    	building.setUserId(mainUserId);
		//添加项目id
    	building.setProjectId(getProjectId());
		redisTemplate.delete(RedisConstart.BUILDING+building.getProjectId());
        return buildingService.insert(building);
    }
    
    /**
     * [修改]
     * @author lingzhiying
     * @date 2019/09/17
     **/
     @PostMapping("update")
     @Log(operateContent = "组织架构--修改",operateModule="设备服务")
     public Result update(@RequestBody Building building){
     	//主用户
		 //authentication(building.getId());
		 redisTemplate.delete(RedisConstart.BUILDING+getProjectId());
     	return buildingService.update(building);
     }

	private void authentication(Long buildingId) {
		Long mainUserId = getMainUserId();
		Integer projectId = getProjectId();
		Long userId = getUserId();
		List<Long> longs = null;
		if(mainUserId.equals(userId)){
			//如果该账户是主账户
			longs = roleService.queryBuildingIdByProjectId(projectId);
		}
		//为空直接返回
		if(CollectionUtils.isEmpty(longs) || !longs.contains(buildingId)){
			throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
		}
	}

	/**
     * [删除]
     * @author lingzhiying
     * @date 2019/09/17
     **/
     @PostMapping("delete")
     @Log(operateContent = "组织架构--删除",operateModule="设备服务")
     public Result delete(@RequestBody Building building){
     	 //鉴权
		 //authentication(building.getId());
		 Integer projectId = getProjectId();
		 building.setProjectId(projectId);
		 Long mainUserId = getMainUserId();
		 building.setUserId(mainUserId);
		 Result delete = buildingService.delete(building);
		 redisTemplate.delete(RedisConstart.BUILDING+getProjectId());
		 return delete;
     }
    
     /**queryBuilding
 	 * 查询组织结构
 	 * @param
 	 * @return
 	 */
 	@GetMapping("queryAddress")
 	public String queryAddress(String serialNumber) {
 		Equipment selectByPrimaryKey = equipmentMapper.selectByPrimaryKey(serialNumber);
 		Long buildingId = selectByPrimaryKey.getBuildingId();
 		return buildingService.queryAddress(buildingId);
 	}


	/**
	 * 查询组织结构
	 * @param
	 * @return
	 */
	@GetMapping("querySerialByBuildingId")
	@Log(operateContent = "企业app组织架构查询所有设备",operateModule="设备服务")
	public Result querySerialByBuildingId(Long buildingId) {
		Integer projectId = getProjectId();
		Long userId = getUserId();
		Long mainUserId = getMainUserId();
		//查出该组织架构下的所有id
		List<Long> ids = buildingService.queryBuildingIds(buildingId,projectId,mainUserId,userId);
		return buildingService.querySerialByBuildingIds(ids);
	}

	/**
	 * 查询企业app首页组织结构
	 * @param
	 * @return
	 */
	@GetMapping("homeBuilding")
	@Log(operateContent = "企业app组织架构查询所有设备",operateModule="设备服务")
	public Result homeBuilding(Integer projectId) {
		projectId = getProjectId(projectId);
		Long userId = getUserId();
		Long mainUserId = getMainUserId();
		//查出该组织架构下的所有id
		return buildingService.homeBuilding(projectId,mainUserId,userId);
	}

	/**
	 * 查询企业第一个设备的组织架构
	 * @param
	 * @return
	 */
	@GetMapping("defaultBuildingId")
	//@Log(operateContent = "企业app组织架构查询所有设备",operateModule="设备服务")
	public Result defaultBuildingId() {
		Integer projectId = getProjectId();
		//查出该组织架构下的所有id
		return buildingService.defaultBuildingId(projectId);
	}

}
