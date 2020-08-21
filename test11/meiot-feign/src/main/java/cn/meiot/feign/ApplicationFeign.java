package cn.meiot.feign;

import cn.meiot.entity.ApplicationFeignVo;
import cn.meiot.feign.hystrix.ApplicationFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "meiot-api-application",fallback = ApplicationFeignHystrix.class)
public interface ApplicationFeign {
    /**
     * 通过AppKey查询App详情
     * @param key
     * @return
     */
    @GetMapping("/application/api/key/{key}")
    public ApplicationFeignVo getApplicationInfoByKey(@PathVariable("key") String key);
}
