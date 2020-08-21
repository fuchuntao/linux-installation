package cn.meiot.controller.pc;

import cn.meiot.aop.Log;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Package cn.meiot.controller.pc
 * @Description:
 * @author: 武有
 * @date: 2019/9/17 15:05
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("AfterSaleType")
public class AfterSaleTypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("getTypeList")
    @Log(operateContent = "获取报修类型列表",operateModule = "售后服务")
    public Result getTypeList(){
        System.out.println("---------------------------------------------");
        Result result = Result.getDefaultTrue();
        result.setData(typeService.getTypeList());
//        Result result=Result.getDefaultFalse()
        return result;
    }

    @PostMapping("addType")
    @Log(operateContent = "添加报修类型")
    public Result addType(@RequestBody Map map){
        String name = (String) map.get("name");
        if (null == name) {
            return Result.getDefaultFalse().Faild("参数不能为空");
        }
        return typeService.addType(name);
    }

}
