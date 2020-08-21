package cn.meiot.mapper;

import cn.meiot.entity.PcCurrentHours;
import cn.meiot.entity.PcLeakageHours;
import cn.meiot.entity.vo.ParametersDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业平台天数据统计表 Mapper 接口
 * </p>
 *
 * @author fuchuntao
 * @since 2020-07-17
 */
@Mapper
public interface PcCurrentHoursMapper extends BaseMapper<PcCurrentHours> {

    /**
     *
     * @Title: currentData
     * @Description: 二代协议查询近12个小时的电流
     * @param pcCurrentHours
     * @param startTime
     * @param endTime
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String, Object>> currentData(@Param("pcCurrentHours") PcCurrentHours pcCurrentHours,
                                          @Param("startTime") Long startTime,
                                          @Param("endTime") Long endTime);


    /**
     *
     * @Title: queryStatisticsByDay
     * @Description: 二代协议查询到具体的某一天的数据
     * @param parametersDto
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String, Object>> queryStatisticsByDay(ParametersDto parametersDto);




    /**
     *
     * @Title: queryMeterByType
     * @Description: 获取本月的数据
     * @param parametersDto
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> queryMeterByType(ParametersDto parametersDto);
}
