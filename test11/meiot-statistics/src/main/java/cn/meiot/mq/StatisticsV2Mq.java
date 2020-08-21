package cn.meiot.mq;

import cn.meiot.entity.*;
import cn.meiot.entity.equipment2.upstatus.Sl;
import cn.meiot.entity.vo.RedisDataDto;
import cn.meiot.entity.vo.RedisMeterDto;
import cn.meiot.entity.vo.UploadDataDto;
import cn.meiot.service.*;
import cn.meiot.utils.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @ClassName: StatisticsV2Mq
 * @Description: 上传电量 me(meter)，漏电电流 l(leakage)， 电压 v, 电流 ca， 温度 t（temp), 负载 po 二代协议
 * @author: 符纯涛
 * @date: 2020/7/1
 */
@Component
@Slf4j
public class StatisticsV2Mq {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private IPcLeakageHoursService pcLeakageHoursService;


    @Autowired
    private IPcTempHoursService pcTempHoursService;

    @Autowired
    private IPcPowerHoursService pcPowerHoursService;

    @Autowired
    private IAppMeterHoursService appMeterHoursService;

    @Autowired
    private IPcMeterHoursService pcMeterHoursService;


    @Autowired
    private IPcCurrentHoursService pcCurrentHoursService;

    @Autowired
    private IPcVoltageHoursService pcVoltageHoursService;


    private Calendar cal = Calendar.getInstance();


