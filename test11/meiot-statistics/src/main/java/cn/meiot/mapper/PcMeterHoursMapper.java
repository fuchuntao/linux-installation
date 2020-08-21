package cn.meiot.mapper;

import cn.meiot.entity.AppMeterHours;
import cn.meiot.entity.PcMeterHours;
import cn.meiot.entity.PcMeterMonths;
import cn.meiot.entity.vo.AppMeterMonthsVo;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.SerialNumberMasterVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业平台天数据统计表 Mapper 接口
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Mapper
public interface PcMeterHoursMapper extends BaseMapper<PcMeterHours> {

    /**
     * 根据条件获取数量
     * @param map
     * @return
     */
    Long getCountByConditionPc(@Param("map") Map<String, Object> map);

    /**
     * 获取上一个小时的最后一条记录
     * @param map
     * @return
     */
    PcMeterHours getLastInfoByConditionPc(@Param("map") Map<String, Object> map);


    /**
     * 查询月统计列表怕
     * @param appMeterVo
     * @return
     */
    List<AppMeterMonthsVo> selectMeterListBySerialNumberPc(@Param("appMeterVo") AppMeterVo appMeterVo);

    /**
     *
     * @Title: getDayMeterByProjectId
     * @Description: 根据主开关获取当天电量的统计
     * @param indexAllByProjectId
     * @param year
     * @param month
     * @return: java.math.BigDecimal
     */
    BigDecimal getDayMeterByProjectId(@Param("list") List<SerialNumberMasterVo> indexAllByProjectId,
                                      @Param("year") int year,
                                      @Param("month") int month,
                                      @Param("day") int day,
                                      @Param("projectId") Integer projectId);

    /**
     * 获取项目id的指定设备编号最后一次上传的电量时间
     * @param userId
     * @param serialNumber
     * @return
     */
    Long getTotalMeterCreateTime(@Param("userId") Long userId,
                                 @Param("serialNumber") String serialNumber,
                                 @Param("projectId") Integer projectId);


}
