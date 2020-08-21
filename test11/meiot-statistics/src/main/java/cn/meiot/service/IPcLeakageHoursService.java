package cn.meiot.service;

import cn.meiot.entity.PcLeakageHours;
import cn.meiot.entity.vo.Result;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 企业平台天数据统计表 服务类
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21
 */
public interface IPcLeakageHoursService extends IService<PcLeakageHours> {

    /**
     *
     * @Title: leakageData
     * @Description: 企业端首页获取近12个小时的电流
     * @param pcLeakageHours
     * @param startTime
     * @param endTIme
     * @return: cn.meiot.entity.vo.Result
     */
    Result leakageData(PcLeakageHours pcLeakageHours, Long startTime, Long endTIme);
	 
}
