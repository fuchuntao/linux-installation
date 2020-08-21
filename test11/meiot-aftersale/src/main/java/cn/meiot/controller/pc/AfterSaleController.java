package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;

import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatusVo;
import cn.meiot.service.IMaintenanceService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Package cn.meiot.controller.pc
 * @Description:
 * @author: 武有
 * @date: 2019/9/17 14:45
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("AfterSale")
@SuppressWarnings("all")
@Slf4j
public class AfterSaleController extends BaseController {


    @Autowired
    private IMaintenanceService maintenanceService;

    @GetMapping(value = "getAfterSaleByPage")
    @Log(operateContent = "查询报修信息列表",operateModule = "售后服务")
    public Result getAfterSaleByPage(@RequestParam(value = "pageSize", required = true) Integer pageSize,
                                     @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
                                     @RequestParam(value = "deviceId", required = false) String deviceId,
                                     @RequestParam(value = "aultfType", required = false) Integer aultfType,
                                     @RequestParam(value = "status", required = false) Integer status) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageSize", pageSize);
        paramMap.put("pageNumber", (pageNumber - 1) * pageSize);
        paramMap.put("deviceId", deviceId);
        paramMap.put("aultfType", aultfType);
        paramMap.put("status", status);
//        paramMap.put("userId",getUserId());
        log.info("查询报修信息列表接受到的参数为：{}", JSONObject.toJSONString(paramMap));

        return maintenanceService.getAfterSaleByPage(paramMap);
    }

    @PostMapping("updateStatus")
    @Log(operateContent = "更新状态",operateModule = "售后服务")
    public Result updateStatus(@RequestBody List<StatusVo> statusVoList) throws Exception {
        return maintenanceService.editStatus(statusVoList);
    }

    @GetMapping("getAfterSaleById")
    @Log(operateContent = "根据Id查询报修详情",operateModule = "售后服务")
    public Result getAfterSaleById(@RequestParam("id") Long id) {
        return maintenanceService.getAfterSaleById(id, null);
    }


//    @GetMapping("statistics")
//    public Result getStatistics(@RequestParam(value = "serialNumber",required = false)String serialNumber){
//        return  maintenanceService.getStatistics(serialNumber);
//    }
}
