package cn.meiot.controller.pc;

import java.util.List;

import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.meiot.aop.Log;
import cn.meiot.entity.db.SwitchType;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SwitchTypeVo;
import cn.meiot.service.SwitchTypeService;

@RestController
@RequestMapping(value = "pc/switchType")
public class PcSwitchTypeController extends PcBaseController{
	
	@Autowired
	private SwitchTypeService switchTypeService;

	/**
	 * [新增]
	 * 
	 * @author 大狼狗
	 * @date 2019/10/24
	 **/
	@PostMapping("insert")
	@Log(operateContent = "设置-添加开关类型",operateModule="设备服务")
	public Result insert(@RequestBody SwitchType switchType) {
		Integer projectId = getProjectId();
		boolean flag = switchTypeService.queryNameCount(switchType.getName(),projectId,switchType.getId());
		if (flag){
			return Result.getDefaultFalse();
		}
		switchType.setProjectId(projectId);
		return switchTypeService.insert(switchType);
	}

	/**
	 * [刪除]
	 * 
	 * @author 大狼狗
	 * @date 2019/10/24
	 **/
	@PostMapping("delete")
	@Log(operateContent = "设置-删除开关类型",operateModule="设备服务")
	public Result delete(@RequestBody List<Long> ids) {
		//switchType.setProjectId(getProjectId());
		return switchTypeService.delete(ids);
	}

	/**
	 * [更新]
	 * 
	 * @author 大狼狗
	 * @date 2019/10/24
	 **/
	@PostMapping("update")
	@Log(operateContent = "设置-修改开关类型",operateModule="设备服务")
	public Result update(@RequestBody SwitchType switchType) {
		Integer projectId = getProjectId();
		boolean flag = switchTypeService.queryNameCount(switchType.getName(),projectId,switchType.getId());
		if (flag){
			return Result.getDefaultFalse();
		}
		switchType.setProjectId(projectId);
		return switchTypeService.update(switchType);
	}

	/**
	 * [更新]
	 * 
	 * @author 大狼狗
	 * @date 2019/10/24
	 **/
	@GetMapping("query")
	@Log(operateContent = "设置-查询开关类型",operateModule="设备服务")
	public Result query(PcEquipmentUserCond cond) {
		cond.setProjectId(getProjectId());
		return switchTypeService.query(cond);
	}

	/**
	 * [更新]
	 *
	 * @author 大狼狗
	 * @date 2019/10/24
	 **/
	@GetMapping("queryNameCount")
	@Log(operateContent = "设置-查询开关类型",operateModule="设备服务")
	public Result queryNameCount(String name,Long id) {
		Integer projectId = getProjectId();
		boolean flag = switchTypeService.queryNameCount(name,projectId,id);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(flag);
		return defaultTrue;
	}
	
	/**
	 * [更新]
	 * 
	 * @author 大狼狗
	 * @date 2019/10/24
	 **/
	@GetMapping("querySwitch")
	//@Log(operateContent = "")
	public List<SwitchTypeVo> querySwitch(Integer projectId) {
		return switchTypeService.querySwitch(projectId);
	}
}
