package cn.meiot.mapper;

import cn.meiot.dao.sql.PcMeterYearsProvider;
import cn.meiot.entity.PcMeterYears;
import cn.meiot.entity.bo.BatteryLeftBo;
import cn.meiot.entity.bo.MeterStatisticalBo;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.SerialNumberMasterVo;
import cn.meiot.entity.vo.SwitchVo;
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
 * 企业平台年电量统计 Mapper 接口
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Mapper
public interface PcMeterYearsMapper extends BaseMapper<PcMeterYears> {

    /**
     * 获取指定年份的月份数据
     * @return
     */
    //@Select(" SELECT s_month as month ,SUM(meter) as meter FROM `pc_meter_years` WHERE project_id = #{projectId} AND s_year = #{year} GROUP BY s_month ")
    //@SelectProvider(type = PcMeterYearsProvider.class,method ="queryMeterByMasterIndex" )
    List<MeterStatisticalBo> queryMeterByMasterIndex(@Param("list") List<SerialNumberMasterVo> list, @Param("year") Integer year,@Param("projectId") Integer projectId );

    /**
     * 通过项目id查询设别号
     * @param projectId
     * @param year
     * @param month
     * @return
     */
    List<String> querySerialNumberByProject(@Param("projectId") Integer projectId, @Param("year")Integer year,@Param("month") Integer month);



    /**
     * 
     * @Title: queryNumberAll
     * @Description: 根据时间查询
     * @param year
     * @param month
     * @param day
     * @param projectId    
     * @return: java.util.List<java.lang.String>     
     */
    List<String> queryNumberAll(@Param("year") int year, @Param("month") int month, @Param("day") int day, @Param("projectId") Integer projectId);


    /**
     *
     * @Title: queryNumberAll
     * @Description: 根据项目id时间查询年表数据
     * @param year
     * @param month
     * @param projectId
     * @return: java.util.List<java.lang.String>
     */
    List<String> queryNumberByMonth(@Param("year") int year, @Param("month") int month, @Param("projectId") Integer projectId);


    /**
     *
     * @Title: queryNumberAll
     * @Description: 根据项目id时间查询当前月的设备编号
     * @param year
     * @param month
     * @param projectId
     * @return: java.util.List<java.lang.String>
     */
    List<String> queryNumberByNowMonth(@Param("year") int year,
                                       @Param("month") int month,
                                       @Param("day") int day,
                                       @Param("projectId") Integer projectId,
                                       @Param("type") Integer type);


    /**
     *
     * @Title: queryNumberAll
     * @Description: 根据项目id时间查询天的设备编号
     * @param year
     * @param month
     * @param projectId
     * @return: java.util.List<java.lang.String>
     */
    List<String> queryNumberByDay(@Param("year") int year,
                                       @Param("month") int month,
                                       @Param("day") int day,
                                       @Param("projectId") Integer projectId);



    /**
     *
     * @Title: queryPcMeterByMasterIndex
     * @Description: 根据开关查询往年电量
     * @param list
     * @param year
     * @return: java.util.List<cn.meiot.entity.bo.MeterStatisticalBo>
     */
    List<Map<String, Object>> queryPcMeterByMasterIndex(@Param("list") List<SerialNumberMasterVo> list,
                                                        @Param("year") Integer year,
                                                        @Param("projectId") Integer projectId);





    /**
     *
     * @Title: queryNowPcMeterByMasterIndex
     * @Description: 查询当年的数据电量
     * @param list
     * @param year
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    Map<String, Object> queryNowPcMeterByMasterIndex(@Param("list") List<SerialNumberMasterVo> list,
                                                     @Param("year") Integer year,
                                                     @Param("month") Integer month,
                                                     @Param("projectId") Integer projectId);



    /**
     *
     * @Title: queryNowPcMeterByMasterIndex
     * @Description: 查询当月的数据电量
     * @param list
     * @param year
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    Map<String, Object> queryNowPcMeterByMonth(@Param("list") List<SerialNumberMasterVo> list,
                                               @Param("year") Integer year,
                                               @Param("month") Integer month,
                                               @Param("day") Integer day,
                                               @Param("projectId") Integer projectId);




    /**
     *
     * @Title: queryNowPcMeterByMasterIndex
     * @Description: 查询其他月的数据电量
     * @param list
     * @param year
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String, Object>> queryPcMeterByMonth(@Param("list") List<SerialNumberMasterVo> list,
                                                  @Param("year") Integer year,
                                                  @Param("month") Integer month,
                                                  @Param("projectId") Integer projectId);




    /**
     *
     * @Title: queryNowPcMeterByMasterIndex
     * @Description: 查询天的数据电量
     * @param list
     * @param year
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String, Object>> queryNowPcMeterByDay(@Param("list") List<SerialNumberMasterVo> list,
                                                   @Param("year") Integer year,
                                                   @Param("month") Integer month,
                                                   @Param("day") Integer day,
                                                   @Param("projectId") Integer projectId);

    /**
     * 
     * @Title: getIndexAllByProjectId  
     * @Description: 根据项目获取设备编号
     * @param projectId    
     * @return: java.util.List<java.lang.String>     
     */
    List<String> getIndexAllByProjectId(@Param("projectId") Integer projectId,
                                        @Param("year") int year,
                                        @Param("month") int month,
                                        @Param("day") int day);


    /**
     *
     * @Title: getMeterByProjectId
     * @Description: 根据项目获取设备编号的电量
     * @param indexAllByProjectId
     * @return: java.math.BigDecimal
     */
    BigDecimal getMeterByProjectId(@Param("list") List<SerialNumberMasterVo> indexAllByProjectId,
                                   @Param("projectId") Integer projectId);



    /**
     *
     * @Title: getYearStatisticalList
     * @Description: 查询用户的近12个月的电量统计
     * @param list
     * @param appMeterVo
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String, Object>> getYearStatisticalList(@Param("list") List<SerialNumberMasterVo> list,
                                                     @Param("appMeterVo") AppMeterVo appMeterVo);



    /**
     * 获取统计近12个月的最高，平均，最低数据
     * @param map
     * @return
     */
    BigDecimal getPcBatteryLeft(@Param("list") List<SerialNumberMasterVo> list,@Param("map") Map<String, Object> map);



    /**
     * 根据项目id和开关类型获取当年的电量
     * @param projectId
     * @return
     */
    BigDecimal getPcListSwitch(@Param("list") List<SwitchVo> list,
                               @Param("projectId") Integer projectId,
                               @Param("year") Integer year);

    /**
     * 查询企业设备的top10
     * @param projectId
     * @param year
     * @param month
     * @return
     */
    List<MeterStatisticalBo> getMeterTopByProjectId(@Param("list")List<SerialNumberMasterVo> list,@Param("projectId")Integer projectId,
                                                    @Param("year")Integer year,
                                                    @Param("month")Integer month);


    /**
     *
     * @Title: queryNowPcMeterByMasterIndex
     * @Description: 查询当月的数据电量
     * @param serialNumberMasterVo
     * @param year
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    BigDecimal appMeterByMasterIndex(@Param("serialNumberMasterVo") SerialNumberMasterVo serialNumberMasterVo,
                                              @Param("year") Integer year,
                                              @Param("month") Integer month,
                                              @Param("projectId") Integer projectId);



    /**
     * 获取该项目id的指定编号的所有用电量
     * @param userId
     * @param serialNumber
     * @return
     */
    BigDecimal gettotalMeterBySerialNumber(@Param("userId") Long userId,
                                           @Param("serialNumber") String serialNumber,
                                           @Param("masterSn") Long masterSn,
                                           @Param("projectId") Integer projectId);





}
