package cn.meiot.controller;

import java.util.List;

import javax.validation.Valid;

import cn.meiot.enums.ResultCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.meiot.aop.Log;
import cn.meiot.dao.EquipmentUserMapper;
import cn.meiot.entity.db.EquipmentUser;
import cn.meiot.entity.dto.EquipmentUserDto;
import cn.meiot.entity.dto.UpdateName;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.EquipmentUserService;

/**
 * @author lingzhiying
 * @title: EquipmentUserController.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月19日
 */
@RestController
@RequestMapping("equipmentUser")
public class EquipmentUserController extends BaseController{
	
	@Autowired
	private EquipmentUserService equipmentUserService;
	
	@Autowired
	private EquipmentUserMapper equipmentUserMapper;
	
	/**
	 * 审批绑定设备
	 * @param serialNumber
	 * @return
	 */
	@PostMapping("approval")
	@Log(operateContent = "App审批绑定设备")
	public Result approvalEquipment(@RequestBody EquipmentUserDto equipmentUserDto) {
		Long userId = getUserId();
		equipmentUserDto.setUserId(userId);
		Result result = equipmentUserService.approvalEquipment(equipmentUserDto);
		return result;
	}
	
	/**
	 * 查询当前用户的电箱
	 * @return
	 */
	@GetMapping("query")
	@Log(operateContent = "App用户当前电箱")
	public Result queryEquipmentUser() {
		Long userId = getUserId();
		Result result = equipmentUserService.queryEquipmentUser(userId);
		return result;
	}
	
	/**
	 * 根据用户id查询绑定设备数量
	 * @return
	 */
	@GetMapping("queryBindNum")
	public int queryBindNum(Long userId) {
		EquipmentUser equipmentUser = new EquipmentUser();
		equipmentUser.setUserId(userId);
		equipmentUser.setUserStatus(1);
		int selectCount = equipmentUserMapper.selectCount(equipmentUser);
		return selectCount;
	}
	
	/**
	 * 修改设备名称
	 * @return
	 */
	@PostMapping("updateName")
	@Log(operateContent = "App修改设备名")
	public Result updateName(@RequestBody @Valid UpdateName updateName) {
		Long userId = getUserId();
		updateName.setUserId(userId);
		Result result = equipmentUserService.updateName(updateName);
		return result;
	}
	
	 /**
     * 通过设备号查询用户id
     * @param serialNumber
     * @return Result   data字段为List<String>集合，存储绑定此账户的所有账户id，第一个为主账户id
     */
    @GetMapping( "getRtuserIdBySerialNumber")
    public Result getRtuserIdBySerialNumber(@RequestParam("serialNumber") String serialNumber) {
    	if(serialNumber == null) {
    		Result result = Result.getDefaultFalse();
    		return result;
    	}
    	return equipmentUserService.getRtuserIdBySerialNumber(serialNumber);
    }
    
    /**
     * 通过设备号查询用户id
     * @param serialNumber
     * @return Result   data字段为List<String>集合，存储绑定此账户的所有账户id，第一个为主账户id
     */
    @GetMapping( "queryUserName")
    public Result queryUserName(String serialNumber) {
    	if(serialNumber == null) {
    		Result result = Result.getDefaultFalse();
    		return result;
    	}
    	return equipmentUserService.queryUserName(serialNumber);
    }
    
    
    
    /**
     * 解绑设备
     * @param serialNumber
     * @return Result  
     */
    @PostMapping("unbound")
    @Log(operateContent = "App解绑设备")
    public Result unbound(@RequestBody EquipmentUser equipmentUser) {
    	if(equipmentUser == null ||equipmentUser.getSerialNumber() == null) {
    		Result result = Result.getDefaultFalse();
    		return result;
    	}
    	Long userId = getUserId();
    	equipmentUser.setUserId(userId);
    	return equipmentUserService.unbound(equipmentUser);
    }
    
    /**
     * 设置为默认设备
     * @param serialNumber
     * @return Result  
     */
    @PostMapping("unDefault")
    @Log(operateContent = "App设置默认设备")
    public Result unDefault(@RequestBody EquipmentUser equipmentUser) {
    	if(equipmentUser == null ||equipmentUser.getSerialNumber() == null) {
    		Result result = Result.getDefaultFalse();
    		return result;
    	}
    	Long userId = getUserId();
    	equipmentUser.setUserId(userId);
    	return equipmentUserService.unDefault(equipmentUser);
    }
    
