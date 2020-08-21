package cn.meiot.feign;

import cn.meiot.entity.vo.Result;
import cn.meiot.feign.impl.MeiotMessageHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "meiot-message",fallback = MeiotMessageHystrix.class)
public interface MeiotMessageFeign {

    /**
     * 开始重复推送用户未查看的重要信息
     * @return
     */
    @RequestMapping(value = "/app/fault-msg/repetitionSend",method = RequestMethod.GET)
    Result repetitionSend();
}
