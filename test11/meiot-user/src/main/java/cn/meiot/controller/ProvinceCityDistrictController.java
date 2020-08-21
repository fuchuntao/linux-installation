package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.ProvinceCityDistrict;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IProvinceCityDistrictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 省市县数据表 前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-26
 */
@RestController
@RequestMapping("/pcd")
public class ProvinceCityDistrictController {

    @Autowired
    private IProvinceCityDistrictService provinceCityDistrictService;

    /**
     * 获取省市区列表
     * @param pid 父id
     * @return
     */
    @GetMapping(value = "/list")
    public Result list(@RequestParam("pid") Integer pid){
        Result result = Result.getDefaultTrue();
        List<ProvinceCityDistrict> list = provinceCityDistrictService.list(new QueryWrapper<ProvinceCityDistrict>().eq("pid", pid));
        result.setData(list);
        return result;

    }
}
