package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.FaultTypeMapper;
import cn.meiot.service.IFaultTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-24
 */
@RestController
@RequestMapping("/fault-type")
public class FaultTypeController {

    @Autowired
    private IFaultTypeService iFaultTypeService;
    @GetMapping("getTypeList")
    @Log(operateContent = "获取故障消息类型列表",operateModule = "消息服务")
    public Result getTypeList(){

        return iFaultTypeService.getFaultTypeList();
    }

}
