package cn.meiot.controller.pc;

import java.util.*;

import cn.meiot.aop.UpgradeDetection;
import cn.meiot.entity.message.DeviceMessage;
import cn.meiot.entity.vo.EquialStatusVo;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.utils.DateUtil;
import cn.meiot.utils.QueueConstantUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.meiot.aop.Log;
import cn.meiot.dao.EquipmentMapper;
import cn.meiot.entity.db.Equipment;
import cn.meiot.entity.dto.pc.equipmentUser.EquipmentUserInsert;
import cn.meiot.entity.dto.pc.examination.BatchExamination;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.EquipmentService;
import cn.meiot.service.EquipmentUserService;

/**
 * @author lingzhiying
 * @title: EquipmentController.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月16日
 */
@RestController
@RequestMapping("pc/equipment")
public class PcEquipmentController extends PcBaseController{
	
	@Autowired
	private EquipmentService equipmentService;
	
	@Autowired
	private EquipmentUserService equipmentUserService;
	
	@Autowired
	private EquipmentMapper equipmentMapper;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private Calendar calendar = Calendar.getInstance();
	/**
	 * 查找building设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("queryBuilding")
	@Log(operateContent = "根据组织架构查询设备",operateModule="设备服务")
	public Result queryBuilding(Long id) {
		Long mainUserId = getMainUserId();
		Result result = equipmentService.queryBuilding(id,mainUserId,getUserId());
		return result;
	}
	
	/**
	 * 查找building设备列表
	 * @param cond
	 * @return
	 */
	@PostMapping("insert")
	@Log(operateContent = "添加设备",operateModule="设备服务")
	public Result insert(@RequestBody EquipmentUserInsert equipmentUserInsert) {
		if(StringUtils.isBlank(equipmentUserInsert.getName())) {
			return Result.getDefaultFalse();
		}
		Long mainUserId = getMainUserId();
		equipmentUserInsert.setUserId(mainUserId);
		equipmentUserInsert.setProjectId(getProjectId());
		Result result = equipmentService.pcInsert(equipmentUserInsert);
		return result;
	}

	/**
	 * 设置漏电自检
	 * @param serialNumber
	 * @return
	 */
	@PostMapping("examination")
	@UpgradeDetection
	@Log(operateContent = "设置漏电自检",operateModule="设备服务")
	public Result examination(@RequestBody Map map) {
		Long mainUserId = getMainUserId();
		Integer projectId = getProjectId();
		Long userId = getUserId();
		String serialNumber = (String)map.get("serialNumber");
		boolean querySerialNumberJurisdiction = equipmentUserService.querySerialNumberJurisdiction(userId, projectId, serialNumber, null,mainUserId);
		if(!querySerialNumberJurisdiction) {
			Result result = Result.getDefaultFalse();
			result.setMsg(ResultCodeEnum.EQUIMENT_IS_NULL.getMsg());
			result.setCode(ResultCodeEnum.EQUIMENT_IS_NULL.getCode());
			return result;
		}
		String examinationTime = (String)map.get("examinationTime");
		Integer examinationStatus = (Integer) map.get("examinationStatus");
		Equipment equipment = new Equipment();
		equipment.setSerialNumber(serialNumber);
		if(examinationStatus ==null){
			examinationStatus = 1;
		}
		equipment.setExaminationStatus(examinationStatus);
		equipment.setExaminationTime(examinationTime);
		Result result = equipmentService.examination(equipment);
		if(StringUtils.isEmpty(examinationTime)) {
			examinationTime = (String) result.getData();
		}
		List<String> serialList = new ArrayList<>();
		serialList.add(serialNumber);
		DeviceMessage examination = DeviceMessage.examination(mainUserId, userId, projectId, serialList, examinationStatus, examinationTime);
		rabbitTemplate.convertAndSend(QueueConstantUtil.ProjectMessage.EXAMINATION,examination);
		return result;
	}
	
	/**
	 * 设置漏电自检
	 * @return
	 */
	@PostMapping("batchExamination")
	@UpgradeDetection
	@Log(operateContent = "批量设置漏电自检",operateModule="设备服务")
	public Result batchExamination(@RequestBody BatchExamination batchExamination) {
		Long mainUserId = getMainUserId();
		Integer projectId = getProjectId();
		Long userId = getUserId();
		String examinationTime = batchExamination.getExaminationTime();
		List<String> serialList = batchExamination.getSerialNumber();
		for (String serialNumber : batchExamination.getSerialNumber()) {
			boolean querySerialNumberJurisdiction = equipmentUserService.querySerialNumberJurisdiction(mainUserId,projectId, serialNumber, null,mainUserId);
			if(!querySerialNumberJurisdiction) {
				return Result.getDefaultFalse();
			}
		}
		Integer examinationStatus = 1;
		Result result = equipmentService.batchExamination(serialList, examinationStatus, examinationTime);
		DeviceMessage examination = DeviceMessage.examination(mainUserId, userId, projectId, serialList, examinationStatus, examinationTime);
		rabbitTemplate.convertAndSend(QueueConstantUtil.ProjectMessage.EXAMINATION,examination);
		return result;
	}
	