    /**
     * @param message
     * @Title: DeviceLeakage
     * @Description: 获取上传的电流，电压，温度
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.TEMP_STATISTICS_V2)
    public void deviceLeakage(String message) {
        asynData(message);
    }


    /**
     * @param message
     * @Title: DeviceTemp
     * @Description: 获取上传的电流，电压，温度
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.TEMP_STATISTICS_V2)
    public void deviceTemp(String message) {
        asynData(message);
    }


    /**
     * @param message
     * @Title: asynData
     * @Description: 异步获取插入数据
     * @return: void
     */
    @Async(value = "taskExecutor")
    public void asynData(String message) {
        Map parseObject = JSON.parseObject(message, Map.class);
        //设备号
        String serialNumber = MqttUtil.findSerialNumber(parseObject);
        List<Sl> data2 = MqttUtil.findData2(parseObject, Sl.class);
        //获取时间
        Long time = MqttUtil.findTime(parseObject);
        Date date = new Date(time * 1000L);
        //获取时间的年月日
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        int day = cal.get(Calendar.DATE);//获取日
        int hour = cal.get(Calendar.HOUR_OF_DAY);//小时
        int minute = cal.get(Calendar.MINUTE);//分


        try {
            //获取用户类型
            Integer userType = (Integer) redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_SERIALNUMER + serialNumber);
            if (userType == null) {
                userType = commonUtil.getRtUserTypeBySerialNumber(serialNumber);
            }
            //判断是企业还是个人
            if (userType == null) {
                log.info("获取用户类型为空！！！");
                return;
            }
            Long rtUserId = commonUtil.getRtUserIdBySerialNumber(serialNumber);
            if (null == rtUserId) {
                log.info("二代协议, 类型：{}, 设备号：{}未获取到主账号id，", userType, serialNumber);
                return;
            }
            //转换时间格式为：yyyy-MM-dd HH:mm:ss
            String format = ConstantsUtil.getSimpleDateFormat().format(date);
            UploadDataDto uploadDataDto = UploadDataDto.builder()
                    .serialNumber(serialNumber)
                    .year(year)
                    .month(month)
                    .day(day)
                    .hour(hour)
                    .minute(minute)
                    .userId(rtUserId)
                    .userType(Long.valueOf(userType))
                    .createTime(format)
                    .build();
            for (int i = 0; i < data2.size(); i++) {
                Sl status = data2.get(i);
                Long sn = status.getSid();
                //获取sn号
                String sid = sn.toString().intern();
                synchronized (sid) {
                    uploadDataDto.setSwitchSn(sn);
                    saveRedisMeter(status, uploadDataDto, serialNumber, time);
                    saveRedis(status, uploadDataDto, serialNumber, time);
                }
            }
        } catch (Exception e) {
            log.error("二代协议错误信息：{},======[]{},=========[]{}", e);
        }


    }

    /**
     * @param status
     * @param uploadDataDto
     * @Title: saveLeakage
     * @Description: 电流
     * @return: void
     */
    public void saveCurrent(Sl status, UploadDataDto uploadDataDto, RedisDataDto redisDataDto) {
        boolean flag = uploadDataDto.getUserType() != null && uploadDataDto.getUserType() > 0;
        //获取电流
        Long ca = status.getCa();
        log.info("二代协议获取电流：{}", ca);
//        if (!flag || ca == null || ca == 0) {
//            return;
//        }
        //判断是否是大于5%
        if (!flag || !compareData(uploadDataDto.getOldData(), redisDataDto.getCurrent(),
                redisDataDto.getStartTime(), redisDataDto.getLastTime())) {
            return;
        }
        BigDecimal current = null;
        PcCurrentHours entity = null;
        try {
            current = new BigDecimal(ca).divide(new BigDecimal("1000"), 3, RoundingMode.HALF_UP);
            entity = PcCurrentHours.builder()
                    .current(current)
                    .serialNumber(uploadDataDto.getSerialNumber())
                    .switchSn(uploadDataDto.getSwitchSn())
                    .sYear(uploadDataDto.getYear())
                    .sMonth(uploadDataDto.getMonth())
                    .sDay(uploadDataDto.getDay())
                    .sTime(uploadDataDto.getHour())
                    .userId(uploadDataDto.getUserId())
                    .projectId(uploadDataDto.getUserType())
                    .createTime(uploadDataDto.getCreateTime())
                    .build();
            //添加数据
            pcCurrentHoursService.save(entity);
        } catch (Exception e) {
            log.info("二代协议添加电流报错：电流{},添加数据：{}", current, entity);
            log.error("二代协议添加电流错误信息：{},======[]{},=========[]{}", e.getMessage(), e.getCause(), e.getSuppressed());
        }


    }


    /**
     * @param status
     * @param uploadDataDto
     * @Title: saveLeakage
     * @Description: 漏电流
     * @return: void
     */
    public void saveLeakage(Sl status, UploadDataDto uploadDataDto, RedisDataDto redisDataDto) {
        boolean flag = uploadDataDto.getUserType() != null && uploadDataDto.getUserType() > 0;
        //获取电流
        Long l = status.getL();
        log.info("二代协议获取漏电流：{}", l);
//        if (!flag || l == null || l == 0) {
//            return;
//        }
        //判断是否是大于5%
        if (!flag || !compareData(uploadDataDto.getOldData(), redisDataDto.getLeakage(),
                redisDataDto.getStartTime(), redisDataDto.getLastTime())) {
            return;
        }
        BigDecimal leakage = null;
        PcLeakageHours entity = null;
        try {
            leakage = new BigDecimal(l);
            entity = PcLeakageHours.builder()
                    .leakage(leakage)
                    .serialNumber(uploadDataDto.getSerialNumber())
                    .switchSn(uploadDataDto.getSwitchSn())
                    .sYear(uploadDataDto.getYear())
                    .sMonth(uploadDataDto.getMonth())
                    .sDay(uploadDataDto.getDay())
                    .sTime(uploadDataDto.getHour())
                    .userId(uploadDataDto.getUserId())
                    .projectId(uploadDataDto.getUserType())
                    .createTime(uploadDataDto.getCreateTime())
                    .build();
            //添加数据
            pcLeakageHoursService.save(entity);
//            redisLeakage(entity);
        } catch (Exception e) {
            log.info("二代协议添加漏电流报错：电流{},添加数据：{}", leakage, entity);
            log.error("二代协议添加漏电流错误信息：{},======[]{},=========[]{}", e.getMessage(), e.getCause(), e.getSuppressed());
        }


    }


    /**
     * @param status
     * @param uploadDataDto
     * @Title: saveTemp
     * @Description: 添加温度
     * @return: void
     */
    public void saveTemp(Sl status, UploadDataDto uploadDataDto, RedisDataDto redisDataDto) {
        boolean flag = uploadDataDto.getUserType() != null && uploadDataDto.getUserType() > 0;
        //获取温度
        Integer t = status.getT();
        log.info("二代协议获取温度：{}", t);
        if (redisDataDto.getTemp() == null || !flag || !compareData(uploadDataDto.getOldData(),
                Long.valueOf(redisDataDto.getTemp().toString()), redisDataDto.getStartTime(), redisDataDto.getLastTime())) {
            return;
        }

        if (flag && t != null) {
            BigDecimal temp = null;
            PcTempHours entity = null;
            try {
                temp = new BigDecimal(t);
                entity = PcTempHours.builder()
                        .temp(temp)
                        .serialNumber(uploadDataDto.getSerialNumber())
                        .switchSn(uploadDataDto.getSwitchSn())
                        .sYear(uploadDataDto.getYear())
                        .sMonth(uploadDataDto.getMonth())
                        .sDay(uploadDataDto.getDay())
                        .sTime(uploadDataDto.getHour())
                        .userId(uploadDataDto.getUserId())
                        .projectId(uploadDataDto.getUserType())
                        .createTime(uploadDataDto.getCreateTime())
                        .build();
                //添加数据
                pcTempHoursService.save(entity);
//                redisTemp(entity);
            } catch (Exception e) {
                log.info("二代协议添加温度报错：温度{},添加数据：{}", temp, entity);
                log.error("二代协议添加温度错误信息：{},======[]{},=========[]{}", e.getMessage(), e.getCause(), e.getSuppressed());
            }
        }

    }


    /**
     * @param status
     * @param uploadDataDto
     * @Title: savePower
     * @Description: 负载
     * @return: void
     */
    private void savePower(Sl status, UploadDataDto uploadDataDto, RedisDataDto redisDataDto) {
        boolean flag = uploadDataDto.getUserType() != null && uploadDataDto.getUserType() > 0;
        //获取负载
        Long po = status.getPo();
        log.info("二代协议获取负载：{}", po);
//        if (!flag || po == null || po == 0) {
//            return;
//        }
        if (!flag || !compareData(uploadDataDto.getOldData(), Long.valueOf(redisDataDto.getPower()),
                redisDataDto.getStartTime(), redisDataDto.getLastTime())) {
            return;
        }
        BigDecimal power = null;
        PcPowerHours entity = null;
        try {
            power = new BigDecimal(po);
            entity = PcPowerHours.builder()
                    .power(power)
                    .serialNumber(uploadDataDto.getSerialNumber())
                    .switchSn(uploadDataDto.getSwitchSn())
                    .sYear(uploadDataDto.getYear())
                    .sMonth(uploadDataDto.getMonth())
                    .sDay(uploadDataDto.getDay())
                    .sTime(uploadDataDto.getHour())
                    .userId(uploadDataDto.getUserId())
                    .projectId(uploadDataDto.getUserType())
                    .createTime(uploadDataDto.getCreateTime())
                    .build();
            //添加数据
            pcPowerHoursService.save(entity);
        } catch (Exception e) {
            log.info("二代协议添加负载报错：负载{},添加数据：{}", power, entity);
            log.error("二代协议添加负载错误信息：{},======[]{},=========[]{}", e.getMessage(), e.getCause(), e.getSuppressed());
        }
    }



    /**
     *
     * @Title: saveVoltage
     * @Description: 电压
     * @param status
     * @param uploadDataDto
     * @param redisDataDto
     * @return: void
     */
    private void saveVoltage(Sl status, UploadDataDto uploadDataDto, RedisDataDto redisDataDto) {
        boolean flag = uploadDataDto.getUserType() != null && uploadDataDto.getUserType() > 0;
        //获取电压
        Long v = status.getV();
        log.info("二代协议获取电压：{}", v);
//        if (!flag || po == null || po == 0) {
//            return;
//        }
        if (!flag || !compareData(uploadDataDto.getOldData(), Long.valueOf(redisDataDto.getVoltage()),
                redisDataDto.getStartTime(), redisDataDto.getLastTime())) {
            return;
        }
        BigDecimal voltage = null;
        PcVoltageHours entity = null;
        try {
            voltage = new BigDecimal(v).divide(new BigDecimal("1000"), 3, RoundingMode.HALF_UP);
            entity = PcVoltageHours.builder()
                    .voltage(voltage)
                    .serialNumber(uploadDataDto.getSerialNumber())
                    .switchSn(uploadDataDto.getSwitchSn())
                    .sYear(uploadDataDto.getYear())
                    .sMonth(uploadDataDto.getMonth())
                    .sDay(uploadDataDto.getDay())
                    .sTime(uploadDataDto.getHour())
                    .userId(uploadDataDto.getUserId())
                    .projectId(uploadDataDto.getUserType())
                    .createTime(uploadDataDto.getCreateTime())
                    .build();
            //添加数据
            pcVoltageHoursService.save(entity);
        } catch (Exception e) {
            log.info("二代协议添加电压报错：电压{},添加数据：{}", voltage, entity);
            log.error("二代协议添加电压错误信息：{},======[]{},=========[]{}", e.getMessage(), e.getCause(), e.getSuppressed());
        }
    }







    /**
     * @param status
     * @param uploadDataDto
     * @Title: saveMeter
     * @Description: 电量统计
     * @return: void
     */
    private void saveMeter(Sl status, UploadDataDto uploadDataDto) {
        boolean flag = uploadDataDto.getUserType() != null && uploadDataDto.getUserType() > 0;
        //获取电量
        Long me = status.getMe();
        log.info("二代协议获取电量：{}", me);
        if (flag && me != null) {
//            //上传的电量
//            BigDecimal decimal = BigDecimal.valueOf(me);
//            //获取上传的电量
//            uploadDataDto.setData(decimal);
            Long userType = uploadDataDto.getUserType();

            Map<String, Object> map = new HashMap<>();
            //设备序列号
            map.put("serial_number", uploadDataDto.getSerialNumber());
            //开关编号
            map.put("switch_sn", uploadDataDto.getSwitchSn());
            map.put("s_year", uploadDataDto.getYear());
            map.put("s_month", uploadDataDto.getMonth());
            map.put("s_day", uploadDataDto.getDay());
            //获取上一个小时的电量
            map.put("s_time", uploadDataDto.getHour());
            map.put("s_minute", uploadDataDto.getMinute());
            map.put("user_id", uploadDataDto.getUserId());
            map.put("project_id", uploadDataDto.getUserType());
            //个人
            if (userType.equals(0)) {
                saveMeterPer(map, uploadDataDto);
            } else {
                //企业
                saveMeterEn(map, uploadDataDto);
            }
        }

    }






    @Transactional
    public void saveMeterPer(Map<String, Object> map, UploadDataDto uploadDataDto) {
        log.info("二代根据设备序列号获取用户类型为个人");
        try {
            //获取上传的电量
            BigDecimal meter = uploadDataDto.getData();
            //查询
            AppMeterHours appMeterHours = null;
            Integer hour = uploadDataDto.getHour();
//            if (0 != hour) {
//                log.info("二代个人查询数据库是否存在上一次的数据");
//                appMeterHours = appMeterHoursService.getLastInfoByCondition(map);
//
//            }
//            //将要保存到数据库的电量
//            BigDecimal meter = BigDecimal.ZERO;
//            if (null == appMeterHours) {
//                meter = decimal;
//            } else {
//                log.info("二代个人获取到之前的总电量：{}", appMeterHours.getMeter());
//                meter = decimal.subtract(appMeterHours.getMeter());
//            }
            //判断当前分钟是否存在
            //判断当前时间的数据是否存在
            map.put("s_time", hour + 1);
            Long id = appMeterHoursService.getCountByCondition(map);
            if (null == id || 0 == id) {
                log.info("二代个人执行了插入操作");
                //数据库没有数据，执行插入操作
                appMeterHours = AppMeterHours.builder()
                        .meter(meter)
                        .createTime(uploadDataDto.getCreateTime())
                        .serialNumber(uploadDataDto.getSerialNumber())
                        .switchSn(uploadDataDto.getSwitchSn())
                        .sYear(uploadDataDto.getYear())
                        .sMonth(uploadDataDto.getMonth())
                        .sDay(uploadDataDto.getDay())
                        .sTime(hour + 1)
//                        .sMinute(uploadDataDto.getMinute())
                        .userId(uploadDataDto.getUserId())
                        .projectId(uploadDataDto.getUserType())
                        .build();
                appMeterHoursService.save(appMeterHours);
                return;
            }
            log.info("二代个人执行了更新操作");
            //执行修改操作
            UpdateWrapper<AppMeterHours> updateWrapper = new UpdateWrapper<AppMeterHours>();
            updateWrapper.set("meter", meter);
            updateWrapper.set("update_time", uploadDataDto.getCreateTime());
            updateWrapper.eq("id", id);
            appMeterHoursService.update(updateWrapper);
            return;
        } catch (Exception e) {
            log.info("二代个人统计电量发生错误：{}", e.getStackTrace());
            log.error("二代个人错误信息：{},======[]{},=========[]{}", e.getMessage(), e.getCause(), e.getSuppressed());
            log.error("二代个人统计电量发生错误：{}", e.getStackTrace());
        }
    }


    @Transactional
    public void saveMeterEn(Map<String, Object> map, UploadDataDto uploadDataDto) {
        log.info("二代根据设备序列号获取用户类型为企业");
        try {
            BigDecimal meter = uploadDataDto.getData();
            //查询
            PcMeterHours pcMeterHours = null;
            Integer hour = uploadDataDto.getHour();
//            if (0 != hour) {
//                log.info("二代企业查询数据库是否存在上一次的数据");
//                pcMeterHours = pcMeterHoursService.getLastInfoByConditionPc(map);
//
//            }
//            //将要保存到数据库的电量
//            BigDecimal meter = BigDecimal.ZERO;
//            if (null == pcMeterHours) {
//                meter = decimal;
//            } else {
//                log.info("二代企业获取到之前的总电量：{}", pcMeterHours.getMeter());
//                meter = decimal.subtract(pcMeterHours.getMeter());
//            }
            //判断当前分钟是否存在
            map.put("s_time", hour + 1);
            Long id = pcMeterHoursService.getCountByConditionPc(map);
            if (null == id || 0 == id) {
                log.info("二代企业执行了插入操作");
                //数据库没有数据，执行插入操作
                pcMeterHours = PcMeterHours.builder()
                        .meter(meter)
                        .createTime(uploadDataDto.getCreateTime())
                        .serialNumber(uploadDataDto.getSerialNumber())
                        .switchSn(uploadDataDto.getSwitchSn())
                        .sYear(uploadDataDto.getYear())
                        .sMonth(uploadDataDto.getMonth())
                        .sDay(uploadDataDto.getDay())
                        .sTime(hour + 1)
//                        .sMinute(uploadDataDto.getMinute())
                        .userId(uploadDataDto.getUserId())
                        .projectId(uploadDataDto.getUserType())
                        .build();
                pcMeterHoursService.save(pcMeterHours);
                return;
            }
            log.info("二代企业执行了更新操作");
            //执行修改操作
            UpdateWrapper<PcMeterHours> updateWrapper = new UpdateWrapper<PcMeterHours>();
            updateWrapper.set("meter", meter);
            updateWrapper.set("update_time", uploadDataDto.getCreateTime());
            updateWrapper.eq("id", id);
            pcMeterHoursService.update(updateWrapper);
            return;
        } catch (Exception e) {
            log.info("二代企业统计电量发生错误：{}", e.getStackTrace());
            log.error("二代企业错误信息：{},======[]{},=========[]{}", e.getMessage(), e.getCause(), e.getSuppressed());
            log.error("二代企业统计电量发生错误：{}", e.getStackTrace());
        }
    }


    /**
     * @param status
     * @param uploadDataDto
     * @param serialNumber
     * @param time 上传的时间戳
     * @Title: saveRedis
     * @Description: 上传的数据存缓存（判断是否大于上一次上传的数据的 +-5%，如果是则存数据库
     * @return: void
     */
    public void saveRedis(Sl status, UploadDataDto uploadDataDto, String serialNumber, Long time) {
        RedisDataDto redisDataDto = RedisDataDto.builder()
                .voltage(status.getV())
                .meter(status.getMe())
                .current(status.getCa())
                .leakage(status.getL())
                .temp(status.getT())
                .power(status.getPo())
                .lastTime(time)
                .build();
        //获取sn
        Long sn = status.getSid();
        Gson gson = new Gson();
        //查询缓存
        Object o = redisTemplate.opsForHash().get(RedisConstantUtil.UPLOAD_DATA + serialNumber, sn.toString());
        //电流
        Long currentOld = null;
        Long leakageOld = null;
        Long tempOld = null;
        Long powerOld = null;
        //电压
        Long voltageOld = null;
        Long startT = null;
        RedisDataDto redisDate = new RedisDataDto();
        log.info("缓存数据：{}",o);
        if (o != null) {
            redisDate = gson.fromJson(o.toString(), RedisDataDto.class);
            //查询缓存的开始时间,是否是大于等于60分钟
            Long startTime = redisDate.getStartTime();
            if(time - startTime >= 60*60) {
                redisDate.setStartTime(time);
                startT = time;
            }else {
                startT = startTime;
            }
            //更新时间戳(缓存为空)
        }else {
            redisDate.setStartTime(time);
            startT = time;
        }
        redisDataDto.setStartTime(startT);
        //对比最新的上传与缓存的值得对比+-5%(漏电电流 l(leakage)， 电压 v, 电流 ca， 温度 t（temp), 负载 po)
        currentOld = redisDate.getCurrent();
        uploadDataDto.setOldData(currentOld);
        saveCurrent(status, uploadDataDto, redisDataDto);

        //漏电流
        leakageOld = redisDate.getLeakage();
        uploadDataDto.setOldData(leakageOld);
        saveLeakage(status, uploadDataDto, redisDataDto);
        if (redisDate.getTemp() != null) {
            //温度
            tempOld = Long.valueOf(redisDate.getTemp().toString());
        }
        uploadDataDto.setOldData(tempOld);
        saveTemp(status, uploadDataDto, redisDataDto);
        //负载
        powerOld = redisDate.getPower();
        uploadDataDto.setOldData(powerOld);
        savePower(status, uploadDataDto, redisDataDto);
        //电压
        voltageOld = redisDate.getVoltage();
        uploadDataDto.setOldData(voltageOld);
        saveVoltage(status, uploadDataDto, redisDataDto);


        //存缓存
        redisTemplate.opsForHash().put(RedisConstantUtil.UPLOAD_DATA + serialNumber, sn.toString(), gson.toJson(redisDataDto));

    }





    /**
     *
     * @Title: saveRedisMeter
     * @Description: 上传电量
     * @param status
     * @param uploadDataDto
     * @param serialNumber
     * @param time
     * @return: void
     */
    private void saveRedisMeter(Sl status, UploadDataDto uploadDataDto, String serialNumber, Long time) {
        Gson gson = new Gson();
        //获取sn
        Long sn = status.getSid();
        //电量实体类
        RedisMeterDto redisMeterDto = new RedisMeterDto();
        //查询电量的缓存
        Object meterData = redisTemplate.opsForHash().get(RedisConstantUtil.UPLOAD_METER + serialNumber, sn.toString());
        if(meterData != null) {
            //判断时间是否为1小时
            redisMeterDto = gson.fromJson(meterData.toString(), RedisMeterDto.class);
            //获取上一次的时间戳
            Long timeMeter = redisMeterDto.getTime();
            if(time - timeMeter >= 60*60) {
                //获取上一次的电量
                Long meterOld = redisMeterDto.getMeter();
                //最新上传的电量
                Long meterNew = status.getMe();
                //判断本次上传与上次获得的电量的差值
                BigDecimal me = BigDecimal.valueOf(meterNew).subtract(BigDecimal.valueOf(meterOld));
                uploadDataDto.setData(me);
                //存数据库
                saveMeter(status, uploadDataDto);
                redisMeterDto.setTime(time);
                redisMeterDto.setMeter(meterNew);
                //存电量缓存
                redisTemplate.opsForHash().put(RedisConstantUtil.UPLOAD_METER + serialNumber, sn.toString(), gson.toJson(redisMeterDto));
            }
        }else {
            redisMeterDto.setTime(time);
            redisMeterDto.setMeter(status.getMe());
            //存电量缓存
            redisTemplate.opsForHash().put(RedisConstantUtil.UPLOAD_METER + serialNumber, sn.toString(), gson.toJson(redisMeterDto));
            //存数据库
            BigDecimal me = BigDecimal.valueOf(status.getMe());
            uploadDataDto.setData(me);
            //存数据库
            saveMeter(status, uploadDataDto);
        }

    }

    /**
     * @param old
     * @param now
     * @param startTime
     * @param lastTime
     * @Title: compareData
     * @Description: 比较上一次的数据是否在+-5%
     * @return: java.lang.Boolean
     */
    private static Boolean compareData(Long old, Long now, Long startTime, Long lastTime) {

        //第一次上传的时间跟后续上传的时间小于5分钟
        if (lastTime - startTime < 60 * 60) {
            if (now == null) {
                return false;
            }
            if (now != null && old == null) {
                return true;
            }
            double abs = Math.abs(old - now);
            double v = old * 0.05;
//            System.out.println("abs==============>" + abs+ "  v===========》 " + v);
            return abs - v > 0 ? true : false;

        } else {
            return true;
        }

    }

    public static void main(String[] args) {
//        System.out.println(compareData(100L, 90L));
//        System.out.println(compareData(100L, 95L));
//        System.out.println(compareData(90L, 95L));
//        System.out.println(compareData(91L, 95L));
    }
}
