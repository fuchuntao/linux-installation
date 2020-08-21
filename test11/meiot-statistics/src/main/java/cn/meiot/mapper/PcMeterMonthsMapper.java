package cn.meiot.mapper;

import cn.meiot.dao.sql.PcMeterYearsProvider;
import cn.meiot.entity.AppMeterYears;
import cn.meiot.entity.PcMeterMonths;
import cn.meiot.entity.PcMeterYears;
import cn.meiot.entity.bo.BatteryLeftBo;
import cn.meiot.entity.bo.MeterStatisticalBo;
import cn.meiot.entity.vo.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业平台月统计表 Mapper 接口
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Mapper
public interface PcMeterMonthsMapper extends BaseMapper<PcMeterMonths> {




    /**
     * 查询月统计列表怕
     * @param appMeterVo
     * @return
     */
    List<AppMeterMonthsVo> selectListBySerialNumberPc(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     * 查询当月的用电总量
     * @param pcDataVo
     * @return
     */
   // @Select("  ")
   // @SelectProvider(type = PcMeterYearsProvider.class,method ="queryNowMonthData" )
    MeterStatisticalBo queryNowMonthData(@Param("pcDataVo") PcDataVo pcDataVo,@Param("list") List<SerialNumberMasterVo> list);


    /**
     *
     * @Title: getMeterByProjectId
     * @Description: 根据主开关获取设备编号的电量
     * @param indexAllByProjectId
     * @return: java.math.BigDecimal
     */
    BigDecimal getMonthMeterByProjectId(@Param("list") List<SerialNumberMasterVo> indexAllByProjectId,
                                        @Param("year") int year,
                                        @Param("month") int month,
                                        @Param("projectId") Integer projectId);

    /**
     *
     * @Title: getMaxMonthMeterByProjectId
     * @Description: 根据主开关获取设备编号月的最高电量
     * @param indexAllByProjectId
     * @return: java.math.BigDecimal
     */
    BigDecimal getMaxMonthMeterByProjectId(@Param("list") List<SerialNumberMasterVo> indexAllByProjectId,
                                           @Param("projectId") Integer projectId,
                                           @Param("year") Integer year);



    /**
     *
     * @Title: getMaxDayMeterByProjectId
     * @Description: 根据主开关获取设备编号日的最高电量
     * @param indexAllByProjectId
     * @return: java.math.BigDecimal
     */
    BigDecimal getMaxDayMeterByProjectId(@Param("list") List<SerialNumberMasterVo> indexAllByProjectId,
                                         @Param("projectId") Integer projectId,
                                         @Param("year") Integer year);



    /**
     * 获取当月的最大值，平均值，最小值
     * @param map
     * @return
     */
    BatteryLeftBo getPcBatteryLeftMonth(@Param("list") List<SerialNumberMasterVo> indexAllByProjectId,
                                        @Param("map") Map<String, Object> map);



    /**
     *
     * @Title: getPcListSwitchMonth
     * @Description: 根据项目id和开关类型获取当月的电量
     * @param list
     * @param projectId
     * @param year
     * @param month
     * @return: java.math.BigDecimal
     */
    BigDecimal getPcListSwitchMonth(@Param("list") List<SwitchVo> list,
                                    @Param("projectId") Integer projectId,
                                    @Param("year") Integer year,
                                    @Param("month") Integer month);
}
