package cn.meiot.mapper;

import cn.meiot.entity.AppMeterMonths;
import cn.meiot.entity.AppMeterYears;
import cn.meiot.entity.bo.BatteryLeftBo;
import cn.meiot.entity.vo.AppMeterMonthsVo;
import cn.meiot.entity.vo.AppMeterVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-19
 */
@Mapper
public interface AppMeterMonthsMapper extends BaseMapper<AppMeterMonths> {

    /**
     * 获取按月的统计数据
     * @param appMeterVo
     * @return
     */
    List<Map<String, Object>> getStatslastmonthly(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     * 获取按年统计数据列表
     * @param appMeterVo
     * @return
     */
    BigDecimal getSumMeterByMonth(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     * 将上个月的电流总数统计出来存放到年度表中
     * @param year
     * @param month
     * @return
     */
    List<AppMeterYears> getLastMonthTotalMeter(@Param("year") int year,@Param("month") int month);

    /**
     * 查询指定月份的所有设备号
     * @param year
     * @param month
     * @return
     */
    //List<String> getLastMonthSerialNumber(@Param("year")int year,@Param("month") int month);

    /**
     * 查询月统计列表怕
     * @param appMeterVo
     * @return
     */
    List<AppMeterMonthsVo> selectListBySerialNumber(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     * 获取当月的统计的数据
     * @param map
     * @return
     */
    BatteryLeftBo getBatteryLeftMonth(@Param("map") Map<String, Object> map);


}
