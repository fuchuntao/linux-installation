package cn.meiot.mapper;

import cn.meiot.entity.WaterStatistics;
import cn.meiot.entity.vo.WaterStatisticsDto;
import cn.meiot.entity.vo.WaterStatisticsVo;
import cn.meiot.entity.water.Record;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 符纯涛
 * @since 2020-02-24
 */
@Mapper
public interface WaterStatisticsMapper extends BaseMapper<WaterStatistics> {
    /**
     *
     * @Title: queryWaterMeter
     * @Description: 查询列表
     * @param waterStatisticsDto
     * @return: java.util.List<cn.meiot.entity.WaterStatistics>
     */
//    List<WaterStatisticsVo> queryWaterMeter(WaterStatisticsDto waterStatisticsDto);

    IPage<WaterStatisticsVo> queryWaterMeter(Page<?> page, @Param("waterStatisticsDto") WaterStatisticsDto waterStatisticsDto);




    /**
     *
     * @Title: queryWaterMeterId
     * @Description: 查询当前最大的抄表id
     * @param
     * @return: java.lang.Long
     */
    Long queryWaterMeterId(@Param("waterStatistics") WaterStatistics waterStatistics);


    /**
     *
     * @Title: saveWaterMeter
     * @Description: 更新抄表统计数据
     * @param waterStatisticsList
     * @return: java.lang.Integer
     */
    Integer saveWaterMeter(@Param("list") List<WaterStatistics> waterStatisticsList);


    /**
     *
     * @Title: queryWaterMeter
     * @Description: excel导出
     * @param waterStatisticsDto
     * @return: java.util.List<cn.meiot.entity.vo.WaterStatisticsVo>
     */
    List<WaterStatisticsVo> queryWaterMeter(@Param("waterStatisticsDto") WaterStatisticsDto waterStatisticsDto);



    /**
     *
     * @Title: queryWaterMeterByListId
     * @Description: 查询当前抄表数据的水表编号列表
     * @param
     * @return: java.util.Set<java.lang.Long>
     */
    List<Long> queryWaterMeterBySetId();



    /**
     *
     * @Title: selectWaterMeterList
     * @Description: 查询数据库的所有的数据
     * @param waterStatisticsDto
     * @return: java.util.List<cn.meiot.entity.WaterStatisticsMonths>
     */
    List<WaterStatistics> selectWaterMeterList(@Param("waterStatisticsDto") WaterStatisticsDto waterStatisticsDto);



    /**
     *
     * @Title: selectWater
     * @Description: 获取该水表编号离修改最小的抄表时间的抄表值（为基准值）
     * @param waterStatisticsDto
     * @return: java.math.BigDecimal
     */
    WaterStatistics selectWater(@Param("waterStatisticsDto") WaterStatisticsDto waterStatisticsDto, @Param("type") Integer type);





}
