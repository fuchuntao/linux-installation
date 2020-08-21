package cn.meiot.service.impl;

import cn.meiot.entity.PcLeakageHours;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.PcLeakageHoursMapper;
import cn.meiot.service.IPcLeakageHoursService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业平台天数据统计表 服务实现类
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21
 */
@Service
public class PcLeakageHoursServiceImpl extends ServiceImpl<PcLeakageHoursMapper, PcLeakageHours> implements IPcLeakageHoursService {


    @Autowired
    private PcLeakageHoursMapper pcLeakageHoursMapper;
    /**
     *
     * @Title: leakageData
     * @Description: 企业端首页获取近12个小时的电流
     * @param pcLeakageHours
     * @param startTime
     * @param endTime
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result leakageData(PcLeakageHours pcLeakageHours, Long startTime, Long endTime) {
        List<Map<String, Object>> mapList = pcLeakageHoursMapper.leakageData(pcLeakageHours, startTime, endTime);
        return Result.OK(mapList);
    }
}
