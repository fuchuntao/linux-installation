package cn.meiot.mapper;

import cn.meiot.entity.WaterQueue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

/**
 * <p>
 * 水表抄表记录队列状态表 Mapper 接口
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
@Mapper
public interface WaterQueueMapper extends BaseMapper<WaterQueue> {


    /**
     *
     * @Title: selectWaterQueue
     * @Description: 根据时间查询是否有队列消费
     * @param time
     * @return: int
     */
    int selectWaterQueue(Long time);

}