    /**
	 * 查询设备信息
	 * @param serialNumber
	 * @return
	 */
	@GetMapping("queryEquipment")
	@Log(operateContent = "App查询设备信息")
	public Result queryEquipment(String serialNumber) {
		if(StringUtils.isBlank(serialNumber)) {
			Result result = Result.getDefaultFalse();
			result.setMsg(ResultCodeEnum.EQUIMENT_IS_NULL.getMsg());
			result.setCode(ResultCodeEnum.EQUIMENT_IS_NULL.getCode());
			return result;
		}
		Long userId = getUserId();
		Result equipment = equipmentUserService.queryEquipment(serialNumber,userId);
		return equipment;
	}
    
    /**
     * 通过设备号查询主账号id
     * @param userId 子账户id
     * @param serialNumber  设备序列号
     * @return Result   data字段为Long类型，存储用户id
     */
    @GetMapping(value = "getRtuserIdByUserId")
    public Result getRtuserIdByUserId(String serialNumber) {
		return equipmentUserService.getRtuserIdByUserId(serialNumber);
    }
    
    /**
     * 给子账户添加用户名
     * @param userId 子账户id
     * @param serialNumber  设备序列号
     * @return Result   data字段为Long类型，存储用户id
     */
    @PostMapping(value = "updateUserName")
    public Result updateUserName(@RequestBody EquipmentUser equipmentUser) {
    	equipmentUser.setUserId(getUserId());
    	boolean queryIsMainUser = equipmentUserService.queryIsMainUserById(equipmentUser.getId(), equipmentUser.getUserId());
    	if(queryIsMainUser) {
    		return equipmentUserService.updateUserName(equipmentUser);
    	}else {
    		Result defaultFalse = Result.getDefaultFalse();
    		defaultFalse.setCode(ResultCodeEnum.NO_AUTHENTICATION.getCode());
			defaultFalse.setMsg(ResultCodeEnum.NO_AUTHENTICATION.getMsg());
    		return defaultFalse;
    	}
    }
    
    /**
     * 解绑子用户设备
     * @param userId 子账户id
     * @param serialNumber  设备序列号
     * @return Result   data字段为Long类型，存储用户id
     */
    @PostMapping(value = "delete")
    public Result delete(@RequestBody EquipmentUser equipmentUser) {
    	equipmentUser.setUserId(getUserId());
    	boolean queryIsMainUser = equipmentUserService.queryIsMainUserById(equipmentUser.getId(), equipmentUser.getUserId());
    	if(queryIsMainUser) {
    		return equipmentUserService.delete(equipmentUser);
    	}else {
    		Result defaultFalse = Result.getDefaultFalse();
			defaultFalse.setCode(ResultCodeEnum.NO_AUTHENTICATION.getCode());
			defaultFalse.setMsg(ResultCodeEnum.NO_AUTHENTICATION.getMsg());
    		return defaultFalse;
    	}
    }
    
    /**
	 * 查询当前用户的主电箱
	 * @return
	 */
	@GetMapping("queryMain")
	public Result queryMain() {
		Long userId = getUserId();
		Result result = equipmentUserService.queryMain(userId);
		return result;
	}
	
	@RequestMapping(value = "getProjectIdBySerialNumber")
    public Integer getProjectIdBySerialNumber(@RequestParam("serialNumber") String serialNumber) {
		EquipmentUser equipmentUser = new EquipmentUser();
		equipmentUser.setSerialNumber(serialNumber);
		List<EquipmentUser> select = equipmentUserMapper.select(equipmentUser);
		if(CollectionUtils.isEmpty(select)) {
			return null;
		}
		return select.get(0).getProjectId();
	}

	@GetMapping(value = "getSerialNumberByName")
    public List<String> getSerialNumberByName(String name) {
		if(StringUtils.isEmpty(name)) {
			return null;
		}
		return equipmentUserMapper.getSerialNumberByName(name);
	}
	
}
