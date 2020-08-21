package cn.meiot.controller.pc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import cn.meiot.aop.UpgradeDetection;
import cn.meiot.constart.ProjectConstart;
import cn.meiot.constart.RabbitConstart;
import cn.meiot.entity.message.DeviceMessage;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.service.*;
import cn.meiot.utils.QueueConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.meiot.aop.Log;
import cn.meiot.constart.ResultConstart;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.dto.SwitchRespDto;
import cn.meiot.entity.dto.UpdateName;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.sw.SendSwitch;
import cn.meiot.entity.vo.Result;
import cn.meiot.utils.RedisConstantUtil;

/**
 * @author lingzhiying
 * @title: SwitchController.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月3日
 */
@RestController
@RequestMapping("pc/switch")
@Slf4j
public class PcSwitchController extends PcBaseController{
	
	@Autowired
	private SwitchService switchService;

	@Autowired
	private SwitchNameService switchNameService;
	
	@Autowired
	private BuildingService buildingService;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private EquipmentUserService equipmentUserService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@PostMapping("updateName")
	@Log(operateContent = "修改开关名和类型",operateModule="设备服务")
	public Result updateName(@RequestBody @Valid UpdateName updateName) {
        Long mainUserId = getMainUserId();
        updateName.setUserId(mainUserId);
        String serialNumber = equipmentUserService.authenticationSwtichSn(updateName.getSwitchSn(), mainUserId);
        updateName.setSerialNumber(serialNumber);
        Result result = switchService.updateName(updateName);
        return result;
    }
	
	@PostMapping("updateNameAll")
	@Log(operateContent = "修改全部开关名和开关类型",operateModule="设备服务")
	public Result updateNameAll(@RequestBody List<UpdateName> listName) {
		for (UpdateName updateName: listName) {
			if(StringUtils.isBlank(updateName.getName())){
				return Result.getDefaultFalse();
			}
		}
		Long mainUserId = getMainUserId();
		listName.forEach(updateName ->{
			updateName.setUserId(mainUserId);
            String serialNumber = equipmentUserService.authenticationSwtichSn(updateName.getSwitchSn(), mainUserId);
            updateName.setSerialNumber(serialNumber);
			switchService.updateName(updateName);
		});
		return Result.getDefaultTrue();
	}

	@GetMapping("querySwitchAll")
	@Log(operateContent = "查询设备",operateModule="设备服务")
	public Result querySwitchAll(String serialNumber) {
		Long mainUserId = getMainUserId();
		List<Map>listMap = switchNameService.querySwitchAll(serialNumber,mainUserId);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(listMap);
		return defaultTrue;
	}
	
	/**
	 * 开关控制
	 * @param sendSwitch
	 * @return
	 */
	@PostMapping("sendSwitch")
	@Log(operateContent = "开关控制",operateModule="设备服务")
	@UpgradeDetection
	public Result sendSwitch(@RequestBody SendSwitch sendSwitch) {
		Long mainUserId = getMainUserId();
		Long userId = getUserId();
		Integer projectId = getProjectId();
		Result result = null ;
		String serialNumber = sendSwitch.getSerialNumber();
		sendSwitch.setUserId(mainUserId);
		boolean querySerialNumberJurisdiction = equipmentUserService.querySerialNumberJurisdiction(userId, projectId, serialNumber, null, mainUserId);
		if(!querySerialNumberJurisdiction) {
			result = Result.getDefaultTrue();
			result.setData(ResultCodeEnum.NO_AUTHENTICATION.getMsg());
			return result;
		}
		Long switchSn = sendSwitch.getSwitchSn();
		List<String> switchList = null;
		if(switchSn != null) {
			switchList = new ArrayList<>();
			switchList.add(switchSn.toString());
			result = switchService.sendSwitch(sendSwitch);
		}else{
			switchList = new ArrayList<>();
			result = switchService.sendSwitchAll(sendSwitch,switchList);
		}
		DeviceMessage deviceMessage = DeviceMessage.switchControl(mainUserId,userId, projectId, serialNumber, switchList, sendSwitch.getStatus());
		rabbitTemplate.convertAndSend(QueueConstantUtil.ProjectMessage.SWITCH_CONTROL,deviceMessage);
		return result;
	}
	
