package cn.meiot.service.impl;

import cn.meiot.entity.WaterQueue;
import cn.meiot.mapper.WaterQueueMapper;
import cn.meiot.service.IWaterQueueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * 水表抄表记录队列状态表 服务实现类
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
@Slf4j
@Service
public class WaterQueueServiceImpl extends ServiceImpl<WaterQueueMapper, WaterQueue> implements IWaterQueueService {

    @Autowired
    private WaterQueueMapper waterQueueMapper;


    @Override
    public int selectByTime(Date localDate) {
        if(localDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(localDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long timestamp = calendar.getTime().getTime();
            int i = waterQueueMapper.selectWaterQueue(timestamp);
            log.info("根据时间查询是否有队列记录,i:{}",i);
            return i;
        }
        return 0;
    }
}
