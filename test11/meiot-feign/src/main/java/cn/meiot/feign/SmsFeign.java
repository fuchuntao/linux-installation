package cn.meiot.feign;

import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SmsVo;
import cn.meiot.feign.hystrix.SmsFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "meiot-message",fallback = SmsFeignHystrix.class)
public interface SmsFeign {

    @RequestMapping(value = "/sms/getSms",method = RequestMethod.POST)
    Result getSms(@RequestBody SmsVo smsVo);

    @RequestMapping(value = "/sms/hello",method = RequestMethod.GET)
    String hello();


}
