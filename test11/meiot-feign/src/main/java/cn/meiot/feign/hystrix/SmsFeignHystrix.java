package cn.meiot.feign.hystrix;

import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SmsVo;
import cn.meiot.feign.SmsFeign;
import org.springframework.stereotype.Service;

@Service
public class SmsFeignHystrix implements SmsFeign {


    @Override
    public Result getSms(SmsVo smsVo) {
        Result result = Result.getDefaultFalse();
        result.setMsg("超时");
        return result;
    }

    @Override
    public String hello() {
        return "超时";
    }

}
