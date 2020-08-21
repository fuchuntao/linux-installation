package cn.meiot.controller.pc;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.RoleService;
import cn.meiot.service.UseTimeService;
import cn.meiot.utils.RedisConstantUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import cn.meiot.aop.Log;
import cn.meiot.entity.db.EquipmentUser;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.excel.ProjectExcel;
import cn.meiot.entity.excel.UserExcel;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.EquipmentUserService;
import cn.meiot.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingzhiying
 * @title: EquipmentController.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月16日
 */
@RestController
@RequestMapping("pc/equipmentUser")
@Slf4j
public class PcEquipmentUserController extends PcBaseController{
	
	@Autowired
	private EquipmentUserService equipmentUserService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UseTimeService useTimeService;

	/**
	 * 查找个人设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("queryUser")
	public Result queryUser(PcEquipmentUserCond cond) {
		Result result = equipmentUserService.queryUser(cond);
		return result;
	}
	
	/**
	 * 查找个人设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("queryUserExcel")
	public Result queryUserExcel(PcEquipmentUserCond cond,HttpServletResponse response) {
		//Workbook workbook = null;
    	List<UserExcel> result = equipmentUserService.queryUserExcel(cond);
    	ExcelUtils.export(result,"个人设备",response, UserExcel.class);
		return null;
	}
	
	/**
	 * 查找个人设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("queryProjectExcel")
	public Result queryProjectExcel(PcEquipmentUserCond cond,HttpServletResponse response) {
		Workbook workbook = null;
    	List<ProjectExcel> result = equipmentUserService.queryProjectExcel(cond);
    	ExcelUtils.export(result,"企业设备",response, ProjectExcel.class);
		return null;
	}
	
	/**
	 * 查找个人设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("queryUserBySerialNumber")
	public Result queryUserBySerialNumber(PcEquipmentUserCond cond) {
		Result result = equipmentUserService.queryUserBySerialNumber(cond);
		return result;
	}
	
	/**
	 * 查找项目设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("queryProject")
	public Result queryProject(PcEquipmentUserCond cond) {
		Result result = equipmentUserService.queryProject(cond);
		return result;
	}
	
	
	/**
	 * 查找项目设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("querySerialNumberAll")
	@Log(operateContent = "数据统计--电箱列表",operateModule="设备服务")
	public Result querySerialNumberAll(PcEquipmentUserCond cond) {
		cond.setProjectId(getProjectId());
		cond.setUserId(getMainUserId());
		Result result = equipmentUserService.querySerialNumberAll(cond);
		return result;
	}
	
	/**
	 * 批量删除
	 * @param cond
	 * @return
	 */
	@PostMapping("delete")
	@Log(operateContent = "设备管理删除",operateModule="设备服务")
	public Result delete(@RequestBody List<String> listIds) {
		if(CollectionUtils.isEmpty(listIds)) {
			return Result.getDefaultTrue();
		}
		Long mainUserId = getMainUserId();
		Integer projectId = getProjectId();
		Result result = equipmentUserService.pcDelete(listIds);
		useTimeService.deleteSerial(mainUserId,projectId);
		return result;
	}

	/**
	 * 修改
	 * @param cond
	 * @return
	 */
	@PostMapping("update")
	@Log(operateContent = "修改电箱",operateModule="设备服务")
	public Result update(@RequestBody EquipmentUser equipmentUser) {
		Integer projectId = getProjectId();
		Long id = equipmentUser.getId();
		List<Long> longs = roleService.queryEquipmentUserByProjectId(projectId);
		if(CollectionUtils.isEmpty(longs) || !longs.contains(id)){
			throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
		}
		equipmentUser.setUserId(getMainUserId());
		equipmentUser.setProjectId(projectId);
		Result result = equipmentUserService.updatePc(equipmentUser);
		return result;
	}
	
	/**
	 * 删除
	 * @param cond
	 * @return
	 */
	@PostMapping("deletePc")
	@Log(operateContent = "删除电箱",operateModule="设备服务")
	public Result deletePc(@RequestBody EquipmentUser equipmentUser) {
		equipmentUser.setUserId(getMainUserId());
		equipmentUser.setProjectId(getProjectId());
		Result result = equipmentUserService.deletePc(equipmentUser);
		return result;
	}
    /**
     * 通过项目id查询所有的设备号
     * @param projectId 项目id
     * @return
     */
    @RequestMapping(value = "getSerialNUmbers",method = RequestMethod.GET)
    public List<String> getSerialNUmbersByProjectId(@RequestParam("projectId") Integer projectId){
    	List<String> serialNUmbersByProjectId = equipmentUserService.getSerialNUmbersByProjectId(projectId);
    	return serialNUmbersByProjectId;
    }
    
    /**
     * 查询设备的总数量
     * @return
     */
    @RequestMapping(value = "queryDeviceTotal",method = RequestMethod.GET)
    public Integer queryDeviceTotal(@RequestParam(value="projectId",defaultValue ="0") Integer projectId) {
    	if(projectId == 0) {
    		projectId = null;
    	}
    	return equipmentUserService.queryDeviceTotal(projectId);
    }

	/**
	 * 查询默认设备
	 * @return
	 */
	@GetMapping(value = "queryDefaultSerial")
	public Result queryDefaultSerial(String serialNumber) {
		Long mainUserId = getMainUserId();
		Long userId = getUserId();
		boolean isMain = mainUserId.equals(userId);
		Integer projectId = getProjectId();
		//如果没有传
		if(StringUtils.isEmpty(serialNumber)){
			List<String> listSerial = null;
			//是否是主账户
			if(isMain){
				//查询该项目下的全部设备
				listSerial = roleService.querySerialNumberByProjectId(projectId);
			}else{
				//查询该角色下的设备
				List<Integer> listRole = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES+userId);
				listSerial = roleService.queryEquipment(projectId,listRole);
			}
			if(CollectionUtils.isEmpty(listSerial)){
				throw new MyServiceException(ResultCodeEnum.MAIN_UNBIND.getCode(),ResultCodeEnum.MAIN_UNBIND.getMsg());
			}
			serialNumber = listSerial.get(0);
		}
		Map map = equipmentUserService.queryDefaultSerial(projectId,serialNumber);
		return  Result.OK(map);
	}


}
