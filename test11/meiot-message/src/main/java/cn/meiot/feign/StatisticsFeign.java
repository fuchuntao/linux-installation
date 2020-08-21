package cn.meiot.feign;

import cn.meiot.feign.hystrix.DeviceFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @Package cn.meiot.feign
 * @Description:
 * @author: 武有
 * @date: 2020/2/26 16:55
 * @Copyright: www.spacecg.cn
 */
@FeignClient(value = "meiot-statistics"/*,fallback = DeviceFeignHystrix.class*/)
public interface StatisticsFeign {
    /**
     * 获取设备在线率
     * @param userId   用户id
     * @param projectId   项目id ，项目id为空或者0时表示个人
     * @return
     */
    @RequestMapping(value = "/pc-device/getDeviceLine",method = RequestMethod.GET)
    public BigDecimal getDeviceLine(@RequestParam("userId") Long userId, @RequestParam(value = "projectId",required = false) Integer projectId );
}
