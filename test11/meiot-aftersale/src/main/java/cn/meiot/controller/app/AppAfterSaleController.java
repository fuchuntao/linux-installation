package cn.meiot.controller.app;

import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.Maintenance;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IMaintenanceService;
import cn.meiot.utils.DateUtil;
import cn.meiot.utils.UserInfoUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package cn.meiot.controller.app
 * @Description:
 * @author: 武有
 * @date: 2019/9/18 10:57
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("AppAfterSale")
@SuppressWarnings("all")
public class AppAfterSaleController extends BaseController {


    @Autowired
    private IMaintenanceService iMaintenanceService;

    @Autowired
    private UserInfoUtil userInfoUtil;
    /**
     * 添加保修记录
     */
    @PostMapping("addAfterSale")
    @Log(operateContent = "添加报修记录",operateModule = "售后服务")
    public Result addAfterSale(@RequestBody Map map, HttpServletRequest request){
        //设备序列号
        String serialNumber= (String) map.get("serialNumber");
        //故障描述
        String reason = (String) map.get("reason");
        //图片地址
        String img= JSONObject.toJSONString(map.get("img"));
        //故障类型ID
        Integer type= (Integer) map.get("type");
        //用户ID
        Long userId=getUserId();
        //账户
        String account=userInfoUtil.getUserInfo().getUser().getUserName();

        if (StringUtils.isEmpty(serialNumber)){
            return Result.getDefaultFalse().Faild("设备序列号不能为空");
        }
        if (null==type){
            return Result.getDefaultFalse().Faild("类型id不能为空");
        }

        Maintenance maintenance=new Maintenance();
        maintenance.setSerialNumber(serialNumber);
        maintenance.setReason(reason);
        maintenance.setImgPath(img);
        maintenance.setMType(type);
        maintenance.setReportTime(DateUtil.getCurrentTime());
        maintenance.setUserId(userId);
        maintenance.setMStatus(1);
        maintenance.setAccount(account);
        return iMaintenanceService.saveMaintenance(maintenance);
    }

    @GetMapping(value = "getAfterSaleByPage")
    @Log(operateContent = "查询报修列表",operateModule = "售后服务")
    public Result getAfterSaleByPage(@RequestParam(value = "pageSize",required = true)Integer pageSize,
                                     @RequestParam(value = "pageNumber",required = true)Integer pageNumber,
                                     @RequestParam(value = "deviceId",required = false)String deviceId,
                                     @RequestParam(value = "aultfType",required = false)Integer aultfType,
                                     @RequestParam(value = "status",required = false) Integer status){
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("pageSize",pageSize);
        paramMap.put("pageNumber",(pageNumber-1)*pageSize);
        paramMap.put("deviceId",deviceId);
        paramMap.put("aultfType",aultfType);
        paramMap.put("status",status);
        paramMap.put("userId",getUserId());
        return iMaintenanceService.getAfterSaleByPage(paramMap);
    }

    @GetMapping("getAfterSaleById")
    @Log(operateContent = "通过ID获取报修记录",operateModule = "售后服务")
    public Result getAfterSaleById(@RequestParam("id") Long id){
        return iMaintenanceService.getAfterSaleById(id,getUserId());
    }

}
