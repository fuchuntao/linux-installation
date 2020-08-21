package cn.meiot.mapper;

import cn.meiot.entity.WaterStatistics;
import cn.meiot.entity.WaterStatisticsMonths;
import cn.meiot.entity.vo.WaterStatisticsDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
@Mapper
public interface WaterStatisticsMonthsMapper extends BaseMapper<WaterStatisticsMonths> {



    /**
     *
     * @Title: selectWaterMeter
     * @Description: 查询month数据库是否为空
     * @param
     * @return: java.lang.Integer
     */
    Integer selectWaterMeter();




    /**
     *
     * @Title: selectWaterMeterMonthsList
     * @Description: 查询数据库的所有的数据
     * @param waterStatisticsDto
     * @return: java.util.List<cn.meiot.entity.WaterStatisticsMonths>
     */
    List<WaterStatisticsMonths> selectWaterMeterMonthsList(@Param("waterStatisticsDto") WaterStatisticsDto waterStatisticsDto);




    /**
     *
     * @Title: WaterMonthsListByMeterIdList
     * @Description: 查询某个月的天用水量
     * @param waterStatisticsDto
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String, Object>> WaterMonthsListByMeterIdList(WaterStatisticsDto waterStatisticsDto);



    /**
     *
     * @Title: saveWaterMeter
     * @Description: 更新抄表月表统计数据
     * @param waterStatisticsList
     * @return: java.lang.Integer
     */
    Integer updateWaterMeter(@Param("list") List<WaterStatisticsMonths> waterStatisticsList);




    /**
     *
     * @Title: selectWaterByMeterId
     * @Description: 根据年月日 水表编号查询数据
     * @param waterStatisticsDto
     * @return: cn.meiot.entity.WaterStatisticsMonths
     */
    WaterStatisticsMonths selectWaterByMeterId(WaterStatisticsDto waterStatisticsDto);

}
