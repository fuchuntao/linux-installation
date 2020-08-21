package cn.meiot;

import cn.meiot.entity.WaterQueue;
import cn.meiot.entity.device.DeviceBase;
import cn.meiot.entity.vo.AppMeterHoursVo;
import cn.meiot.entity.vo.DeviceVo;
import cn.meiot.entity.vo.RedisDataDto;
import cn.meiot.entity.vo.RedisDateVo;
import cn.meiot.entity.water.Record;
import cn.meiot.enums.WaterType;
import cn.meiot.mapper.WaterQueueMapper;
import cn.meiot.mq.AppStatisticsMq;
import cn.meiot.service.IPcDeviceStatisticsService;
import cn.meiot.service.IWaterQueueService;
import cn.meiot.service.IWaterStatisticsMonthsService;
import cn.meiot.service.IWaterStatisticsYearsService;
import cn.meiot.service.impl.WaterStatisticsMonthsServiceImpl;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.WaterUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan("cn.meiot.utils.*")
public class MeiotStatisticsApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AppStatisticsMq appStatisticsMq;

    @Autowired
    private IPcDeviceStatisticsService pcDeviceStatisticsService;
    @Autowired
    private IWaterQueueService waterQueueService;

    @Autowired
    private WaterUtils waterUtils;

    @Autowired
    private IWaterStatisticsMonthsService waterStatisticsMonthsService;

    @Autowired
    private IWaterStatisticsYearsService waterStatisticsYearsService;

    @Test
    public void contextLoads() {
        rabbitTemplate.convertAndSend(QueueConstantUtil.STATISTICS_DAY_QUEUE, 2);
    }


    @Test
    public void contextLoads1() {
//        DeviceVo deviceVo =DeviceVo.builder().serialNumber("AD13-1909050007").switchIndex(1).oldSwitchSn(1909050067L).newSwitchSn(1909050061L).build();
//        rabbitTemplate.convertAndSend(QueueConstantUtil.CHANGE_SWTICH_SN,"",deviceVo);
//        rabbitTemplate.convertAndSend(QueueConstantUtil.LOSE_METER,"");
//        rabbitTemplate.convertAndSend(QueueConstantUtil.STATISTICS_DAY_QUEUE,1);
//        //企业用户拉取天的数据到月表中
//        rabbitTemplate.convertAndSend(QueueConstantUtil.STATISTICS_DAY_QUEUE_PC,1);
//        appStatisticsMq.DeviceMeterMonthStatistics(2);
//        List<AppMeterHoursVo> appMeterHoursVoList = pcDeviceStatisticsService.selectDeviceMeter();
//        System.out.println(appMeterHoursVoList);
//        appStatisticsMq.DeviceMeterMonthStatisticsPc(2);
//        rabbitTemplate.convertAndSend(QueueConstantUtil.WATER_RECORD,new Date());
        //则数据全部拉取抄表列表的数据并且计算出差值，插入数据
//        waterStatisticsMonthsService.insertWaterMeterMonthsList();
//        waterStatisticsYearsService.insertWaterMeterYearsList();
        Map mapWater = new HashMap();
//        mapWater.put("type", 0);
        rabbitTemplate.convertAndSend(QueueConstantUtil.UPDATE_WATER_RECORD, mapWater);

    }


    @Test
    public void test7() {
//        WaterQueue waterQueue = new WaterQueue();
//        LocalDate timte = LocalDate.now();
//        waterQueue.setStatus(1);
//        waterQueue.setCreateTime(new Date());
//        waterQueueService.save(waterQueue);
        Map map = new HashMap();
        map.put("from", 0);
        map.put("size", 20);
        map.put("starttime", 1572248192000L);
        map.put("endtime", 1572420992000L);
        List<Record> customer = waterUtils.getCustomer(Record.class, WaterType.RECORD, map);
        System.out.println(customer);


//        int i = waterQueueService.selectByTime(timte);
//        System.out.println(i);


    }

    @Test
    public void test8() {
        RedisDataDto redisDataDto = RedisDataDto.builder()
                .current(1L)
                .leakage(1L)
                .temp(1)
                .power(1L)
                .lastTime(1111111111L)
                .build();

        redisTemplate.opsForHash().put(RedisConstantUtil.UPLOAD_DATA + "000001", "111111", redisDataDto);


        RedisDataDto redisDataDto2 = RedisDataDto.builder()
                .current(2L)
                .leakage(2L)
                .temp(2)
                .power(2L)
                .lastTime(2222222222L)
                .build();

        Gson gson = new Gson();


        redisTemplate.opsForHash().put(RedisConstantUtil.UPLOAD_DATA + "000001", "111111", gson.toJson(redisDataDto2));

        Object o = redisTemplate.opsForHash().get(RedisConstantUtil.UPLOAD_DATA + "000001", "111111");


        RedisDataDto redisDate = gson.fromJson(o.toString(), RedisDataDto.class);

        System.out.println("缓存的值" + redisDate);


    }

    public static void main(String[] args) {
        String s = "false";
        boolean equals = "false".equals(s);
        System.out.println(equals);

//        String json = "{\n" +
//                "\t\"clientid\": \"P2201912090003\",\n" +
//                "\t\"topic\": \"P2201912090003\\/event\\/raw\",\n" +
//                "\t\"payload\": {\n" +
//                "\t\t\"messageid\": 147588229,\n" +
//                "\t\t\"timestamp\": 1577073482,\n" +
//                "\t\t\"cmd\": \"CMD-104\",\n" +
//                "\t\t\"deviceid\": \"P2201912090003\",\n" +
//                "\t\t\"desired\": {\n" +
//                "\t\t\t\"arrays\": [{\n" +
//                "\t\t\t\t\"device\": {\n" +
//                "\t\t\t\t\t\"index\": 4,\n" +
//                "\t\t\t\t\t\"mode\": \"C32\",\n" +
//                "\t\t\t\t\t\"id\": 1911190415\n" +
//                "\t\t\t\t},\n" +
//                "\t\t\t\t\"status\": {\n" +
//                "\t\t\t\t\t\"event\": [0],\n" +
//                "\t\t\t\t\t\"power\": 0,\n" +
//                "\t\t\t\t\t\"loadmax\": 13200,\n" +
//                "\t\t\t\t\t\"temp\": 27,\n" +
//                "\t\t\t\t\t\"tempmax\": 75,\n" +
//                "\t\t\t\t\t\"flag\": 0,\n" +
//                "\t\t\t\t\t\"time\": 12,\n" +
//                "\t\t\t\t\t\"meterh\": 0,\n" +
//                "\t\t\t\t\t\"meterd\": 0,\n" +
//                "\t\t\t\t\t\"meterm\": 0,\n" +
//                "\t\t\t\t\t\"switch\": 0,\n" +
//                "\t\t\t\t\t\"auto\": 0,\n" +
//                "\t\t\t\t\t\"leakage\": 0,\n" +
//                "\t\t\t\t\t\"current\": [10],\n" +
//                "\t\t\t\t\t\"voltage\": [221819]\n" +
//                "\t\t\t\t},\n" +
//                "\t\t\t\t\"meter\": [\n" +
//                "\t\t\t\t\t[]\n" +
//                "\t\t\t\t]\n" +
//                "\t\t\t}]\n" +
//                "\t\t}\n" +
//                "\t},\n" +
//                "\t\"qos\": 0,\n" +
//                "\t\"raw_packet_id\": 0,\n" +
//                "\t\"is_retain\": false,\n" +
//                "\t\"is_resend\": false,\n" +
//                "\t\"is_will\": false,\n" +
//                "\t\"ip\": \"113.89.97.149\",\n" +
//                "\t\"created\": {\n" +
//                "\t\t\"$date\": {\n" +
//                "\t\t\t\"$numberLong\": \"1577073490775\"\n" +
//                "\t\t}\n" +
//                "\t},\n" +
//                "\t\"created_time\": 1577073490\n" +
//                "}";
////        JSONObject resultPostJson = JSON.parseObject(json);
//        DeviceBase deviceBase = JSON.parseObject(json, DeviceBase.class);
//        System.out.println(deviceBase);
//        Calendar calendar = Calendar.getInstance();
//
//        calendar.set(2019, Calendar.DECEMBER, 31);
//
//        Date strDate = calendar.getTime();
//
//        DateFormat formatUpperCase = new SimpleDateFormat("yyyy-MM-dd");
//        System.out.println("2019-08-31 to yyyy-MM-dd: " + formatUpperCase.format(strDate));
//
//        formatUpperCase = new SimpleDateFormat("YYYY-MM-dd");
//        System.out.println("2019-08-31 to YYYY/MM/dd: " + formatUpperCase.format(strDate));

    }
}
