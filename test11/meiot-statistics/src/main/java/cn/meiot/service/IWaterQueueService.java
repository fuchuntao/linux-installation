package cn.meiot.service;

import cn.meiot.entity.WaterQueue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 * 水表抄表记录队列状态表 服务类
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
public interface IWaterQueueService extends IService<WaterQueue> {


    /**
     *
     * @Title: selectByTime
     * @Description: 根据时间查询是否有队列记录
     * @param time
     * @return: int
     */
    int selectByTime(Date time);





}
