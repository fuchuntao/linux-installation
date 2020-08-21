package cn.meiot.service;

import cn.meiot.entity.PcCurrentHours;
import cn.meiot.entity.PcLeakageHours;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业平台天数据统计表 服务类
 * </p>
 *
 * @author fuchuntao
 * @since 2020-07-17
 */
public interface IPcCurrentHoursService extends IService<PcCurrentHours> {


    /**
     *
     * @Title: leakageData
     * @Description: 企业端首页获取近12个小时的电流
     * @param pcCurrentHours
     * @param startTime
     * @param endTIme
     * @return: cn.meiot.entity.vo.Result
     */
    Result currentData(PcCurrentHours pcCurrentHours, Long startTime, Long endTIme);




    /**
     *
     * @Title: queryStatisticsByDay
     * @Description: 统计天的数据
     * @param parametersDto
     * @return: cn.meiot.entity.vo.Result
     */
    List<Map<String,Object>> queryStatisticsByDay(ParametersDto parametersDto);


    /**
     *
     * @Title: queryMeterByType
     * @Description: 如果年相等则统计电量要统计到当月的  不包含当天的电量
     * @param time
     * @param type  type 0：年， 1：月， 2：日
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    Map<String,Object> queryMeterByType(ParametersDto parametersDto);


}
