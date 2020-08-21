package cn.meiot.controller.pc;

import java.util.List;

import cn.meiot.aop.UpgradeDetection;
import cn.meiot.constart.ProjectConstart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.meiot.aop.Log;
import cn.meiot.entity.db.PcTimerMode;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.time.PcTimerModerDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.PcTimerModeService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingzhiying
 * @title: TimerModeController.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月2日
 */
@RestController
@RequestMapping("pc/timerMode")
@Slf4j
public class PcTimerModeController extends PcBaseController{

	@Autowired
	protected PcTimerModeService pcTimerModeService;

	@GetMapping("query")
	@Log(operateContent = "查询情景模式--定时开关列表",operateModule="设备服务")
	public Result query(PcEquipmentUserCond cond) {
		Long mainUserId = getMainUserId();
		cond.setUserId(mainUserId);
		cond.setProjectId(getProjectId());
		Result result = pcTimerModeService.query(cond);
		return result;
	}
	
	@GetMapping("querySwitch")
	@Log(operateContent = "查询开关定时弹窗--定时开关",operateModule="设备服务")
	public Result querySwitch(PcEquipmentUserCond cond) {
		Long mainUserId = getMainUserId();
		cond.setUserId(mainUserId);
		cond.setProjectId(getProjectId());
		Result result = pcTimerModeService.querySwitch(cond);
		return result;
	}
	
	@GetMapping("queryById")
	@Log(operateContent = "新建编辑时间点的查询--定时开关",operateModule="设备服务")
	public Result queryById(Long id) {
		Long userId = getMainUserId();
		Integer projectId = getProjectId();
		return pcTimerModeService.queryById(id,userId,projectId,getUserId());
	}
	
	@PostMapping("insert")
	@UpgradeDetection
	@Log(operateContent = "添加情景模式--定时开关",operateModule="设备服务")
	public Result insert(@RequestBody PcTimerModerDto pcTimerModerDto) {
		boolean checkList = pcTimerModerDto.checkList();
		if(checkList) {
			return Result.getDefaultFalse();
		}
		PcTimerMode pcTimerMode = pcTimerModerDto.getPcTimerMode();
		pcTimerMode.setFlag(1);
		pcTimerMode.setLoadmax(null);
		Long userId = getMainUserId();
		Integer projectId = getProjectId();
		pcTimerMode.setUserId(userId);
		pcTimerMode.setProjectId(projectId);
		return pcTimerModeService.insert(pcTimerModerDto);
	}


	
	@PostMapping("update")
	@UpgradeDetection
	@Log(operateContent = "修改情景模式--定时开关",operateModule="设备服务")
	public Result update(@RequestBody PcTimerModerDto pcTimerModerDto) {
		boolean checkList = pcTimerModerDto.checkList();
		if(checkList) {
			return Result.getDefaultFalse();
		}
		PcTimerMode pcTimerMode = pcTimerModerDto.getPcTimerMode();
		Long userId = getMainUserId();
		Integer projectId = getProjectId();
		pcTimerMode.setUserId(userId);
		pcTimerMode.setProjectId(projectId);
        pcTimerMode.setFlag(1);
        pcTimerMode.setLoadmax(null);
		return pcTimerModeService.update(pcTimerModerDto);
	}
	
	@PostMapping("delete")
	//@UpgradeDetection
	@Log(operateContent = "删除情景模式--定时开关",operateModule="设备服务")
	public Result delete(@RequestBody PcTimerMode pcTimerMode) {
		if(pcTimerMode.getId()== null) {
			return null;
		}
		Long userId = getMainUserId();
		Integer projectId = getProjectId();
		pcTimerMode.setUserId(userId);
		pcTimerMode.setProjectId(projectId);
		pcTimerMode.setUpdateTime(null);
		log.info("删除数据为:{},当前用户{}",pcTimerMode,getUserId());
		return pcTimerModeService.delete(pcTimerMode);
	}

	@GetMapping("querySwitchList")
	@Log(operateContent = "查询功率情景模式开关列表--定时开关",operateModule="设备服务")
	public Result querySwitchList(PcEquipmentUserCond cond) {
		if(cond.getId()== null) {
			return null;
		}
		Long mainUserId = getMainUserId();
		cond.setUserId(mainUserId);
		cond.setProjectId(getProjectId());
		cond.setTable(ProjectConstart.TIMER);
		return pcTimerModeService.querySwitchList(cond);
	}

	@GetMapping("queryEquipment")
	@Log(operateContent = "查询功率情景模式开关列表--定时开关",operateModule="设备服务")
	public Result queryEquipment(PcEquipmentUserCond cond) {
		if(cond.getId()== null) {
			return null;
		}
		Long mainUserId = getMainUserId();
		cond.setUserId(mainUserId);
		cond.setProjectId(getProjectId());
		return pcTimerModeService.queryEquipment(cond);
	}

	@PostMapping("isSwitch")
	@Log(operateContent = "情景模式打开与关闭",operateModule="设备服务")
	public Result isSwitch(@RequestBody PcTimerMode pcTimerMode) {
		Long mainUserId = getMainUserId();
		Integer projectId = getProjectId();
		//Long userId = getUserId();
		pcTimerMode.setProjectId(projectId);
		pcTimerMode.setUserId(mainUserId);
		return pcTimerModeService.isSwitch(pcTimerMode);
	}

	@GetMapping("isImplement")
	@Log(operateContent = "情景模式打开与关闭",operateModule="设备服务")
	public Result isImplement(Long id,List<String> switchSn) {
		Integer projectId = getProjectId();
		return pcTimerModeService.isImplement(id,switchSn,projectId);
	}
}
