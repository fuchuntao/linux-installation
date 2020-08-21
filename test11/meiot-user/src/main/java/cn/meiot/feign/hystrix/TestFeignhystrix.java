package cn.meiot.feign.hystrix;

import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SysMsgVo;
import cn.meiot.feign.TestFeign;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TestFeignhystrix implements TestFeign {

    @Override
    public Result test() {
        System.out.println("超时");
        return Result.getDefaultFalse();
    }
}
