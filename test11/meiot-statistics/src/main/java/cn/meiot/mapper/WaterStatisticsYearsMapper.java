package cn.meiot.mapper;

import cn.meiot.entity.WaterStatisticsYears;
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
public interface WaterStatisticsYearsMapper extends BaseMapper<WaterStatisticsYears> {



    /**
     *
     * @Title: WaterYearsListByMeterIdList
     * @Description: 查询某个年的每个月用水量
     * @param waterStatisticsDto
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String, Object>> WaterYearsListByMeterIdList(WaterStatisticsDto waterStatisticsDto);

    /**
     *
     * @Title: selectWaterYearByMeterId
     * @Description: 根据年月水表编号查询是否有数据
     * @param waterStatisticsDto
     * @return: cn.meiot.entity.WaterStatisticsYears
     */
    WaterStatisticsYears selectWaterYearByMeterId(WaterStatisticsDto waterStatisticsDto);

}
