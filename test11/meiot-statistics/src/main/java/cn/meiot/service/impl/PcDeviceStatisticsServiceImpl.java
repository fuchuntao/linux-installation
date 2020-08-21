package cn.meiot.service.impl;

import cn.meiot.config.TableConfig;
import cn.meiot.entity.PcDeviceStatistics;
import cn.meiot.entity.bo.Crcuit;
import cn.meiot.entity.bo.MeterStatisticalBo;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.mapper.AppMeterYearsMapper;
import cn.meiot.mapper.PcDeviceStatisticsMapper;
import cn.meiot.mapper.PcMeterYearsMapper;
import cn.meiot.service.IPcDeviceStatisticsService;
import cn.meiot.utils.DataUtil;
import cn.meiot.utils.NetworkingUtlis;
import cn.meiot.utils.NumUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备数据统计表 服务实现类
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Service
@Slf4j
public class PcDeviceStatisticsServiceImpl extends ServiceImpl<PcDeviceStatisticsMapper, PcDeviceStatistics> implements IPcDeviceStatisticsService {

    private DeviceFeign deviceFeign;

    private RedisTemplate redisTemplate;

    private NetworkingUtlis networkingUtlis;

    @Autowired
    private DataUtil dataUtil;


    @Autowired
    private PcDeviceStatisticsMapper pcDeviceStatisticsMapper;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private PcMeterYearsMapper pcMeterYearsMapper;

    @Autowired
    private AppMeterYearsMapper appMeterYearsMapper;

    @Autowired
    private AppMeterYearsServiceImpl appMeterYearsService;


    public PcDeviceStatisticsServiceImpl(DeviceFeign deviceFeign, RedisTemplate redisTemplate, NetworkingUtlis networkingUtlis) {
        this.deviceFeign = deviceFeign;
        this.redisTemplate = redisTemplate;
        this.networkingUtlis = networkingUtlis;
    }

    @Override
    public Result queryDeviceInfo(Long userId, Integer projectId) {
        Result result = Result.getDefaultTrue();
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> serialNumbers = deviceFeign.getSerialNUmbersByProjectId(projectId);
        if (null == serialNumbers || serialNumbers.size() == 0) {
            log.info("获取到的项目设备列表为空");
            map.put("deivceSum", 0);
            map.put("onLineDevice", 0);
            map.put("loadRate", 0);
        } else {
            log.info("获取到的项目设备列表：{}", serialNumbers);
            //获取设备总数量以及设备在线数量
            deviceNum(serialNumbers, map);
            //计算负载率
            loadRate(projectId, serialNumbers, map);
        }
        result.setData(map);
        return result;
    }


    /**
     * 计算设备总数量以及设备在线数量
     *
     * @param map
     */
    private void deviceNum(List<String> serialNumbers, Map<String, Object> map) {
        if (null == serialNumbers || serialNumbers.size() == 0) {
            log.info("当前项目下未找到设备");
            map.put("deivceSum", 0);
            map.put("onLineDevice", 0);
        } else {
            //获取设备的在线数量
            Integer num = networkingUtlis.deviceOnLineNum(serialNumbers);
            map.put("deivceSum", serialNumbers.size());
            map.put("onLineDevice", num);
        }
    }


