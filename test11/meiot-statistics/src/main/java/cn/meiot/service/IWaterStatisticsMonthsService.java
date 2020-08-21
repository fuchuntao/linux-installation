package cn.meiot.service;

import cn.meiot.entity.WaterStatisticsMonths;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
public interface IWaterStatisticsMonthsService extends IService<WaterStatisticsMonths> {




    /**
     *
     * @Title: insertWaterMeterMonthsList
     * @Description: 查询抄表记录里面的每一天最后一条数据并且计算差值,最后插入
     * @param
     * @return: java.lang.Integer
     */
    void insertWaterMeterMonthsList();




    /**
     *
     * @Title: updateWaterMonths
     * @Description: 更新月水表的数据
     * @param
     * @return: void
     */
    Map updateWaterMonths(Map map);






}
