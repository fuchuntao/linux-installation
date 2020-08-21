package cn.meiot.mapper;

import cn.meiot.entity.PcLeakageHours;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.StatisticsDto;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 企业平台天数据统计表 Mapper 接口
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21
 */
@Mapper
public interface PcLeakageHoursMapper extends BaseMapper<PcLeakageHours> {

	List<StatisticsDto> selectListBySerialNumber(StatisticsDto statisticsDto);

	List<Map<String, Object>> queryStatistics(ParametersDto parametersDto);

	List<StatisticsDto> selectStatisticsDtoList(ParametersDto parametersDto);

	List<Map<String, Object>> queryStatisticsSum(ParametersDto parametersDto);

	List<Map<String, Object>> leakageData(@Param("pcLeakageHours") PcLeakageHours pcLeakageHours,
										  @Param("startTime") Long startTime,
										  @Param("endTime") Long endTime);

}
