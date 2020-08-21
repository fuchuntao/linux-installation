package cn.meiot.service;

import cn.meiot.entity.WaterStatistics;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.WaterStatisticsDto;
import cn.meiot.entity.water.Record;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 符纯涛
 * @since 2020-02-24
 */
public interface IWaterStatisticsService extends IService<WaterStatistics> {

    /**
     *
     * @Title: queryWaterMeter
     * @Description: 查询抄表列表
     * @param waterStatisticsDto
     * @return: java.util.List<cn.meiot.entity.WaterStatistics>
     */
    Result queryWaterMeter(WaterStatisticsDto waterStatisticsDto);



    /**
     *
     * @Title: queryWaterMeterId
     * @Description: 查询当前最大的抄表id
     * @param
     * @return: java.lang.Long
     */
    Long queryWaterMeterId(WaterStatistics record);


    /**
     *
     * @Title: saveWaterMeter
     * @Description: 更新抄表统计数据
     * @param waterStatisticsList
     * @return: java.lang.Integer
     */
     Integer saveWaterMeter(List<WaterStatistics> waterStatisticsList);






}
