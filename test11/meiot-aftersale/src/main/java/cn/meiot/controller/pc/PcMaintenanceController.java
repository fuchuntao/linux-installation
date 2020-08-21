package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.common.enums.MaintenanceStatusEnum;
import cn.meiot.controller.MaintenanceController;
import cn.meiot.entity.Maintenance;
import cn.meiot.enums.AccountType;
import cn.meiot.utils.ErrorCodeConstant;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IMaintenanceService;
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
@RequestMapping("pc/maintenance")
@Slf4j
public class PcMaintenanceController extends MaintenanceController {

    @Autowired
    private IMaintenanceService maintenanceService;


    /**
     * 查询某人的报修列表
     * @param current  当前页
     * @param pageSize 每页展示多少条记录
     * @return
     */
    @GetMapping(value = "/list")
    @Log(operateContent = "查询某人的报修列表",operateModule = "售后服务")
    public Result list(@RequestParam("current") Integer current, @RequestParam("pageSize") Integer pageSize){
        return listByType(current,pageSize, AccountType.PLATFORM.value());
    }




    /**
     *
     * @param type
     * @param id
     * @return
     */
    @PutMapping(value = "/accept/{type}/{id}")
    public Result accept(@PathVariable("type") Integer type,@PathVariable("id") Long id){
        if(null == type ){
            log.info("类型不可为空");
            return new Result().Faild(ErrorCodeConstant.TYPE_CANNOT_BE_EMPTY);
        }
        if(MaintenanceStatusEnum.ACCEPT.value() != type && MaintenanceStatusEnum.DISPOSE.value() != type ){
            log.info("前端传递的类型：{}，不符合要求",type);
            return new Result().Faild(ErrorCodeConstant.INCORRECT_TYPE);
        }
        return maintenanceService.updateStatus(type,id);
    }

}
