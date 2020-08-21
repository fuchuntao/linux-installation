package cn.meiot.feign;

import cn.meiot.entity.vo.EmailVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SysMsgVo;
import cn.meiot.feign.hystrix.TestFeignhystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "meiot-message",fallback = TestFeignhystrix.class)
public interface TestFeign {

    @RequestMapping(value = "/api/test",method = RequestMethod.GET)
    Result<EmailVo> test();
}
