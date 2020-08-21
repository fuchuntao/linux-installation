package cn.meiot.service;


import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SerialNumberMasterVo;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @ClassName: IPcManagementDataStatisticsService
 * @Description: 管理平台首页数据统计
 * @author: 符纯涛
 * @date: 2019/9/20
 */
public interface IPcManagementDataStatisticsService {

    /**
     *
     * @Title: selectDataStatistics
     * @Description: 首页数据统计
     * @param
     * @return: cn.meiot.entity.vo.Result
     */
    Result selectDataStatistics();


    /**
     *
     * @Title: selectPcDataAll
     * @Description:  根据项目统计企业设备的数据
     * @param projectId
     * @return: cn.meiot.entity.vo.Result
     */
    Result selectPcDataAll(Integer projectId, Long startTime, Integer type);


    /**
     *
     * @Title: selectPcMonthMeter
     * @Description: 统计近12月的用电峰谷
     * @param projectId
     * @return: cn.meiot.entity.vo.Result
     */
    Result selectPcMonthMeter(Integer projectId);



    /**
     *
     * @Title: selectPcEnergy
     * @Description: 统计项目企业设备的能效
     * @param projectId
     * @return: cn.meiot.entity.vo.Result
     */
    Result selectPcEnergy(Integer projectId);




    /**
     *
     * @Title: selectDataAllByNumber
     * @Description: 根据项目id获取主开关编号
     * @param projectId
     * @param startYear
     * @param startMonth
     * @param startDay
     * @param year
     * @param month
     * @param day
     * @param type
     * @return: java.util.List<cn.meiot.entity.vo.SerialNumberMasterVo>
     */
    List<SerialNumberMasterVo> selectDataAllByNumber (Integer projectId,
                                                             int startYear,
                                                             int startMonth,
                                                             int startDay,
                                                             int year,
                                                             int month,
                                                             int day,
                                                             Integer type);
}