	@PostMapping("sendSwitchProject")
	@UpgradeDetection
	@Log(operateContent = "企业全开全关",operateModule="设备服务")
	public Result sendSwitchProject(@RequestBody PcEquipmentUserCond cond) {
		Long userId = getUserId();
		Long mainUserId = getMainUserId();
		cond.setUserId(mainUserId);
		cond.setProjectId(getProjectId());
		List<Integer> listRole = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES+getUserId());
		if(!mainUserId.equals(userId)) {
			if(CollectionUtils.isEmpty(listRole)) {
				return Result.getDefaultTrue();
			}
			cond.setListRole(listRole);
		}
		return switchService.sendSwitchProject(cond);
	}
	
	@GetMapping("querySwitchColseProject")
	@UpgradeDetection
	@Log(operateContent = "企业全开全关开合闸状态",operateModule="设备服务")
	public Result querySwitchColseProject(PcEquipmentUserCond cond) {
		Long userId = getUserId();
		Long mainUserId = getMainUserId();
		cond.setUserId(mainUserId);
		cond.setProjectId(getProjectId());
		List<Integer> listRole = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES+getUserId());
		if(!mainUserId.equals(userId)) {
			if(CollectionUtils.isEmpty(listRole)) {
				return Result.getDefaultTrue();
			}
			cond.setListRole(listRole);
		}
		return switchService.querySwitchColseProject(cond);
	}
	
	/**
	 * 设置最大功率
	 * @param listSendSwitch
	 * @return
	 */
	@PostMapping("sendSwitchLoadmax")
	@UpgradeDetection
	@Log(operateContent = "设置最大功率",operateModule="设备服务")
	public Result sendSwitchLoadmax(@RequestBody List<SendSwitch> listSendSwitch) {
		Long userId = getMainUserId();
		return switchService.sendPcSwitchLoadmax(listSendSwitch);
	}
	
	/**
	 * web端  暂留 设置全部最大功率
	 * @param map
	 * @return
	 */
	@PostMapping("sendSwitchLoadmaxAll")
	@UpgradeDetection
	@Log(operateContent = "设置设备最大功率",operateModule="设备服务")
	public Result sendSwitchLoadmaxAll(@RequestBody Map<String,Object> map) {
		String serialNumber = (String) map.get("serialNumber");
		Integer loadMax = (Integer) map.get("loadMax");
		return switchService.sendPcSwitchLoadmaxAll(serialNumber,loadMax);
	}

	@PostMapping("switchLoadmaxAll")
	@Log(operateContent = "App设置最大功率")
	@UpgradeDetection
	public Result switchLoadmaxAll(@RequestBody SendSwitch map) {
        Long mainUserId = getMainUserId();
        String serialNumber = map.getSerialNumber();
		equipmentUserService.authentication(serialNumber,mainUserId);
		//设置全部开关
		map.setSwitchIndex(ProjectConstart.SWITCH_INDEX_ALL);
		Integer status = map.getStatus();
		Result result = switchService.sendSwitchLoadmaxAll(map);
        List<String> switchList = (List<String>) result.getData();
        DeviceMessage deviceMessage = DeviceMessage.loadMax(mainUserId, getUserId(), getProjectId(), serialNumber, switchList, status, map.getLoadMax());
        rabbitTemplate.convertAndSend(QueueConstantUtil.ProjectMessage.LOADMAX_ALL,deviceMessage);
        return result;
	}

    @PostMapping("switchLoadmax")
    @Log(operateContent = "App设置最大功率")
    @UpgradeDetection
    public Result switchLoadmax(@RequestBody SendSwitch map) {
        Long mainUserId = getMainUserId();
        Long userId = getUserId();
        Integer projectId = getProjectId();
        //equipmentUserService.authentication(sw,mainUserId);
        //设置全部开关
		Integer status = map.getStatus();
		Result result = switchService.sendSwitchLoadmaxPersonal(map);
        String serialNumber = (String)result.getData();
        List<String> switchList = new ArrayList<>();
        switchList.add(map.getSwitchSn().toString());
        DeviceMessage deviceMessage = DeviceMessage.loadMax(mainUserId, userId, projectId, serialNumber, switchList, status, map.getLoadMax());
        rabbitTemplate.convertAndSend(QueueConstantUtil.ProjectMessage.LOADMAX_ONE,deviceMessage);
        return result;
    }
	
	/**
	 * 获取功率开关状态
	 * @param serialNumber
	 * @return
	 */
	@GetMapping("querySwitchStatus")
	@Log(operateContent = "获取开关功率状态",operateModule="设备服务")
	public Result querySwitchStatus(String serialNumber,Integer deleted) {
		Long mainUserId = getMainUserId();
		Integer projectId = getProjectId();
		List<SwitchRespDto> querySwitchStatus = switchService.querySwitchStatus(serialNumber,mainUserId,projectId,deleted);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(querySwitchStatus);
		return defaultTrue;
	}

    /**
     * 根据设备号获取主开关编号
     * @param serialNumber
     * @return   Result     data直接存放Long类型的主开关编号
     */
    @GetMapping("switchLoadStatus")
    @Log(operateContent = "查询功率状态",operateModule="设备服务")
    public Result switchStatus(String serialNumber) {
        Long userId = getMainUserId();
        return switchService.switchStatus(serialNumber,userId);
    }

	/**
	 * 获取开关状态
	 * @param serialNumber
	 * @return
	 */
	@GetMapping("querySwitchDetails")
	@Log(operateContent = "获取开关状态",operateModule="设备服务")
	public Result querySwitchDetails(Long id,String serialNumber) {
		Long mainUserId = getMainUserId();
		Integer projectId = getProjectId();
		List<SwitchRespDto> querySwitchStatus = switchService.querySwitchDetails(serialNumber,mainUserId,projectId);
		Map map = new HashMap();
		map.put("list", querySwitchStatus);
		map.put("address", buildingService.queryAddress(id));
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}

	/**
	 * 获取开关状态
	 * @param serialNumber
	 * @return
	 */
	@GetMapping("querySwitch")
	@Log(operateContent = "获取开关状态",operateModule="设备服务")
	public Result querySwitch(String serialNumber) {
		Long mainUserId = getMainUserId();
		return switchService.querySwitch(serialNumber,mainUserId);
	}
	
	/**
	 * 通过building查询设备名，和主开关号
	 */
	@GetMapping("querySwitchByBuilding")
	@Log(operateContent = "通过building查询设备名，和主开关号",operateModule="设备服务")
	public Result querySwitchByBuilding(Long buildingId) {
		//Long mainUserId = getMainUserId();
		Integer projectId = getProjectId();
		List<Map> listMap = switchService.querySwitchByBuilding(buildingId,projectId);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(listMap);
		return defaultTrue;
	}
}
