package cn.meiot.mapper;

import cn.meiot.entity.AppMeterYears;
import cn.meiot.entity.bo.BatteryLeftBo;
import cn.meiot.entity.bo.MeterStatisticalBo;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.PersonalSerialVo;
import cn.meiot.entity.vo.PersonalSerialVo;
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
public interface AppMeterYearsMapper extends BaseMapper<AppMeterYears> {


    /**
     * 获取app当月的电量
     * @param appMeterVo
     * @return
     */
    BigDecimal getMonthStatistical(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     * 获取按年统计的数据列表
     * @param appMeterVo
     * @return
     */
    List<Map<String, Object>> getYearStatisticalList(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     * 获取统计的数据
     * @param map
     * @return
     */
    BatteryLeftBo getBatteryLeft(@Param("map") Map<String, Object> map);


    /**
     * 获取统计的月份有数据
     * @param appMeterVo
     * @return
     */
    Integer getMaterCount(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     * 用电量top10
     * @param personalSerialVos
     * @param year
     * @param month
     * @return
     */
    List<MeterStatisticalBo> getTopMeter(@Param("list") List<PersonalSerialVo> personalSerialVos, @Param("year") Integer year,  @Param("month")Integer month);

    /**
     * 获取制定年份的每月用电数据
     * @param list
     * @param year
     * @return
     */
    List<MeterStatisticalBo> getMeterMonth(@Param("list") List<PersonalSerialVo> list,@Param("year") Integer year,@Param("userId") Long userId);


    /**
     * 获取制定年份的每月用电数据
     * @param list
     * @param year
     * @return
     */
    List<MeterStatisticalBo> getEnterpriseMeterMonth(@Param("list") List<PersonalSerialVo> list,
                                                     @Param("year") Integer year,
                                                     @Param("userId") Long userId,
                                                     @Param("projectId") Integer projectId);


    /**
     *
     * @Title: getTotalMeter
     * @Description: 运维报告基本信息
     * @param personalSerialVoList
     * @param parametersDto
     * @return: java.math.BigDecimal
     */
    BigDecimal getTotalMeter(@Param("list") List<PersonalSerialVo> personalSerialVoList,
                             @Param("parametersDto") ParametersDto parametersDto);

    /**
     *
     * @Title: getMonthlyMeter
     * @Description: 个人app的每月用电量
     * @param personalSerialVoList
     * @param parametersDto
     * @return: java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    List<Map<String, Object>> getMonthlyMeter(@Param("list") List<PersonalSerialVo> personalSerialVoList,
                                              @Param("parametersDto") ParametersDto parametersDto);
}
