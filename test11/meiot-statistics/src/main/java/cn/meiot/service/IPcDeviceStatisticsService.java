package cn.meiot.service;

import cn.meiot.entity.PcDeviceStatistics;
import cn.meiot.entity.vo.AppMeterHoursVo;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备数据统计表 服务类
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
public interface IPcDeviceStatisticsService extends IService<PcDeviceStatistics> {

    /**
     * 统计设备信息
     * @param userId
     * @return
     */
    Result queryDeviceInfo(Long userId,Integer projectId);


    /**
     *
     * @Title: selectDeviceMeter
     * @Description: 统计设备丢失的上传电量的设备号和时间
     * @param
     * @return: java.util.List<cn.meiot.entity.AppMeterHours>
     */
    List<AppMeterHoursVo> selectDeviceMeter();

    /**
     * 获取设备在线率
     * @param userId   用户id
     * @param projectId   项目id ，项目id为空或者0时表示个人
     * @return
     */
    BigDecimal getDeviceLine(Long userId, Integer projectId );

    /**
     * 设备用电量top10
     * @param userId  用户id
     * @param year 年
     * @param month 月
     * @param projectId  项目id
     * @return
     */
    Result meterTop(Long userId, Integer year, Integer month, Integer projectId);


    /**
     * 年度用电占比
     * @param userId
     * @param year
     * @return
     */
    Result meterToYear(Long userId, Integer year,Integer projectId);
}
