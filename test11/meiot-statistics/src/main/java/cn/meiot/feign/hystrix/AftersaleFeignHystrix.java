package cn.meiot.feign.hystrix;

import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatisticsVo;
import cn.meiot.feign.AftersaleFeign;
import cn.meiot.feign.DeviceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * @ClassName: AftersaleFeign
 * @Description: 获取售后服务报修状态返回类
 * @author: 符纯涛
 * @date: 2019/9/27
 */
@Slf4j
@Service
public class AftersaleFeignHystrix implements AftersaleFeign {
    @Override
    public List<StatisticsVo> getAfterSaleStatistics(String serialNumber) {
        log.info("获取设备报修状态统计错误！");
        return null;
    }
}
