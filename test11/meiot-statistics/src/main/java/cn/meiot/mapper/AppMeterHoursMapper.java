package cn.meiot.mapper;

import cn.meiot.entity.AppMeterHours;
import cn.meiot.entity.AppMeterMonths;
import cn.meiot.entity.AppMeterYears;
import cn.meiot.entity.bo.BatteryLeftBo;
import cn.meiot.entity.vo.AppMeterMonthsVo;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.ParametersDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-16
 */
@Mapper
public interface AppMeterHoursMapper extends BaseMapper<AppMeterHours> {

    /**
     * 根据条件获取数量
     * @param map
     * @return
     */
    Long getCountByCondition(@Param("map") Map<String, Object> map);

    /**
     * 获取上一个小时的最后一条记录
     * @param map
     * @return
     */
    AppMeterHours getLastInfoByCondition(@Param("map") Map<String, Object> map);

    /**
     * 查询某天的总用电量
     * @param appMeterVo
     * @return
     */
    BigDecimal getNowDayByCondition(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     * 统计昨天的电量使用
     * @param appMeterVo
     * @return
     */
    AppMeterMonths getLastTotalMaterBySerialNumber(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     * 获取列表
     * @param appMeterVo
     * @return
     */
    List<Map<String, Object>> getList(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     * 获取指定时间的所有设备号
     * @param year
     * @param month
     * @param day
     * @return
     */
    List<String> getLastDaySerialNumber(@Param("year")int year,@Param("month") int month, @Param("day")int day);

    /**
     * 通过设备号查询总电量
     * @param userId
     * @param serialNumber
     * @param masterIndex
     * @return
     */
    @Select("select  sum(meter) as totalMeter from app_meter_years where serial_number=#{serialNumber} and switch_sn = #{masterSn} and user_id =#{userId}")
    BigDecimal gettotalMeterBySerialNumber(@Param("userId") Long userId, @Param("serialNumber") String serialNumber,@Param("masterSn") Long masterSn);


    /**
     * 通过设备号查询最后上传的时间
     * @param userId
     * @param serialNumber
     * @return
     */
    @Select("select unix_timestamp(create_time) * 1000 AS createTime from app_meter_hours where serial_number=#{serialNumber}  and user_id =#{userId} ORDER BY create_time DESC LIMIT 1")
    Long getTotalMeterCreateTime(@Param("userId") Long userId, @Param("serialNumber") String serialNumber);


    /**
     * 获取当天的统计的数据
     * @param map
     * @return
     */
    BatteryLeftBo getBatteryLeftHours(@Param("map") Map<String, Object> map);


    /**
     *
     * @Title: getNowMonthByCondition
     * @Description: 查询当月的电量统计
     * @param appMeterVo
     * @return: cn.meiot.entity.bo.BatteryLeftBo
     */
    BatteryLeftBo getNowMonthByCondition(@Param("appMeterVo") AppMeterVo appMeterVo);


    List<AppMeterMonthsVo> selectListBySerialNumber(@Param("appMeterVo") AppMeterVo appMeterVo);


    /**
     *
     * @Title: selectListByMonths
     * @Description: 查询月表的数据
     * @param parametersDto
     * @return: java.util.List<cn.meiot.entity.AppMeterMonths>
     */
    List<AppMeterMonthsVo> selectListByMonths(ParametersDto parametersDto);


    /**
     *
     * @Title: selectByOne
     * @Description: 查询电量
     * @param parametersDto
     * @return: java.math.BigDecimal
     */
    Map<String, Object> selectByOne(ParametersDto parametersDto);



    /**
     *
     * @Title: insertByOne
     * @Description: 插入电量
     * @param parametersDto
     * @return: int
     */
    int insertByOne(ParametersDto parametersDto);


    /**
     *
     * @Title: updateByone
     * @Description: 更新电量
     * @param parametersDto
     * @return: int
     */
    int updateByone(ParametersDto parametersDto);


    /**
     *
     * @Title: getMonthlyMeterApp
     * @Description: app根据年月日查询电量
     * @param parametersDto
     * @param appMeterVo
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String, Object>> getMonthlyMeterApp(@Param("parametersDto") ParametersDto parametersDto, @Param("appMeterVo") AppMeterVo appMeterVo);

}
