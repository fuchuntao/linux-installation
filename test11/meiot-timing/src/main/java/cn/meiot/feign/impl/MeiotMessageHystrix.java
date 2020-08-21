package cn.meiot.feign.impl;

import cn.meiot.entity.vo.Result;
import cn.meiot.feign.MeiotMessageFeign;
import org.springframework.stereotype.Service;

@Service
public class MeiotMessageHystrix implements MeiotMessageFeign {
    @Override
    public Result repetitionSend() {
        Result result = Result.getDefaultFalse();
        result.setMsg("超时");
        return result;
    }
}
