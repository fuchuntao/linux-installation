package cn.meiot.controller.pc;

import cn.meiot.aop.UpgradeDetection;
import cn.meiot.client.UserClient;
import cn.meiot.utils.ConstantsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.meiot.aop.Log;
import cn.meiot.entity.bo.Crcuit;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.CrcuitService;

@RestController
@RequestMapping("pc/crcuit")
public class PcCrcuitController extends PcBaseController{
	
	@Autowired
	private CrcuitService crcuitService;

	@Autowired
	private UserClient userClient;
	
	@GetMapping("query")
	@Log(operateContent = "断路器参数查询",operateModule="设备服务")
	public Result query(String mode) {
		Integer projectId = getProjectId();
		return crcuitService.query(projectId,mode);
	}
	
	@PostMapping("update")
	//@UpgradeDetection
	@Log(operateContent = "断路器参数修改",operateModule="设备服务")
	public Result update(@RequestBody Crcuit crcuit) {
		Integer projectId = getProjectId();
		Long userId = getMainUserId();
		return crcuitService.update(crcuit,projectId,userId);
	}

	@GetMapping("queryCircuitParameter")
	@Log(operateContent = "断路器默认参数查询",operateModule="设备服务")
	public Result queryCircuitParameter() {
		return Result.OK(userClient.getConfigValueByKey(ConstantsUtil.CIRCUIT_PARAMETER));
	}
}