    /**
     * 计算负载率
     */
    private void loadRate(Integer projectId, List<String> serialNumbers, Map<String, Object> map) {
        //获取总负载
        Crcuit crcuit = JSONObject.parseObject(JSONObject.toJSONString(redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_PARAMETER + projectId)), Crcuit.class);
        if (null == crcuit) {
            log.info("为获取到项目id：{}的总负载", projectId);
            map.put("loadRate", 0);
            return;
        }
        BigDecimal totalLoad = crcuit.getTotalLoad();
        if (totalLoad == null || totalLoad.compareTo(BigDecimal.ZERO) == 0) {
            log.info("后台设置当前项目{}的总负载为零", projectId);
            map.put("loadRate", 0);
            return;
        }
        log.info("总负载：{}", totalLoad);
        //计算所有设备的负载之和
        BigDecimal deviceLoadSum = deviceLoad(serialNumbers);
        //计算负载率
        BigDecimal percent = NumUtil.percent(deviceLoadSum, totalLoad);
        log.info("负载率：{}", percent);
        map.put("loadRate", percent);
    }

    /**
     * 统计指定设备下的负载总和
     *
     * @param serialNumbers
     * @return
     */
    private BigDecimal deviceLoad(List<String> serialNumbers) {
        //电流
        BigDecimal current = BigDecimal.ZERO; //BigDecimal.valueOf(Double.valueOf(status.getCurrent().toString()));
        //电压
        BigDecimal voltage = BigDecimal.ZERO;// BigDecimal.valueOf(Double.valueOf(status.getVoltage().toString()));
        //通过设备号查询主开关信息
        BigDecimal deviceLoadSum = BigDecimal.ZERO;
        List<SerialNumberMasterVo> list = deviceFeign.queryMasterIndexBySerialNUmber(serialNumbers);
        log.info("获取到的主开关好：{}", list);
        for (SerialNumberMasterVo s : list) {
            //Status status = (Status) redisTemplate.opsForHash().get(RedisConstantUtil.DEVICE, s.getMasterSn());
            Object value = redisTemplate.opsForHash().get(RedisConstantUtil.DEVICE, s.getMasterSn().toString());
            log.info("value：{}", value);
            if (null == value) {
                continue;
            }
            Map map = new Gson().fromJson(value.toString(), Map.class);
            //Map map = (Map) redisTemplate.opsForHash().get(RedisConstantUtil.DEVICE, s.getMasterSn());
            Object current1 = map.get("current");
            log.info("设备号：{}；电流：{}", s.getSerialNumber(), current1.toString());
            if (null != current) {
                List<Double> doubles = (List<Double>) current1;
                current = BigDecimal.valueOf(doubles.get(0)).divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_HALF_UP);
                log.info("设备号：{}；电流：{}", s.getSerialNumber(), current);
            }
            Object voltage1 = map.get("voltage");
            log.info("设备号：{}；电压：{}", s.getSerialNumber(), voltage1.toString());
            if (null != voltage1) {
                List<Double> voltages = (List<Double>) voltage1;
                voltage = BigDecimal.valueOf(voltages.get(0)).divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_HALF_UP);
                log.info("设备号：{}；电压：{}", s.getSerialNumber(), voltage);
            }
            if (current.compareTo(BigDecimal.ZERO) == 0 || voltage.compareTo(BigDecimal.ZERO) == 0) {

            } else {
                deviceLoadSum = deviceLoadSum.add(current.multiply(voltage));
                deviceLoadSum.setScale(2, BigDecimal.ROUND_HALF_UP);
                log.info("deviceLoadSum:{}", deviceLoadSum);
            }
        }
        log.info("设备总负载：{}", deviceLoadSum);
        return deviceLoadSum;
    }

    /**
     * @param
     * @Title: selectDeviceMeter
     * @Description: 电量丢失统计
     * @return: java.util.List<cn.meiot.entity.AppMeterHours>
     */
    @Override
    public List<AppMeterHoursVo> selectDeviceMeter() {

        List<AppMeterHoursVo> appMeterHoursTime = new ArrayList<>();

        List<AppMeterHoursVo> appMeterHoursVos = pcDeviceStatisticsMapper.selectDeviceStatistics();
        //获取所有的设备号
        List<String> stringList = appMeterHoursVos.stream().map(AppMeterHoursVo::getSerialNumber).collect(Collectors.toList());
        //查询设备不在线的
        List<String> list = networkingUtlis.deviceOnLineNumList(stringList);
        //去重设备的不在线的
        if (!CollectionUtils.isEmpty(list) && !CollectionUtils.isEmpty(appMeterHoursVos)) {
            for (String serialNumber : list) {
                for (int i = appMeterHoursVos.size() - 1; i >= 0; i--) {
                    if (StringUtils.isNotBlank(serialNumber) && serialNumber.equals(appMeterHoursVos.get(i).getSerialNumber())) {
                        //移除不在线的设备
                        appMeterHoursVos.remove(i);
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(appMeterHoursVos)) {
            for (AppMeterHoursVo appMeterHoursVo : appMeterHoursVos) {
                //项目id
                Integer projectId = appMeterHoursVo.getProjectId();

                //设备号
                String serialNumber = appMeterHoursVo.getSerialNumber();
                //开关号
                List<DeviceVo> switchSnList = appMeterHoursVo.getSwitchSnList();

//                List<Long> switchSnList = appMeterHoursVo.getSwitchSnList();
                //判断是否有项目id
                if (projectId != null) {
                    //app
                    //查询开关的时间和设备号
                    //判断当前的时间小时是否是0点如果是则是统计昨天的数据


                    long millisNow = System.currentTimeMillis();
                    Calendar calStartDataUtil = Calendar.getInstance();

                    calStartDataUtil.setTimeInMillis(millisNow);
                    //开始时间的年
                    int startYear = calStartDataUtil.get(Calendar.YEAR);
                    //月
                    int startMonth = calStartDataUtil.get(Calendar.MONTH) + 1;
                    //日
                    int startDay = calStartDataUtil.get(Calendar.DATE);
                    //小时
                    int hour = calStartDataUtil.get(Calendar.HOUR_OF_DAY);

                    ParametersDto parametersDto = new ParametersDto();

                    parametersDto.setSerialNumber(serialNumber);
                    parametersDto.setTableName(TableConfig.METER);
                    parametersDto.setType(2);
                    parametersDto.setProjectId(projectId);
                    Integer hourTime = null;
                    if (hour == 0) {
                        hourTime = hour;
                        //获取前一天的时间
                        Map<String, Object> map = dataUtil.lastTime(millisNow);

                        startYear = (Integer) map.get("year");
                        //月
                        startMonth = (Integer) map.get("month");
                        //日
                        startDay = (Integer) map.get("day");
                        hour = 24;
                    }
                    parametersDto.setSYear(startYear);
                    parametersDto.setSMonth(startMonth);
                    parametersDto.setSDay(startDay);
                    parametersDto.setSTime(hour);

                    if (!CollectionUtils.isEmpty(switchSnList)) {
                        List<Map<String, Object>> mapList = new ArrayList<>();
                        //当小时为0时查询传上一天和上一个月的数据 flag 1表示小时,2:日,3:月
                        if (hourTime != null && hourTime.equals(0)) {
                            Set<Integer> setTime = new HashSet<>();
                            //上一天
                            Map<String, Object> mapTime = new HashMap<>();
                            mapTime.put("flag", 2);
                            setTime.add(startDay);
                            mapTime.put("time", setTime);
                            mapList.add(mapTime);
                            //上一个月
                            int i = calStartDataUtil.get(Calendar.MONTH);
                            if (i == 0) {
                                i = 12;
                            }
                            setTime = new HashSet<>();
                            Map<String, Object> mapT = new HashMap<>();
                            mapT.put("flag", 3);
                            setTime.add(i);
                            mapT.put("time", setTime);
                            mapList.add(mapT);
                        }

                        //上传小时的电量
                        Set<Integer> setTimeAll = new HashSet<>();
                        //一个设备多个开关
                        for (DeviceVo sn : switchSnList) {
                            parametersDto.setSwitchSn(sn.getOldSwitchSn());
                            //获取有电量上传的小时
                            List<Integer> integerList = pcDeviceStatisticsMapper.selectListTime(parametersDto);
                            List<Integer> listTime = DataUtil.listTime(hour);
                            //获取某个开关电量的没有上传的时间点
                            if (!CollectionUtils.isEmpty(integerList)) {
                                listTime.removeAll(integerList);
                            }
                            if (!CollectionUtils.isEmpty(listTime)) {
                                setTimeAll.addAll(listTime);
                            }
                        }
                        if (setTimeAll != null && setTimeAll.size() > 0) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("flag", 1);
                            map.put("time", setTimeAll);
                            mapList.add(map);

                        }
                        if (!CollectionUtils.isEmpty(mapList)) {
                            appMeterHoursVo.setMapList(mapList);
                            appMeterHoursTime.add(appMeterHoursVo);
                        }
                    }

                }

            }
        }
        return appMeterHoursTime;
    }

    @Override
    public BigDecimal getDeviceLine(Long userId, Integer projectId) {
        //判断查询的是个人还是企业
        Map<String, Object> map = new HashMap<String, Object>();
        if (null == projectId || projectId == 0) {
            //个人版
            return  getSingleDeviceLine(userId);
        } else {
            //企业版
            List<String> serialNumbers = deviceFeign.getSerialNUmbersByProjectId(projectId);
            if (null == serialNumbers || serialNumbers.size() == 0) {
                log.info("获取到的项目设备列表为空");
                map.put("deivceSum", 0);
                map.put("onLineDevice", 0);
                map.put("loadRate", 0);
            } else {
                log.info("获取到的项目设备列表：{}", serialNumbers);
                //获取设备总数量以及设备在线数量
                deviceNum(serialNumbers, map);
                return calculate(map);
            }
        }
        return null;
    }

    @Override
    public Result meterTop(Long userId, Integer year, Integer month, Integer projectId) {
        //判断个人还是企业
        List<MeterStatisticalBo> list = null;
        if(null != projectId){
            List<String> serialNumbers = deviceFeign.getSerialNUmbersByProjectId(projectId);
            List<SerialNumberMasterVo> serialNumberMasterVos = deviceFeign.queryMasterIndexBySerialNUmber(serialNumbers);
//            log.info("企业设备用电top serialNumberMasterVos：{},projectId:{},year:{},month：{}", serialNumberMasterVos,projectId,year,month);
            list = pcMeterYearsMapper.getMeterTopByProjectId(serialNumberMasterVos,projectId,year,month);
            if(null == list || list.size() ==  0 ){
                return  Result.getDefaultTrue();
            }

            //获取主账号id
            Long masterUserId = userFeign.getMainUserId(userId);
            log.info("设备用电top(个人)获取主账号：{}", masterUserId);
            if(null == masterUserId){
                return new Result().Faild("获取主账号失败！");
            }
            //获取设备昵称
            list.forEach( l ->{
                String serialName = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER,masterUserId+"_"+l.getName());
                if(null != serialName){
                    l.setName(serialName);
                }
                log.info("设备编号：{},",serialName);
            });
        }else{
            //个人版
            List<PersonalSerialVo> personalSerialVos = deviceFeign.querySerialAndMaster(userId);
//            personalSerialVos = new ArrayList<PersonalSerialVo>();
//            PersonalSerialVo p  = new PersonalSerialVo();
//            p.setSerial("M2202001150009");
//            p.setMasterSn(1224130425l);
//            p.setMasterId(10000983l);
//            personalSerialVos.add(p);
            //查询用电量前10
            log.info("查询用户id:{}，在设备服务查询到的设备信息：{}",userId,personalSerialVos);
            list = appMeterYearsMapper.getTopMeter(personalSerialVos,year,month);
            if(null == list || list.size() ==  0 ){
                return  Result.getDefaultTrue();
            }
            list.forEach( l ->{
                String serialName = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER,userId+"_"+l.getName());
                log.info("设备编号：{},设备昵称：{}",l.getName(),serialName);
                if(null != serialName){
                    l.setName(serialName);
                }

            });
        }
//        log.info("list设备电量top10:{}",list);
        BigDecimal totalMeter = BigDecimal.ZERO;
        for (MeterStatisticalBo meterStatisticalBo : list) {
            BigDecimal value = meterStatisticalBo.getValue();
            totalMeter = totalMeter.add(value);
        }

        if( totalMeter == null || totalMeter.compareTo(BigDecimal.ZERO) == 0) {
            //数据空
            log.info("电量汇总数据为空" , userId);
            return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(),ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
        }

        Result result = Result.getDefaultTrue();
        result.setData(list);
//        log.info("设备电量top10:{}",result);

        return result;
    }

    @Override
    public Result meterToYear(Long userId, Integer year, Integer projectId) {
        //获取当前用户下的所有设备
//        List<PersonalSerialVo> list = deviceFeign.querySerialAndMaster(userId);
        List<PersonalSerialVo> list = appMeterYearsService.getPersonalSerialVos(userId, projectId);
        log.info("当前用户：{} 拥有的设备信息：{}",userId,list);
        if(null == list || list.size() == 0 ){
            return  Result.getDefaultTrue();
        }
        List<MeterStatisticalBo> yearData = new ArrayList<MeterStatisticalBo>();
        //获取制定年份的数据
        if(projectId.equals(0)) {
            yearData = appMeterYearsMapper.getMeterMonth(list,year,userId);
        }else {
            yearData = appMeterYearsMapper.getEnterpriseMeterMonth(list,year,userId,projectId);
        }
        log.info("查询到的年度统计信息：{}",yearData);
        BigDecimal totalMeter = BigDecimal.ZERO;
        for (MeterStatisticalBo meterStatisticalBo : yearData) {
            BigDecimal value = meterStatisticalBo.getValue();
            totalMeter = totalMeter.add(value);
        }

        if( totalMeter == null || totalMeter.compareTo(BigDecimal.ZERO) == 0) {
            //数据空
            log.info("电量汇总数据为空" , userId);
            return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(),ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
        }

        //将没有的月份自动补0
        yearData =  DataUtil.complementedMonth(yearData,12);
        Result result = Result.getDefaultTrue();
        result.setData(yearData);
        return result;
    }

    /**
     * 获取个人版的设备在线率
     * @param userId
     * @return
     */
    private BigDecimal getSingleDeviceLine(Long userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        //获取当前用户下的所有设备
        List<PersonalSerialVo> list = deviceFeign.querySerialAndMaster(userId);
        log.info("当前用户：{}，拥有的设备：{}",userId,list);
        if(null == list){
            return BigDecimal.ZERO;
        }
        List<String> serialNumbers = new ArrayList<String>();
       for (PersonalSerialVo s: list){
           serialNumbers.add(s.getSerial());
       }
        deviceNum(serialNumbers, map);
        return calculate(map);
    }

    /**
     * 计算
     * @param map
     * @return
     */
    private BigDecimal  calculate(Map<String, Object> map){
        BigDecimal deviceNum = BigDecimal.valueOf((Integer) map.get("deivceSum"));
        BigDecimal  onLineDevice = BigDecimal.valueOf((Integer) map.get("onLineDevice"));
        //计算设备在线率
        return NumUtil.percent(onLineDevice, deviceNum);
    }

}
