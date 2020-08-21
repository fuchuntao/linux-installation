package cn.meiot.controller.app;


import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.controller.MaintenanceController;
import cn.meiot.entity.Maintenance;
import cn.meiot.entity.vo.MaintenanceVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.AccountType;
import cn.meiot.service.IMaintenanceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-28
 */
@RestController
@RequestMapping("app/maintenance")
@Slf4j
public class AppMaintenanceController extends MaintenanceController {

    @Autowired
    private IMaintenanceService maintenanceService;


    /**
     * 查询某人的报修列表
     * @param current  当前页
     * @param pageSize 每页展示多少条记录
     * @return
     */
    @GetMapping(value = "/list")
    public Result list(@RequestParam("current") Integer current, @RequestParam("pageSize") Integer pageSize){
        return listByType(current,pageSize, AccountType.PERSONAGE.value());
    }

    /**
     * 新增报修订单
     * @param maintenanceVo
     * @return
     */
    @PostMapping(value = "/add")
    @Log(operateContent = "新增报修订单",operateModule = "售后服务")
    public Result add(@RequestBody MaintenanceVo maintenanceVo){
        //对象复制
        Maintenance maintenance = maintenanceVo.convertToMaintenanceDTO();

        maintenance.setUserId(getUserId());
        return maintenanceService.saveMaintenance(maintenance);
    }


}
