package cn.meiot.feign;

import cn.meiot.entity.vo.Result;
import cn.meiot.feign.impl.DeviceFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "meiot-device",fallback = DeviceFeignHystrix.class)
public interface DeviceFeign {
    @RequestMapping(value = "/equipment/timingExamination",method = RequestMethod.GET)
    void timingExamination();
    @RequestMapping(value = "api/geihaowenceshi",method = RequestMethod.GET)
    void haowenTest();

    /**
     * 拉取水表数据
     */
    @RequestMapping(value = "pc/water/systemAddWater",method = RequestMethod.POST)
    Boolean pullWaterMeterData();
}