	/**
     * 根据设备号查询地址ID
     * @param serialNumber
     * @return
     */
    @GetMapping("queryAddressIdBySerialNumber")
    public Long queryAddressIdBySerialNumber(@RequestParam("serialNumber")String serialNumber) {
    	Equipment selectByPrimaryKey = equipmentMapper.selectByPrimaryKey(serialNumber);
    	if(selectByPrimaryKey == null) {
    		return null;
    	}
    	return selectByPrimaryKey.getBuildingId();
    }

	/**
	 *
	 * @Title: allExaminationByMonth
	 * @Description: 根据平台管理--设备近一年增长
	 * @param
	 * @return: cn.meiot.entity.vo.Result
	 */
	@GetMapping("allExaminationByMonth")
	@Log(operateContent = "平台管理--设备近一年增长",operateModule="设备服务")
	public Result allExaminationByMonth() {
		long date = System.currentTimeMillis();
		calendar.setTimeInMillis(date);
		int year = calendar.get(Calendar.YEAR);//获取年份
		int month = calendar.get(Calendar.MONTH) + 1;//获取月份

		int startYear = 0;
		int startMonth = 0;

		List<Map<String, Object>> mapList = new ArrayList<>();
		//跨年
		if(month < 12) {
			startMonth = month + 1;
			 startYear = year - 1;
			 //查询当年的
			mapList = equipmentMapper.allExaminationByMonth(year, 1, month);
			//查询去年的
			List<Map<String, Object>> mapListS = equipmentMapper.allExaminationByMonth(startYear, startMonth, 12);
			if(!CollectionUtils.isEmpty(mapListS)) {
				mapList.addAll(mapListS);
			}
		}else {
			mapList = equipmentMapper.allExaminationByMonth(year, 1, month);
			startYear = year;
			startMonth = 1;
		}
		List<Map<String, Object>> mapListData = DateUtil.toTimeData(null, null,
				mapList, startYear, startMonth, year, month);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(mapListData);
		return defaultTrue;
	}

	/**
	 * 下发设备开关列表查询命令
	 * @param cond
	 * @return
	 */
	@GetMapping("selectSwitch")
	@Log(operateContent = "下发设备开关列表查询命令",operateModule="设备服务")
	public Result selectSwitch(String serialNumber) {
		if(StringUtils.isBlank(serialNumber)) {
			return Result.getDefaultFalse();
		}
		equipmentUserService.authentication(serialNumber,getMainUserId());
		return equipmentService.selectSwitch(serialNumber);
	}

	/**
	 * 组织架构的设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("listEquial")
	//@Log(operateContent = "下发设备开关列表查询命令",operateModule="设备服务")
	public Result listEquialStatus(Long buildingId, Integer equipmentStatus) {
		if(buildingId == null) {
			return Result.getDefaultFalse();
		}
		Integer projectId = getProjectId();
		Long userId = getUserId();
		Long mainUserId = getMainUserId();
		List<String> listEquial = equipmentUserService.listEquial(buildingId,userId,mainUserId,projectId,equipmentStatus);
		if (CollectionUtils.isEmpty(listEquial)){
			return Result.OK(new ArrayList<>());
		}
		List<EquialStatusVo> statusVoList = equipmentService.listEquialStatus(listEquial,equipmentStatus,mainUserId,projectId);
		return Result.OK(statusVoList);
	}

	/**
	 * 组织架构的设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("equialNumber")
	//@Log(operateContent = "下发设备开关列表查询命令",operateModule="设备服务")
	public Result listEquialNumber(Long buildingId) {
		if(buildingId == null) {
			return Result.getDefaultFalse();
		}
		Integer projectId = getProjectId();
		Long userId = getUserId();
		Long mainUserId = getMainUserId();
		List<String> listEquial = equipmentUserService.listEquial(buildingId,userId,mainUserId,projectId,0);
		Map map  = equipmentUserService.listEquialNumber(listEquial,projectId);
		return Result.OK(map);
	}

	/**
	 * 组织架构的设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("findAddressAndName")
	//@Log(operateContent = "下发设备开关列表查询命令",operateModule="设备服务")
	public Result findAddressAndName(String serialNumber) {
		if(StringUtils.isBlank(serialNumber)) {
			return Result.getDefaultFalse();
		}
		Integer projectId = getProjectId();
		Map map = equipmentUserService.findAddressAndName(serialNumber,projectId);
		return Result.OK(map);
	}

    /**
     * 组织架构的设备列表
     * @param cond
     * @return
     */
    @GetMapping("findAddressAndNameBySwitchSn")
    //@Log(operateContent = "下发设备开关列表查询命令",operateModule="设备服务")
    public Result findAddressAndNameBySwitchSn(String switchSn) {
        if(StringUtils.isBlank(switchSn)) {
            return Result.getDefaultFalse();
        }
        Integer projectId = getProjectId();
        Long mainUserId = getMainUserId();
        Map map = equipmentUserService.findAddressAndNameBySwitchSn(projectId,switchSn,mainUserId);
        return Result.OK(map);
    }
}
