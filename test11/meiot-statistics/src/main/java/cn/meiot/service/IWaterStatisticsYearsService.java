package cn.meiot.service;

import cn.meiot.entity.WaterStatisticsYears;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.WaterStatisticsDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
public interface IWaterStatisticsYearsService extends IService<WaterStatisticsYears> {


    /**
     *
     * @Title: insertWaterMeterYearsList
     * @Description 查询抄表记录里面的每一天最后一条数据并且计算差值,最后插入年表中
     * @param
     * @return: void
     */
    void insertWaterMeterYearsList();





    /**
     *
     * @Title: queryWaterMeterList
     * @Description: 查询用水趋势，用水占比
     * @param startTime
     * @param type
     * @return: cn.meiot.entity.vo.Result
     */
    Result queryWaterMeterList(Integer projectId,Long userId, Long startTime,Integer type, Long buildingId);




    /**
     *
     * @Title: updateWaterMeterYearsList
     * @Description: 更新年表数据
     * @param map
     * @return: void
     */
    void updateWaterMeterYearsList(Map<String, Object> map);

}
