package cn.meiot.mapper;

import cn.meiot.entity.PcLeakageMonths;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.StatisticsDto;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 企业平台月数据统计表 Mapper 接口
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21
 */
@Mapper
public interface PcLeakageMonthsMapper extends BaseMapper<PcLeakageMonths> {



	/**
	 * 添加当月数据
	 * @param list
	 * @param tableName
	 */
	void insertList(@Param("list")List<StatisticsDto> list,@Param("tableName") String tableName);

	/**
	 * 查询数据
	 * @param appMeterVo
	 * @return
	 */
	List<StatisticsDto> selectListBySerialNumber(StatisticsDto appMeterVo);

	/**
	 * 查询本月或本日数据
	 * @param parametersDto
	 * @return
	 */
	Map<String, Object> queryStatisticsMonths(ParametersDto parametersDto);


	/**
	 * 查询本月或本日数据
	 * @param parametersDto
	 * @return
	 */
	Map<String, Object> queryStatisticsMonthsSum(ParametersDto parametersDto);

	/**
	 *
	 * @Title: selectListSerialNumber
	 * @Description: 查询修改的开关的设备号
	 * @param parametersDto
	 * @return: java.util.List<java.lang.Long>
	 */
	List<Long> selectListSerialNumber(ParametersDto parametersDto);



	/**
	 *
	 * @Title: updateStatistics
	 * @Description: 替换开关的统计数据的更改
	 * @param parametersDto
	 * @return: java.util.List<java.lang.Long>
	 */
	int updateStatistics(ParametersDto parametersDto);



}
