package cn.meiot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.meiot.aop.UpgradeDetection;
import cn.meiot.enums.ResultCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import cn.meiot.aop.Log;
import cn.meiot.dao.EquipmentMapper;
import cn.meiot.entity.db.Equipment;
import cn.meiot.entity.dto.EquipmentRespDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.EquipmentService;
import cn.meiot.service.EquipmentUserService;
import cn.meiot.service.SwitchService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingzhiying
 * @title: EquipmentController.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月16日
 */
@RestController
@RequestMapping("equipment")
@Slf4j
public class EquipmentController extends BaseController{
	
	@Autowired
	private EquipmentService equipmentService;
	
	@Autowired
	private EquipmentMapper equipmentMapper;
	
	@Autowired
	private EquipmentUserService equipmentUserService;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private SwitchService switchService;
	
	/**
	 * 查询设备信息
	 * @param serialNumber
	 * @return
	 */
	@GetMapping("queryEquipment")
	@Log(operateContent = "App发起绑定该设备详情")
	public Result queryEquipment(String serialNumber) {
		if(StringUtils.isBlank(serialNumber)) {
			Result result = Result.getDefaultFalse();
			result.setMsg(ResultCodeEnum.EQUIMENT_IS_NULL.getMsg());
			result.setCode(ResultCodeEnum.EQUIMENT_IS_NULL.getCode());
			return result;
		}
		Long userId = getUserId();
		Result equipment = equipmentService.queryEquipment(serialNumber,userId);
		EquipmentRespDto equ = (EquipmentRespDto) equipment.getData();
		if(equ != null && equ.getUserStatus() == null) {
			equ.setUserStatus(2);
		}
		return equipment;
	}

	/**
	 * 设置漏电自检
	 * @return
	 */
	@PostMapping("examination")
	@Log(operateContent = "App设置漏电自检")
	@UpgradeDetection
	public Result examination(@RequestBody Map map) {
		String serialNumber = (String)map.get("serialNumber");
		Integer examinationStatus = (Integer)map.get("examinationStatus");
		String examinationTime = (String)map.get("examinationTime");
		Equipment equipment = new Equipment();
		equipment.setSerialNumber(serialNumber);
		equipment.setExaminationStatus(examinationStatus);
		equipment.setExaminationTime(examinationTime);
		if(StringUtils.isBlank(serialNumber)||examinationStatus == null || StringUtils.isBlank(examinationTime)) {
			Result result = Result.getDefaultFalse();
			result.setMsg(ResultCodeEnum.EQUIMENT_IS_NULL.getMsg());
			result.setCode(ResultCodeEnum.EQUIMENT_IS_NULL.getCode());
			return result;
		}
		Result result = equipmentService.examination(equipment);
		return result;
	}

	@PostMapping("batchExamination")
	@Log(operateContent = "App设置批量漏电自检")
	@UpgradeDetection
	public Result batchExamination(@RequestBody  Map map) {
		List<String> serialList = (List<String>) map.get("serialList");
		Integer examinationStatus = (Integer) map.get("examinationStatus");
		String examinationTime = (String) map.get("examinationTime");
		final Long userId = getUserId();
		//鉴权
		serialList.forEach( serial->equipmentUserService.authentication(serial,userId));
		return equipmentService.batchExamination(serialList,examinationStatus,examinationTime);
	}
	
	/**
	 * 绑定设备
	 * @param serialNumber
	 * @return
	 */
	@PostMapping("bindEquipment")
	@Log(operateContent = "App绑定设备或者申请绑定")
	public Result bindEquipment(@RequestBody Map map) {
		String serialNumber = (String)map.get("serialNumber");
		if(StringUtils.isBlank(serialNumber)) {
			Result result = Result.getDefaultFalse();
			result.setMsg(ResultCodeEnum.EQUIMENT_IS_NULL.getMsg());
			result.setCode(ResultCodeEnum.EQUIMENT_IS_NULL.getCode());
			return result;
		}
		Long userId = getUserId();
		synchronized (serialNumber) {
			Result result = equipmentService.bindEquipment(serialNumber, userId);
			return result;
		}
	}
	
	/**
	 * 查询设备状态
	 * @param serialNumber
	 * @return
	 */
	@GetMapping("realtime")
	@Log(operateContent = "App查询设备状态")
	public Result realtime(String serialNumber) {
		Long userId = getUserId();
		if(StringUtils.isBlank(serialNumber)) {
			Result result = Result.getDefaultFalse();
			result.setMsg(ResultCodeEnum.EQUIMENT_IS_NULL.getMsg());
			result.setCode(ResultCodeEnum.EQUIMENT_IS_NULL.getCode());
			return result;
		}
		boolean falg = equipmentUserService.queryUserIdAndSerialumber(userId,serialNumber);
		if(!falg) {
			Result result = Result.faild(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
			return result;
		}
		Result result = equipmentService.realtime(serialNumber);
		return result;
	}
	
	/**
	 * 查询设备状态
	 * @param serialNumber
	 * @return
	 */
	@GetMapping("queryEquipmentStatus")
	@Log(operateContent = "App查询设备状态")
	public Result queryEquipmentStatus(String serialNumber) {
		Long userId = getUserId();
		if(StringUtils.isBlank(serialNumber)) {
			Result result = Result.getDefaultFalse();
			result.setMsg(ResultCodeEnum.EQUIMENT_IS_NULL.getMsg());
			result.setCode(ResultCodeEnum.EQUIMENT_IS_NULL.getCode());
			return result;
		}
		boolean falg = equipmentUserService.queryUserIdAndSerialumber(userId,null);
		if(!falg) {
			Result result = Result.getDefaultFalse();
			result.setMsg(ResultCodeEnum.NO_AUTHENTICATION.getMsg());
			result.setCode(ResultCodeEnum.NO_AUTHENTICATION.getCode());
			return result;
		}
		Result result = equipmentService.queryEquipmentStatus(serialNumber,userId);
		return result;
	}
	
	/**
	 * 漏电自检
	 * @param serialNumber
	 * @return
	 */
	@GetMapping("timingExamination")
	public Result timingExamination() {
		Result result = equipmentService.timingExamination();
		return result;
	}
	
	@GetMapping(value = "queryCreateTime")
    public Map<String,Object> queryDayAndMonth(String serialNumber){
		Equipment eq = equipmentMapper.selectByPrimaryKey(serialNumber);
		if(eq == null) {
			return null;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("createTime", eq.getCreateTime().getTime());
		return map;
	}
	
	@GetMapping(value = "timeQuerySeria")
    public void timeQuerySeria(){
		/*SendSwitch sd = new SendSwitch();
		sd.setSerialNumber("M2201910100005");
		sd.setStatus(status);
		switchService.sendSwitchGjh(sd);
		if(status == 1) {
			
		}*/
	}
}
