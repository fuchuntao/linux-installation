package cn.meiot.feign.impl;

import cn.meiot.feign.DeviceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeviceFeignHystrix implements DeviceFeign {
    @Override
    public void timingExamination() {
        log.info("请求设备超时!!!!!!!!!!!!!");
    }

    @Override
    public void haowenTest() {
        log.info("好文测试专用，请求设备超时!!!!!!!!!!!!!");
    }

    @Override
    public Boolean pullWaterMeterData() {
        log.info("拉去水表数据失败，可能服务超时！");
        return false;
    }

}
