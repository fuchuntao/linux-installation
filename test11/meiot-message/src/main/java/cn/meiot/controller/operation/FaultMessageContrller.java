package cn.meiot.controller.operation;

import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.FaultMessage;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IFaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Package cn.meiot.controller.operation
 * @Description:
 * @author: 武有
 * @date: 2019/9/25 15:19
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("operation")
public class FaultMessageContrller extends BaseController {

    @Autowired
    private IFaultMessageService iFaultMessageService;
    @Autowired
    private UserFeign userFeign;

    @GetMapping("getFaultMessage")
    @Log(operateContent = "获取故障消息列表", operateModule = "消息服务")
    public Result getFaultMessage(
            @RequestParam(value = "current", required = true) Integer current,
            @RequestParam(value = "pageSize", required = true) Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "faultType", required = false) Integer faultType,
            @RequestParam(value = "distributionBoxName", required = false) String distributionBoxName) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectId", getProjectId());
        map.put("current", (current - 1) * pageSize);
        map.put("pageSize", pageSize);
        map.put("status", status);
        map.put("faultType", faultType);
        map.put("distributionBoxName", distributionBoxName);
        map.put("userId", getUserId());
        return iFaultMessageService.getFaultMessageList(map);
    }

    @PostMapping("updateStatus")
    @Log(operateContent = "更新故障消息状态", operateModule = "消息服务")
    public Result updateStatus(@RequestBody FaultMessage faultMessage) {
        if (null == faultMessage.getId()) {
            return Result.getDefaultFalse();
        }
        if (null == faultMessage.getSwitchStatus()) {
            return Result.getDefaultFalse();
        }
        if (!iFaultMessageService.updateById(faultMessage)) {
            return Result.getDefaultFalse();
        }
        return Result.getDefaultTrue();
    }


    @RequestMapping("test01")
    public void test01() {
        Long userId = getUserId();
        Long mainUserId = userFeign.getMainUserIdByUserId(userId);
    }
}
