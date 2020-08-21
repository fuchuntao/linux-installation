package cn.meiot.controller;


import cn.meiot.common.enums.MaintenanceStatusEnum;
import cn.meiot.entity.Maintenance;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatusVo;
import cn.meiot.enums.AccountType;
import cn.meiot.service.IMaintenanceService;
import cn.meiot.utils.ErrorCodeConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-28
 */
@RestController
public class MaintenanceController extends BaseController {

    @Autowired
    private IMaintenanceService maintenanceService;


    /**
     * 查询某人的报修列表
     * @param current  当前页
     * @param pageSize 每页展示多少条记录
     * @param type 类型
     * @return
     */
    public Result listByType(Integer current,Integer pageSize,Integer type ){
        IPage<Maintenance> list = null;
        Page<Maintenance> page = new Page<Maintenance>(current,pageSize);
        //判断查询的类型， 企业查询所有   其他根据id查询
        if(AccountType.PLATFORM.value() == type){
            list = maintenanceService.page(page);
        }else{
            list = maintenanceService.page(page,new QueryWrapper<Maintenance>().eq("user_id",getUserId()));
        }
        Result result = Result.getDefaultTrue();
        result.setData(list);
        return result;
    }

    /**
     * 通过id查询详情
     * @param id
     * @return
     */
    @GetMapping(value = "/detailByid")
    public Result detailByid(@RequestParam("id") Long id){
        return maintenanceService.getDetail(id,getUserId());
    }





}
