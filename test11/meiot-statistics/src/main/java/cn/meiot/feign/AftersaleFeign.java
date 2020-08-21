package cn.meiot.feign;

import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatisticsVo;
import cn.meiot.feign.hystrix.AftersaleFeignHystrix;
//import cn.meiot.feign.hystrix.DeviceFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName: AftersaleFeign
 * @Description: 获取售后服务报修状态
 * @author: 符纯涛
 * @date: 2019/9/27
 */
@FeignClient(value = "meiot-aftersale",fallback = AftersaleFeignHystrix.class)
public interface AftersaleFeign {

    /**
     *
     * @Title: getMasterIndex
     * @Description: 根据设备号获取设备报修状态统计
     * @param serialNumber
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "/api/statistics",method = RequestMethod.GET)
    List<StatisticsVo> getAfterSaleStatistics(@RequestParam(value = "serialNumber", required = false) String serialNumber);

}
