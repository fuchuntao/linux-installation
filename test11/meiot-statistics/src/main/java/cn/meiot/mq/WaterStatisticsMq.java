package cn.meiot.mq;

import cn.meiot.entity.WaterStatistics;
import cn.meiot.entity.WaterStatisticsMonths;
import cn.meiot.entity.vo.RedisDateVo;
import cn.meiot.entity.vo.WaterStatisticsDto;
import cn.meiot.mapper.WaterStatisticsMapper;
import cn.meiot.mapper.WaterStatisticsMonthsMapper;
import cn.meiot.service.IWaterStatisticsMonthsService;
import cn.meiot.service.IWaterStatisticsYearsService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.WaterUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName: WaterStatisticsMq
 * @Description: 水表统计mq
 * @author: 符纯涛
 * @date: 2020/4/22
 */
@Component
@Slf4j
public class WaterStatisticsMq {



    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Autowired
    private AppStatisticsMq appStatisticsMq;


    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private WaterStatisticsMonthsMapper waterStatisticsMonthsMapper;


    @Autowired
    private IWaterStatisticsMonthsService waterStatisticsMonthsService;

    @Autowired
    private IWaterStatisticsYearsService waterStatisticsYearsService;


    @Autowired
    private WaterStatisticsMapper waterStatisticsMapper;


    private  Calendar calEndDataUtil = Calendar.getInstance();
    /**
     *
     * @Title: selectLoseMeter
     * @Description: 手动拉取水表的抄表记录队列
     * @param
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.HAND_WATER_RECORD)
//    @RabbitListener(queues = QueueConstantUtil.WATER_RECORD_TEST)
    public void selectWaterRecord(Map map) {
        log.info("手动拉取水表的抄表记录队列！！");
        //获取水表列表
        List<String> meterIdList = (List<String>) map.get("meterIdList");
        //获取项目id
        Integer projectId = (Integer) map.get("projectId");

        appStatisticsMq.insetListWaterS(null, 1,meterIdList, projectId);
        //手动拉取成功修改redis的值为0
        //查询缓存是否有这个值
        Object waterRedis = redisTemplate.opsForValue().get(ConstantsUtil.REDIS_WATER_PROJECT + projectId);
        Gson gson = new Gson();
        if(waterRedis != null) {
            //查看值type == 1 将1修改为0
            RedisDateVo redisDate = gson.fromJson(waterRedis.toString(), RedisDateVo.class);
            Integer type = redisDate.getType();
            if (type != null && type == 1) {
                redisDate.setType(0);
                redisTemplate.opsForValue().set(ConstantsUtil.REDIS_WATER_PROJECT + projectId,
                        gson.toJson(redisDate), ConstantsUtil.REDIS_WATER_EXPIRE_TIME, TimeUnit.MINUTES);
            }
        }
    }


    /**
     *
     * @Title: updatewaterrecord
     * @Description: 更新水表数据月表
     * @param
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.UPDATE_WATER_RECORD)
    public void updatewaterrecord(Map map) {
        log.info("更新水表数据月表队列！！");
        Map mapWater = new HashMap();
//        List<WaterStatistics> waterStatisticsList = new ArrayList<>();
        mapWater.put("type", 1);
        //查询数据库看数据是否为空
        Integer integer = waterStatisticsMonthsMapper.selectWaterMeter();
        if(integer == 0) {
            //则数据全部拉取抄表列表的数据并且计算出差值，插入数据
            waterStatisticsMonthsService.insertWaterMeterMonthsList();
            mapWater.put("type", integer);

        }else {
            //拉取更新操作
            Map updateAndInsertWater = waterStatisticsMonthsService.updateWaterMonths(map);
            mapWater.put("updateAndInsertWater", updateAndInsertWater);

        }
        //拉取数据后修改队列
        rabbitTemplate.convertAndSend(QueueConstantUtil.YEAR_WATER_RECORD,mapWater);
    }



    /**
     *
     * @Title: updateYearWaterrecord
     * @Description: 水表年表修改数据队列
     * @param map
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.YEAR_WATER_RECORD)
    public void updateYearWaterrecord(Map map) {
        log.info("水表年表修改数据队列！！");
        //判断是否是第一次拉取
        Object type = map.get("type");
        if(type != null && type.equals(0)) {
            //第一次拉取
            waterStatisticsYearsService.insertWaterMeterYearsList();
        }else {
            //拉去更新的数据
            Map<String, Object> updateAndInsertWater = new HashMap<>();

            if(map.get("updateAndInsertWater") != null) {

                updateAndInsertWater  = (Map<String, Object>)map.get("updateAndInsertWater");

                //获取修改的参数
                //修改
                //拉去更新的数据
                Map<String, Object> updateWater = new HashMap<>();

                if(updateAndInsertWater.get("updateWater") != null) {

                    updateWater  = (Map<String, Object>)updateAndInsertWater.get("updateWater");
                    //根据水表编号
                    waterStatisticsYearsService.updateWaterMeterYearsList(updateWater);
                }

                Map<String, Object> insertWater =  new HashMap<>();
                if(updateAndInsertWater.get("insertWater") != null) {
                    insertWater  = (Map<String, Object>)updateAndInsertWater.get("insertWater");
                    //根据水表编号
                    waterStatisticsYearsService.updateWaterMeterYearsList(insertWater);
                }

            }

        }
    }

}
