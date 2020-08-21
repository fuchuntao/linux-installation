package cn.meiot.mq;

import cn.meiot.config.TableConfig;
import cn.meiot.entity.*;
import cn.meiot.entity.device.DeviceBase;
import cn.meiot.entity.equipment.Status;
import cn.meiot.entity.vo.*;
import cn.meiot.entity.water.Record;
import cn.meiot.enums.WaterType;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.mapper.WaterStatisticsMapper;
import cn.meiot.mapper.WaterStatisticsMonthsMapper;
import cn.meiot.service.*;
import cn.meiot.utils.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AppStatisticsMq {


    @Autowired
    private IAppMeterHoursService appMeterHoursService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private IAppMeterMonthsService appMeterMonthsService;
    @Autowired
    private IAppMeterYearsService appMeterYearsService;
    @Autowired
    private IUserStatisticsService userStatisticsService;

    @Autowired
    private IPcMeterHoursService pcMeterHoursService;

    @Autowired
    private IPcMeterMonthsService pcMeterMonthsService;

    @Autowired
    private IPcMeterYearsService pcMeterYearsService;

    @Autowired
    private IPcLeakageHoursService pcLeakageHoursService;

    @Autowired
    private IPcLeakageMonthsService  pcLeakageMonthsService;

    @Autowired
    private IPcLeakageYearsService  pcLeakageYearsService;

    @Autowired
    private IPcTempHoursService pcTempHoursService;

    @Autowired
    private IPcPowerHoursService pcPowerHoursService;

    private Calendar cal = Calendar.getInstance();


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PcStatisticsService pcStatisticsService;
    @Autowired
    private IPcDeviceStatisticsService pcDeviceStatisticsService;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DataUtil dataUtil;
    @Autowired
    private IWaterQueueService waterQueueService;

    @Autowired
    private IWaterStatisticsService waterStatisticsService;

    @Autowired
    private IWaterRecordErrorService waterRecordErrorService;

    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Autowired
    private  WaterUtils waterUtils;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private WaterStatisticsMapper waterStatisticsMapper;


    @Autowired
    private WaterStatisticsMonthsMapper waterStatisticsMonthsMapper;

    @Autowired
    private WaterUtil waterUtil;
    /**
     * 统计每小时电量
     */
//    @RabbitListener(queues = QueueConstantUtil.STATISTICS_METER_APP)
    @Transactional
    public void DeviceMeterStatistics(String content) throws InterruptedException {
        try{
            log.info("接收到的数据：{}", content);
            Gson gson = new Gson();
            DeviceBase deviceBase = null;
            try{
                deviceBase = JSON.parseObject(content, DeviceBase.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("转换后的数据：{}", deviceBase);
            if (null == deviceBase) {
                log.info("设备信息为空1");
                return;
            }

            if (null == deviceBase.getPayload()) {
                log.info("设备信息为空2");
                return;
            }
            if (null == deviceBase.getPayload().getDesired()) {
                log.info("设备信息为空3");
                return;
            }
            if (null == deviceBase.getPayload().getDesired().getArrays()) {
                log.info("设备信息为空4");
                return;
            }
            Date date = new Date(deviceBase.getPayload().getTimestamp()*1000L);
            log.info("转换后的日期为：{}", ConstantsUtil.DF.format(date));
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);//获取年份
            int month = cal.get(Calendar.MONTH) + 1;//获取月份
            int day = cal.get(Calendar.DATE);//获取日
            int hour = cal.get(Calendar.HOUR_OF_DAY);//小时
            int minute = cal.get(Calendar.MINUTE);//分
            log.info("年：{},月：{},日：{},时：{},分：{}", year, month, day, hour, minute);
            // TODO 判断当前电量不为空
            Status status = deviceBase.getPayload().getDesired().getArrays().get(0).getStatus();
            if (null == status || null == status.getMeterd()) {
                log.info("设备信息为空5");
                return;
            }

            Integer switchIndex = deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getIndex();
            String serialNumber = deviceBase.getPayload().getDeviceid();
            //获取设备号的主账户
            //Long rtUserId = 1l;
            Long rtUserId = commonUtil.getRtUserIdBySerialNumber(deviceBase.getPayload().getDeviceid());
            if (null == rtUserId) {
                log.info("设备号：{}未获取到主账号id， ！", serialNumber);
                return;
            }
            Integer userType = (Integer)redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_SERIALNUMER+serialNumber);
            if(userType == null) {
                userType = commonUtil.getRtUserTypeBySerialNumber(serialNumber);
            }
            //当前时间
            String format = ConstantsUtil.DF.format(date);

            boolean flag = userType != null && userType > 0 ;
            boolean flagMain = "C63".equals(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getMode());
            //只统计主开关的漏电
            if(flag && status.getLeakage()!= null && flagMain) {
            	BigDecimal leakage = null;
            	PcLeakageHours entity = null;
            	try {
            	leakage = new BigDecimal(status.getLeakage());
            	entity = PcLeakageHours.builder().leakage(leakage).createTime(format).serialNumber(serialNumber)
                .switchIndex(switchIndex)
                .switchSn(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getId())
                .sYear(year)
                .sMonth(month)
                .sDay(day)
                .sTime(hour + 1)
                .userId(rtUserId)
                .projectId(Long.valueOf(userType))
                .createTime(format)
                .build();
            	//添加数据
            	pcLeakageHoursService.save(entity);
            	}catch (Exception e) {
            	    log.info("添加漏电流报错：电流{},添加数据：{}", leakage,entity);
                    log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
				}
            }
            //添加温度天数据
            if(flag && status.getTemp()!=null) {
            	BigDecimal temp = null;
            	PcTempHours entity = null;
            	try {
            	temp = new BigDecimal(status.getTemp());
            	entity = PcTempHours.builder().temp(temp).createTime(format).serialNumber(serialNumber)
                .switchIndex(switchIndex)
                .switchSn(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getId())
                .sYear(year)
                .sMonth(month)
                .sDay(day)
                .sTime(hour + 1)
                .userId(rtUserId)
                .projectId(Long.valueOf(userType))
                .createTime(format)
                .build();
            	//添加数据
            	pcTempHoursService.save(entity);
            	}catch (Exception e) {
            	    log.info("添加温度报错：温度{},添加数据：{}", temp,entity);
                    log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
				}
            }
            //添加负载  为零不统计
            if(flagMain && flag && CollectionUtils.isNotEmpty(status.getCurrent()) && CollectionUtils.isNotEmpty(status.getVoltage())
            		&&status.getCurrent().get(0) != 0
            		&& status.getVoltage().get(0)!= 0) {
            	//电流乘以电压  = 负载
            	BigDecimal power = null;
            	PcPowerHours entity = null;
            	try {
            	power = new BigDecimal((status.getCurrent().get(0)/1000)*(status.getVoltage().get(0)/1000));
            	entity = PcPowerHours.builder().power(power).createTime(format).serialNumber(serialNumber)
                .switchIndex(switchIndex)
                .switchSn(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getId())
                .sYear(year)
                .sMonth(month)
                .sDay(day)
                .sTime(hour + 1)
                .userId(rtUserId)
                .projectId(Long.valueOf(userType))
                .createTime(format)
                .build();
            	//添加数据
            	pcPowerHoursService.save(entity);
            	}catch (Exception e) {
            	    log.info("添加负载报错：负载值{},添加数据：{}", power,entity);
                    log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
				}
            }
            Map<String, Object> map = new HashMap<>();
            map.put("serial_number", serialNumber);//设备序列号
            //开关编号
            map.put("switch_index", switchIndex);
            map.put("s_year", year);
            map.put("s_month", month);
            map.put("s_day", day);
            //获取上一个小时的电量
            map.put("s_time", hour);


            //获取用户类型 =0 个人   >0 企业
            if(userType == null) {
                log.info("根据设备序列号获取用户类型为空");
                return;
            }else if(userType.equals(0)) {
                log.info("根据设备序列号获取用户类型为app");
                AppMeterHours appMeterHours = null;
                if (0 != hour) {
                    log.info("查询数据库是否存在上一次的数据");
                    appMeterHours = appMeterHoursService.getLastInfoByCondition(map);

                }
                //将要保存到数据库的电量
                BigDecimal meter = BigDecimal.ZERO;
                //设备上传的电量
                BigDecimal deviceMeter = deviceBase.getPayload().getDesired().getArrays().get(0).getStatus().getMeterd();
                log.info("设备上传的电量：{}", deviceMeter);
                if (null == appMeterHours) {
                    meter = deviceMeter;
                } else {
                    log.info("获取到之前的总电量：{}", appMeterHours.getMeter());
                    meter = deviceMeter.subtract(appMeterHours.getMeter());
                }
                //判断当前时间的数据是否存在
                map.put("s_time", hour + 1);
                Long id = appMeterHoursService.getCountByCondition(map);
                if (null == id || 0 == id) {
                    log.info("执行了插入操作");

                    //数据库没有数据，执行插入操作
                    appMeterHours = AppMeterHours.builder().meter(meter).createTime(ConstantsUtil.DF.format(date)).serialNumber(deviceBase.getPayload().getDeviceid())
                            .switchIndex(switchIndex)
                            .switchSn(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getId())
                            .sYear(year)
                            .sMonth(month)
                            .sDay(day)
                            .sTime(hour + 1)
                            .userId(rtUserId)
                            .projectId(Long.valueOf(userType))
                            .build();
                    appMeterHoursService.save(appMeterHours);
                    return;
                }
                log.info("执行了更新操作");
                //执行修改操作
                UpdateWrapper<AppMeterHours> updateWrapper = new UpdateWrapper<AppMeterHours>();
                updateWrapper.set("meter", meter);
                updateWrapper.set("update_time", ConstantsUtil.DF.format(date));
                updateWrapper.eq("id", id);
                appMeterHoursService.update(updateWrapper);
            }else {
                log.info("根据设备序列号获取用户类型为企业用户");
                pcMeterHoursService.getPcMeterHours(map,deviceBase,date,serialNumber,switchIndex,year,month,day,hour,userType);
                return;
            }
        }catch (Exception e){
            log.info("统计电量发生错误：{}",e.getStackTrace());
            log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
            log.error("统计电量发生错误：{}",e.getStackTrace());
        }

    }


    /**
     *
     * @Title: DeviceMeterMonthStatistics
     * @Description: 拉取队列电量数据到月表和年表中
     * @param type
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.STATISTICS_DAY_QUEUE)
    public void DeviceMeterMonthStatistics(Integer type){
    	Date date=new Date();
		//Calendar calendar =new GregorianCalendar();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);
		int year = calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH)+1;//获取月份
		int day = calendar.get(Calendar.DAY_OF_MONTH);
        if(type.equals(1)) {
            //拉取天的数据到月表中
            appMeterMonthsService.dayStatistics(year,month,day);
            pcLeakageMonthsService.dayStatistics(TableConfig.LEAKAGE, year, month, day);
            pcLeakageMonthsService.dayStatistics(TableConfig.TEMP, year, month, day);
            pcLeakageMonthsService.dayStatistics(TableConfig.POWER, year, month, day);
            pcLeakageMonthsService.dayStatistics(TableConfig.CURRENT, year, month, day);
            pcLeakageMonthsService.dayStatistics(TableConfig.VOLTAGE, year, month, day);
        }else if(type.equals(2)) {
            //拉取月的数据到年表中
            appMeterYearsService.monthStatistics(year,month);
            pcLeakageYearsService.monthStatistics(TableConfig.LEAKAGE, year, month);
            pcLeakageYearsService.monthStatistics(TableConfig.TEMP, year, month);
            pcLeakageYearsService.monthStatistics(TableConfig.CURRENT, year, month);
            pcLeakageYearsService.monthStatistics(TableConfig.VOLTAGE, year, month);
        }
    }

    /**
     *
     * @Title: DeviceMeterMonthStatistics
     * @Description: 拉取队列数据添加用户表中
     * @param
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.MODIFY_USER_NOTIFICATION)
    public void getUserStatistics(PcUserStatisticsVo pcUserStatisticsVo){
        if(pcUserStatisticsVo == null) {
            log.info("数据统计获取添加账户的队列参数错误");
            return;
        }
        log.info("数据统计获取添加账户的队列参数:{}",pcUserStatisticsVo);
        userStatisticsService.updateUser(pcUserStatisticsVo);
    }


    /**
     *
     * @Title: DeviceMeterMonthStatistics
     * @Description: 企业拉取队列电量数据到月表和年表中
     * @param type
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.STATISTICS_DAY_QUEUE_PC)
    public void DeviceMeterMonthStatisticsPc(Integer type){

        Date date=new Date();
        //Calendar calendar =new GregorianCalendar();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        int year = calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;//获取月份
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if(type.equals(1)) {
            //企业拉取天的数据到月表中
            pcMeterMonthsService.dayStatisticsPc(year, month, day);
        }else if(type.equals(2)) {
            //企业拉取月的数据到年表中
            pcMeterYearsService.monthStatisticsPc(year, month);
        }
    }

    /**
     *
     * @Title: updateChangeSwitch
     * @Description: 更换开关统计电量
     * @param
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.UPTATE_METER)
    public void updateMeterChangeSwitch(DeviceVo deviceVo){
        if(deviceVo == null) {
            log.info("统计电量获取更换开关的数据为空");
            return;
        }
        log.info("统计电量获取更换开关的数据:{}",deviceVo);
        pcStatisticsService.updateMeterChangeSwitch(deviceVo);
    }



    /**
     *
     * @Title: updateChangeSwitch
     * @Description: 更换开关统计电流
     * @param
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.UPTATE_LEAKAGE)
    public void updateleakageChangeSwitch(DeviceVo deviceVo){
        if(deviceVo == null) {
            log.info("统计电流获取更换开关的数据为空");
            return;
        }
        log.info("统计电流获取更换开关的数据:{}",deviceVo);
        pcStatisticsService.updateleakageChangeSwitch(deviceVo);
    }

    /**
     *
     * @Title: updateChangeSwitch
     * @Description: 更换开关统计温度
     * @param
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.UPTATE_TEMP)
    public void updateTempChangeSwitch(DeviceVo deviceVo){
        if(deviceVo == null) {
            log.info("统计温度获取更换开关的数据为空");
            return;
        }
        log.info("统计温度获取更换开关的数据:{}",deviceVo);
        pcStatisticsService.updateTempChangeSwitch(deviceVo);
    }


    /**
     *
     * @Title: updateChangeSwitch
     * @Description: 更换开关统计负载
     * @param
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.UPTATE_POWER)
    public void updatePowerChangeSwitch(DeviceVo deviceVo){
        if(deviceVo == null) {
            log.info("统计负载获取更换开关的数据为空");
            return;
        }
        log.info("统计负载获取更换开关的数据:{}",deviceVo);
        pcStatisticsService.updatePowerChangeSwitch(deviceVo);
    }



    /**
     *
     * @Title: selectLoseMeter
     * @Description: 定时检查丢失电量
     * @param
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.LOSE_METER)
    public void selectLoseMeter() {
        log.info("定时检查丢失电量队列监听方法selectLoseMeter");
        List<AppMeterHoursVo> appMeterHoursVoList = pcDeviceStatisticsService.selectDeviceMeter();
        log.info("定时检查丢失电量查询数据：{}", appMeterHoursVoList);
        if(CollectionUtils.isEmpty(appMeterHoursVoList)) {
            log.info("定时检查丢失电量查询数据为空");
         return;
        }
        log.info("定时检查丢失电量数据发送队列");
        rabbitTemplate.convertAndSend(QueueConstantUtil.SELECT_LOSE_METER,appMeterHoursVoList);
    }




//    @RabbitListener(queues = QueueConstantUtil.STATISTICS_METER_APP)
    @Transactional
    public void DeviceMeterStatistics1(String content) throws InterruptedException {
        try{
            log.info("接收到的数据：{}", content);
            Gson gson = new Gson();
            DeviceBase deviceBase = null;
            try{
                deviceBase = JSON.parseObject(content, DeviceBase.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("转换后的数据：{}", deviceBase);
            if (null == deviceBase) {
                log.info("设备信息为空1");
                return;
            }

            if (null == deviceBase.getPayload()) {
                log.info("设备信息为空2");
                return;
            }
            if (null == deviceBase.getPayload().getDesired()) {
                log.info("设备信息为空3");
                return;
            }
            if (null == deviceBase.getPayload().getDesired().getArrays()) {
                log.info("设备信息为空4");
                return;
            }


            Date date = new Date(deviceBase.getPayload().getTimestamp()*1000L);
            log.info("转换后的日期为：{}", ConstantsUtil.DF.format(date));
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);//获取年份
            int month = cal.get(Calendar.MONTH) + 1;//获取月份
            int day = cal.get(Calendar.DATE);//获取日
            int hour = cal.get(Calendar.HOUR_OF_DAY);//小时
            int minute = cal.get(Calendar.MINUTE);//分
            //上个月的时间
            int lMonth = 0;
            int lYear = 0;
            if(cal.get(Calendar.MONTH) == 0) {
                lMonth = 12;
                lYear = year - 1;
            }else {
                lYear = year;
                lMonth = month - 1;
            }
            //获取当前时间
            long timeD = deviceBase.getPayload().getTimestamp()*1000L;
            long timeS = System.currentTimeMillis();
            //设备上传时间小于服务器时间小于86400000ms(一天)
            if((timeS-timeD) > 86400000) {
                log.info("上传时间:{},与当前程序时间:{},相差大于一天",timeD, timeS);
                return;
            }


            log.info("年：{},月：{},日：{},时：{},分：{}", year, month, day, hour, minute);
            // TODO 判断当前电量不为空
            Status status = deviceBase.getPayload().getDesired().getArrays().get(0).getStatus();


            Integer switchIndex = deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getIndex();
            String serialNumber = deviceBase.getPayload().getDeviceid();
            //获取设备号的主账户
            //Long rtUserId = 1l;
            Long rtUserId = commonUtil.getRtUserIdBySerialNumber(deviceBase.getPayload().getDeviceid());
            if (null == rtUserId) {
                log.info("设备号：{}未获取到主账号id， ！", serialNumber);
                return;
            }
            Integer userType = (Integer)redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_SERIALNUMER+serialNumber);
            if(userType == null) {
                userType = commonUtil.getRtUserTypeBySerialNumber(serialNumber);
            }
            //当前时间
            String format = ConstantsUtil.DF.format(date);
            log.info("设备上传电量时间：{}", format);

            boolean flag = userType != null && userType > 0 ;
            boolean flagMain = "C63".equals(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getMode());

            //只统计主开关的漏电
            if(flag && status.getLeakage()!= null && flagMain) {
                BigDecimal leakage = null;
                PcLeakageHours entity = null;
                try {
                    leakage = new BigDecimal(status.getLeakage());
                    entity = PcLeakageHours.builder().leakage(leakage).createTime(format).serialNumber(serialNumber)
                            .switchIndex(switchIndex)
                            .switchSn(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getId())
                            .sYear(year)
                            .sMonth(month)
                            .sDay(day)
                            .sTime(hour + 1)
                            .userId(rtUserId)
                            .projectId(Long.valueOf(userType))
                            .createTime(format)
                            .build();
                    //添加数据
                    pcLeakageHoursService.save(entity);
                }catch (Exception e) {
                    log.info("添加漏电流报错：电流{},添加数据：{}", leakage,entity);
                }
            }
            //添加温度天数据
            if(flag && status.getTemp()!=null) {
                BigDecimal temp = null;
                PcTempHours entity = null;
                try {
                    temp = new BigDecimal(status.getTemp());
                    entity = PcTempHours.builder().temp(temp).createTime(format).serialNumber(serialNumber)
                            .switchIndex(switchIndex)
                            .switchSn(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getId())
                            .sYear(year)
                            .sMonth(month)
                            .sDay(day)
                            .sTime(hour + 1)
                            .userId(rtUserId)
                            .projectId(Long.valueOf(userType))
                            .createTime(format)
                            .build();
                    //添加数据
                    pcTempHoursService.save(entity);
                }catch (Exception e) {
                    log.info("添加温度报错：温度{},添加数据：{}", temp,entity);
                }
            }
            //添加负载  为零不统计
            if(flagMain && flag && CollectionUtils.isNotEmpty(status.getCurrent()) && CollectionUtils.isNotEmpty(status.getVoltage())
                    &&status.getCurrent().get(0) != 0
                    && status.getVoltage().get(0)!= 0) {
                //电流乘以电压  = 负载
                BigDecimal power = null;
                PcPowerHours entity = null;
                try {
                    power = new BigDecimal((status.getCurrent().get(0)/1000)*(status.getVoltage().get(0)/1000));
                    entity = PcPowerHours.builder().power(power).createTime(format).serialNumber(serialNumber)
                            .switchIndex(switchIndex)
                            .switchSn(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getId())
                            .sYear(year)
                            .sMonth(month)
                            .sDay(day)
                            .sTime(hour + 1)
                            .userId(rtUserId)
                            .projectId(Long.valueOf(userType))
                            .createTime(format)
                            .build();
                    //添加数据
                    pcPowerHoursService.save(entity);
                }catch (Exception e) {
                    log.info("添加负载报错：负载值{},添加数据：{}", power,entity);
                }
            }
            if (null == status || null == status.getFlag()) {
                log.info("设备信息为空5");
                return;
            }
            //获取用户类型 =0 个人   >0 企业
            if(userType == null) {
                log.info("根据设备序列号获取用户类型为空");
                return;
            }
            //查询是否是主动上传
            Integer flagInteger = deviceBase.getPayload().getDesired().getArrays().get(0).getStatus().getFlag();
            if(flagInteger == null || flagInteger.equals("")){
                log.info("根据主动上传flag标识为空");
                return;
            }
            //区间电量的时间
            Integer dataTime = deviceBase.getPayload().getDesired().getArrays().get(0).getStatus().getTime();
//            Integer dataTime =14;

            //区间电量
            BigDecimal meterh =  deviceBase.getPayload().getDesired().getArrays().get(0).getStatus().getMeterh();
//            BigDecimal meterh = new BigDecimal("14");

            //设备上传的当天的电量
            BigDecimal meterd = deviceBase.getPayload().getDesired().getArrays().get(0).getStatus().getMeterd();


            //上一天的电量
//            BigDecimal meterdlast = deviceBase.getPayload().getDesired().getArrays().get(0).getStatus().getMeterdlast();
//            BigDecimal meterdlast = new BigDecimal("12.2");

            //当月的电量
            BigDecimal meterdm = deviceBase.getPayload().getDesired().getArrays().get(0).getStatus().getMeterm();



            //上个月的月
//            Integer meterdLastMonth = deviceBase.getPayload().getDesired().getArrays().get(0).getMeter().get(0).getMonth();

            //上个月的电量值
//            BigDecimal value = deviceBase.getPayload().getDesired().getArrays().get(0).getMeter().get(0).getValue();


            log.info("设备上传的电量的状态：{},设备上传的区间时间：{},区间电量：{},当天的电量：{}, 当月的电量：{}",
                    flagInteger,dataTime, meterh, meterd, meterdm);

            Long time = deviceBase.getPayload().getTimestamp()*1000L;

            //上一天的时间
            Map<String, Object> map = dataUtil.lastTime(time);
            Integer lastYear = (Integer) map.get("year");
            //月
            Integer lastMonth = (Integer) map.get("month");
            //日
            Integer lastDay = (Integer) map.get("day");
            log.info("上一天的年：{},月：{},日：{},时：{},分：{}", lastYear, lastMonth, lastDay);
            ParametersDto parametersDto = ParametersDto.builder()
                    .tableName(TableConfig.METER)
                    .serialNumber(serialNumber)
                    .switchSn(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getId())
                    .switchIndex(switchIndex)
                    .userId(rtUserId)
                    .createTime(ConstantsUtil.DF.format(date))
                    .build();
            parametersDto.setProjectId(userType);
            parametersDto.setData(meterh);
            parametersDto.setSTime(dataTime);

            //插入小时表
            //如果上传的区间时间24且当前时间的小时是0
            parametersDto.setType(2);
            if(flagInteger != null && flagInteger.equals(1) && dataTime != null && hour == 0 && dataTime.equals(24)) {
                //重新上传的失败电量
                parametersDto.setSDay(lastDay);
                parametersDto.setSMonth(lastMonth);
                parametersDto.setSYear(lastYear);
                int hoursTotalL = commonUtil.commonMeterUpdate(parametersDto, meterh);
                log.info("修改上一个小时的表的数据条数:{}",hoursTotalL);


            }else if(flagInteger != null && !flagInteger.equals(2) && !flagInteger.equals(3)) {
                parametersDto.setSDay(day);
                parametersDto.setSMonth(month);
                parametersDto.setSYear(year);
                int hoursTotal = commonUtil.commonMeterUpdate(parametersDto, meterh);
                log.info("修改当天小时表的数据条数:{}",hoursTotal);

            }
//            if(flagInteger != null) {
//                log.info("添加小时表的数据为:{}",parametersDto);
//                log.info("根据设备序列号获取用户类型为:{}",parametersDto.getPlatform());
//                int hourTotal = appMeterHoursService.insertByOne(parametersDto);
//                log.info("修改小时表的数据条数:{}",hourTotal);
//
//            }

            //月表
            parametersDto.setType(1);
            if(flagInteger != null && flagInteger.equals(2) && dataTime != null
                    && lastDay != null && dataTime.equals(lastDay)) {
                //上一天的查询 meterdlast
                parametersDto.setSDay(lastDay);
                parametersDto.setSMonth(lastMonth);
                parametersDto.setSYear(lastYear);
                int monthTotalL = commonUtil.commonMeterUpdate(parametersDto, meterd);
                log.info("修改上一天月表的数据条数:{}",monthTotalL);
            }else if (flagInteger != null && flagInteger.equals(0)){
                //当天的查询 meterd
                parametersDto.setSDay(day);
                parametersDto.setSMonth(month);
                parametersDto.setSYear(year);
                int monthTotal = commonUtil.commonMeterUpdate(parametersDto, meterd);
                log.info("修改当天月表的数据条数:{}",monthTotal);

            }

            //年表
            parametersDto.setType(0);
            if(flagInteger != null && flagInteger.equals(3) && dataTime != null && dataTime.equals(lMonth)) {
                //上个月的查询 value
                parametersDto.setSMonth(lMonth);
                parametersDto.setSYear(lYear);
                int yearTotalL = commonUtil.commonMeterUpdate(parametersDto, meterdm);
                log.info("修改上个月年表的数据条数:{}",yearTotalL);
            }else if (flagInteger != null && flagInteger.equals(0)){
                //当月的查询 meterdm
                parametersDto.setSMonth(month);
                parametersDto.setSYear(year);
                int yearTotal = commonUtil.commonMeterUpdate(parametersDto, meterdm);
                log.info("修改当月年表的数据条数:{}",yearTotal);
            }
        }catch (Exception e){
            log.info("统计电量发生错误：{}",e.getStackTrace());
            log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
            log.error("统计电量发生错误：{}",e.getStackTrace());
        }

    }


    /**
     *
     * @Title: selectLoseMeter
     * @Description: 水表的抄表记录
     * @param
     * @return: void
     */
    @RabbitListener(queues = QueueConstantUtil.WATER_RECORD)
    public void selectWaterRecord(Date time) {
        log.info("自动拉取抄表统计,time:{}", time);
        //获取项目id下的水表编号
        List<String> stringList = waterUtil.querySubBuildingWaterId(0L, 0, 0L);
        if(CollectionUtils.isEmpty(stringList)) {
            log.info("自动拉取抄表统计获取水表编号为空！！！");
            return;
        }
        insetListWaterS(time, 0, stringList,0);
//        insetListWater(time, 0);
    }



    /**
     *
     * @Title: selectList
     * @Description: 获取抄表数据
     * @param recordByGtId
     * @return: java.util.List<cn.meiot.entity.WaterStatistics>
     */
    public List<WaterStatistics> selectList(List<Record> recordByGtId) {

        //获取所有水表编号id
        List<String> stringList = recordByGtId.stream().map(Record::getMeterid).collect(Collectors.toList());

        Set<String> customerList = new HashSet<>();
        customerList.addAll(stringList);

        log.info("customerList数据：{}", customerList);
        if(CollectionUtils.isEmpty(customerList)) {
            log.info("水表列表获取的设备编号为空！！！！");
            return null;
        }
        //设备号获取项目id ,名称，地址，单位
        Map<String, WaterAddressVo> stringWaterAddressVoMap = deviceFeign.queryWaterUser(customerList);

        List<WaterStatistics> waterStatisticsList = new ArrayList<>();
        if(stringWaterAddressVoMap != null && !CollectionUtils.isEmpty(customerList)) {
            for (Record record : recordByGtId) {
                String meterid = record.getMeterid();
                WaterAddressVo waterAddressVo = stringWaterAddressVoMap.get(meterid);
                WaterStatistics waterStatistics = new WaterStatistics();
                BeanUtils.copyProperties(record, waterStatistics);

//                WaterStatistics waterStatisticsT = waterStatistics;
                if(waterAddressVo == null) {
                    continue;
                }else {
                    BeanUtils.copyProperties(waterAddressVo, waterStatistics);
                    waterStatisticsList.add(waterStatistics);
                }
            }
        }
        return waterStatisticsList;
    }



    /**
     *
     * @Title: inserRecord
     * @Description: 比较数据库的数据跟水表查询的数据对比
     * @param
     * @return: java.util.List<cn.meiot.entity.water.Record>
     */
    public List<Record> inserRecord(List<Record> list, Long recordId) {
        List<Record> listRecord = new ArrayList<>();
        //查询数据库的水表列表
        List<Long> strings = waterStatisticsMapper.queryWaterMeterBySetId();
        if(CollectionUtils.isEmpty(list)) {
            log.info("传入水表数据为空！！！");
            return listRecord;
        }
        if(CollectionUtils.isEmpty(strings) && recordId == null) {
            log.info("数据库查询的数据为空！！！");
            return list;
        }
        for(int i = list.size() -1 ;i>= 0 ;i--){
            Record record = list.get(i);
            Long id = record.getId();
             if(strings.contains(id)){
               list.remove(i);
//               strings.remove(meterid);
            }
        }
        return list;
    }



    @Transactional
    public void insetListWaterS(Date time, Integer type, List<String> stringList, Integer projectId) {
        //正常的数据
        List<WaterStatistics> waterStatisticsList = new ArrayList<>();
        try {
            //查询水表的队列(队列时间 == 数据库队列表的创建时间)
            int i = waterQueueService.selectByTime(time);
            Long aLong = null;
            //i的数量为0,则使用队列拉取数据
            if ((i == 0 && type.equals(0)) || type.equals(1)) {

                if (CollectionUtils.isNotEmpty(stringList)) {

                    WaterStatistics waterStatistics = new WaterStatistics();

                    //添加抄表记录
                    List<Record> recordByGtId = new ArrayList<>();


                    //修改抄表记录
                    List<Record> recordUpdate = new ArrayList<>();


                    List<Record> recordByGtIdW = new ArrayList<>();

                    //根据抄表的最大id查询抄表记录
                    Map map = null;


                    for (String meterId : stringList) {
                        map = new HashMap();
                        map.put("order", "id");
                        //以水表编号查询
                        map.put("meterid", meterId);
                        waterStatistics.setMeterid(meterId);
                        waterStatistics.setProjectId(projectId);

                        //读取数据id
                        aLong = waterStatisticsService.queryWaterMeterId(waterStatistics);

                        //添加数据
                        map.put("sort", "desc");
                        //获取抄表数据 type（0 ：为添加, 1：为修改，-1为所有数据）
                        //获取全部数据
                        if (aLong == null) {
                            recordByGtIdW = waterUtils.getRecordByGtId(map, aLong, null);
                            //添加抄表记录
                            recordByGtId.addAll(recordByGtIdW);
                            log.info("获取添加抄表列表recordByGtId：{}", recordByGtId);
                        } else {
                            //添加数据
                            //获取抄表数据 type（0 ：为添加, 1：为修改）
                            recordByGtIdW = waterUtils.getRecordByGtId(map, aLong, 0);
                            log.info("新增recordByGtIdW：{}", recordByGtIdW);


                            //修改数据
                            map.put("sort", "asc");
//                            map.put("checked","true");
                            List<Record> recordByGtIdUpdate = waterUtils.getRecordByGtId(map, aLong, 1);

                            List<Record> recordListWater = new ArrayList<>();
                            recordListWater.addAll(recordByGtIdUpdate);
                            log.info("修改抄表记录 recordByGtIdUpdate：{}", recordByGtIdUpdate);

                            //小于数据库最大id有新增的抄表记录
                            List<Record> recordList = inserRecord(recordListWater, aLong);
                            recordByGtIdW.addAll(recordList);
                            //添加抄表记录
                            recordByGtId.addAll(recordByGtIdW);

                            recordByGtIdUpdate = recordByGtIdUpdate.stream().filter(record -> "true".equals(record.getChecked())).collect(Collectors.toList());

                            //修改抄表数据
                            recordUpdate.addAll(recordByGtIdUpdate);

                            log.info("修改抄表记录 recordUpdate：{}", recordUpdate);

                        }
                    }
                    if (CollectionUtils.isEmpty(recordByGtId) && CollectionUtils.isEmpty(recordUpdate)) {
                        log.info("获取抄表列表recordByGtId,recordUpdate数据为空");
                        return;
                    }

                    //数据修改集合
                    Map mapWater = new HashMap();
                    //添加
                    if (!CollectionUtils.isEmpty(recordByGtId)) {
                        waterStatisticsList = selectList(recordByGtId);
                        if (!CollectionUtils.isEmpty(waterStatisticsList)) {
                            mapWater.put("addWaterRecord", waterStatisticsList);
                            //插入抄表数据
                            waterStatisticsService.saveBatch(waterStatisticsList);
                        }
                    }
                    //修改
                    if (!CollectionUtils.isEmpty(recordUpdate)) {
                        waterStatisticsList = selectList(recordUpdate);
                        if (!CollectionUtils.isEmpty(waterStatisticsList)) {
                            mapWater.put("updateWaterRecord", waterStatisticsList);
                            //插入抄表数据
                            Integer integer = waterStatisticsService.saveWaterMeter(waterStatisticsList);
                            log.info("修改抄表统计数据条数", integer);
                        }
                    }

                    if (type != null && type.equals(0)) {
                        WaterQueue waterQueue = new WaterQueue();
                        waterQueue.setCreateTime(time);
                        waterQueue.setStatus(1);
                        waterQueue.setDescription("插入水表的抄表记录队列");
                        //插入队列表
                        waterQueueService.save(waterQueue);
                        log.info("自动拉取抄表统计队列消费成功");
                    } else {
                        log.info("手动拉取抄表统计队列消费成功");
                    }

                    //查询数据库看数据是否为空
                    Integer integer = waterStatisticsMonthsMapper.selectWaterMeter();
                    if(integer == null) {
                        mapWater = null;
                    }
                    //拉取数据后修改队列
                    rabbitTemplate.convertAndSend(QueueConstantUtil.UPDATE_WATER_RECORD, mapWater);
                }
            } else {
                log.info("抄表统计队列重复消费,time：{}", time);
                return;
            }
        } catch (Exception e) {
            //删除key
            redisTemplate.delete(ConstantsUtil.REDIS_WATER_PROJECT + projectId);
            log.error("水表的抄表记录！:正常数据waterStatisticsList:{}", waterStatisticsList);
            log.error("错误信息：{},======[]{},=========[]{}", e.getMessage(), e.getCause(), e.getSuppressed());
            log.info("错误信息 ->", e);
            return;
        }

    }

}


