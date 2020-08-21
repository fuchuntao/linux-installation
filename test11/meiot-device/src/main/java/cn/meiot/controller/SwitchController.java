package cn.meiot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import cn.meiot.aop.UpgradeDetection;
import cn.meiot.constart.ProjectConstart;
import cn.meiot.service.EquipmentService;
import cn.meiot.service.EquipmentUserService;
import cn.meiot.utils.NetworkingUtlis;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.meiot.aop.Log;
import cn.meiot.constart.ResultConstart;
import cn.meiot.entity.dto.UpdateName;
import cn.meiot.entity.dto.sw.SendSwitch;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SerialNumberMasterVo;
import cn.meiot.service.SwitchService;
import cn.meiot.utils.RedisConstantUtil;

/**
 * @author lingzhiying
 * @title: SwitchController.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月3日
 */
@RestController
@RequestMapping("switch")
public class SwitchController extends BaseController{
	
	@Autowired
	private SwitchService switchService;
	
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private NetworkingUtlis networkingUtlis;

	@Autowired
	private EquipmentUserService equipmentUserService;
	
	@PostMapping("updateName")
	@Log(operateContent = "App修改开关名")
	public Result updateName(@RequestBody @Valid UpdateName obj) {
		Long userId = getUserId();
		obj.setUserId(userId);
		Result result = switchService.updateName(obj);
		return result;
	}
	
	@PostMapping("sendSwitch")
	@Log(operateContent = "App控制开关")
	@UpgradeDetection
	public Result sendSwitch(@RequestBody SendSwitch sendSwitch) {
		Long userId = getUserId();
		sendSwitch.setUserId(userId);
		if(sendSwitch.getSwitchSn() != null) {
			return  switchService.sendSwitch(sendSwitch);
		}
		return  switchService.sendSwitchAll(sendSwitch,new ArrayList<>());
	}


	@PostMapping("sendSwitchLoadmax")
	@Log(operateContent = "App设置最大功率")
	@UpgradeDetection
	public Result sendSwitchLoadmax(@RequestBody SendSwitch map) {
		switchService.authentication(getUserId(),map.getSwitchSn().toString());
		//networkingUtlis.isNetworkingThrowException(serialNumber);
		return switchService.sendSwitchLoadmaxPersonal(map);
	}


	@PostMapping("sendSwitchLoadmaxAll")
	@Log(operateContent = "App设置最大功率")
	//@UpgradeDetection
	public Result sendSwitchLoadmaxAll(@RequestBody SendSwitch map) {
		String serialNumber = map.getSerialNumber();
		equipmentUserService.authentication(serialNumber,getUserId());
		//设置全部开关
		map.setSwitchIndex(ProjectConstart.SWITCH_INDEX_ALL);
		return switchService.sendSwitchLoadmaxAll(map);
	}
	
	/**
     * 根据设备号获取主开关编号
     * @param serialNumber
     * @return   Result     data直接存放Long类型的主开关编号
     */
    @GetMapping("getMasterIndex")
    public Result getMasterIndex(String serialNumber) {
    	return switchService.getMasterIndex(serialNumber);
    }

	/**
	 * 根据设备号获取主开关编号
	 * @param serialNumber
	 * @return   Result     data直接存放Long类型的主开关编号
	 */
	@GetMapping("switchStatus")
	public Result switchStatus(String serialNumber) {
		Long userId = getUserId();
		return switchService.switchStatus(serialNumber,userId);
	}
    
    /**
     * 通过设备号查询主开关编号
     * @param serialNumbers
     * @return  
     */

    @PostMapping(value = "queryMasterIndexBySerialNUmber")
    public List<SerialNumberMasterVo> queryMasterIndexBySerialNUmber(@RequestBody List<String> serialNumbers){
    	return switchService.queryMasterIndexBySerialNUmber(serialNumbers);
    }
    
    /**
     * 根据设备号获取主开关编号
     * @param serialNumber
     * @return   Result     data直接存放Long类型的主开关编号
     */
    @GetMapping("querySwitchStatus")
    public Result querySwitchStatus(String switchSn) {
    	Result defaultTrue = Result.getDefaultTrue();
    	Object obj = redisTemplate.opsForValue().get(RedisConstantUtil.FAULT_SERIALNUMER + "_" +switchSn);
    	if(null == obj) {
    		defaultTrue.setData(1);
    	}else {
    		defaultTrue.setData(obj);
    	}
    	return defaultTrue;
    }
}
