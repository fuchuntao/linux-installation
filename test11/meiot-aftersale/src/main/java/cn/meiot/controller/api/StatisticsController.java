package cn.meiot.controller.api;

import cn.meiot.entity.vo.StatisticsVo;
import cn.meiot.service.IMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Package cn.meiot.controller.api
 * @Description:
 * @author: 武有
 * @date: 2019/9/27 11:24
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("api")
public class StatisticsController {

    @Autowired
    private IMaintenanceService maintenanceService;

    @GetMapping("statistics")
    public List<StatisticsVo> getStatistics(@RequestParam(value = "serialNumber",required = false)String serialNumber){
        return  maintenanceService.getStatistics(serialNumber);
    }
}
