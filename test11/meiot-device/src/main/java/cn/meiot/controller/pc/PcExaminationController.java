package cn.meiot.controller.pc;

import java.util.List;
import java.util.Map;

import cn.meiot.service.BuildingService;
import cn.meiot.service.EquipmentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import cn.meiot.aop.Log;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.EquipmentService;
import cn.meiot.service.ExaminationService;
import cn.meiot.utils.RedisConstantUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("pc/examination")
@Slf4j
public class PcExaminationController extends PcBaseController{
	
	@Autowired
	private ExaminationService examinationService;
	
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private BuildingService buildingService;

	@Autowired
	private EquipmentUserService equipmentUserService;
	/**
	 * 查找building设备列表
	 * @param cond
	 * @return
	 */
	@GetMapping("query")
	@Log(operateContent = "漏电自检查询",operateModule="设备服务")
	public Result query(PcEquipmentUserCond cond) {
		Long mainUserId = getMainUserId();
		Long userId = getUserId();
		Integer projectId = getProjectId();
		cond.setUserId(mainUserId);
		cond.setProjectId(projectId);
		List<Integer> listRole = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES+getUserId());
		log.info("查询漏电自检数据:当前用户{},主账户:{},角色:{}",userId,mainUserId,listRole);
		if(!mainUserId.equals(userId)) {
			if(CollectionUtils.isEmpty(listRole)) {
				return Result.getDefaultTrue();
			}
			cond.setListRole(listRole);
		}
		Long id = cond.getId();
		if(id != null && !id.equals(0)){
			List<Long> longs = buildingService.queryBuildingIds(id, projectId, mainUserId, userId);
			cond.setBuildingList(longs);
		}
		Result result = examinationService.query(cond);
		return result;
	}
	
	/**
	 * 查询自检历史
	 * @param cond
	 * @return
	 */
	@GetMapping("queryBySerialNumber")
	@Log(operateContent = "漏电自检历史",operateModule="设备服务")
	public Result queryBySerialNumber(PcEquipmentUserCond cond) {
		Long mainUserId = getMainUserId();
		cond.setUserId(mainUserId);
		cond.setProjectId(getProjectId());
		Result result = examinationService.queryBySerialNumber(cond);
		return result;
	}

	/**
	 * 立即自检
	 * @param
	 * @return
	 */
	@PostMapping("test")
	@Log(operateContent = "立即自检",operateModule="设备服务")
	public Result test(@RequestBody Map map) {
		String serialNumber = (String) map.get("serialNumber");
		Long mainUserId = getMainUserId();
		equipmentUserService.authentication(serialNumber,mainUserId);
		Result result = examinationService.test(serialNumber);
		return result;
	}
}
