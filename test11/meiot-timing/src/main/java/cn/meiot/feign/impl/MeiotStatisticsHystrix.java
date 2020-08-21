package cn.meiot.feign.impl;

import cn.meiot.entity.vo.Result;
import cn.meiot.feign.MeiotStatisticsFeign;
import org.springframework.stereotype.Service;

@Service
public class MeiotStatisticsHystrix implements MeiotStatisticsFeign {
    @Override
    public Result dayStatistics() {
        Result result = Result.getDefaultFalse();
        result.setMsg("超时");
        result.setCode("-2");
        return result;
    }

    @Override
    public Result monthStatistics() {
        Result result = Result.getDefaultFalse();
        result.setMsg("超时");
        result.setCode("-2");
        return result;
    }
}
