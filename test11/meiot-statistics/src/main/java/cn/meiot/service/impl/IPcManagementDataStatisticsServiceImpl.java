package cn.meiot.service.impl;

import cn.meiot.config.TableConfig;
import cn.meiot.entity.bo.BatteryLeftBo;
import cn.meiot.entity.bo.PcMeterBo;
import cn.meiot.entity.bo.UserNumBo;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.AccountType;
import cn.meiot.feign.AftersaleFeign;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.mapper.PcMeterHoursMapper;
import cn.meiot.mapper.PcMeterMonthsMapper;
import cn.meiot.mapper.PcMeterYearsMapper;
import cn.meiot.service.IPcManagementDataStatisticsService;
import cn.meiot.service.IUserStatisticsService;
import cn.meiot.utils.DataUtil;
import cn.meiot.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: IPcManagementDataStatisticsServiceImpl
 * @Description: 数据统计
 * @author: 符纯涛
 * @date: 2019/9/20
 */
@Service
@Slf4j
public class IPcManagementDataStatisticsServiceImpl implements IPcManagementDataStatisticsService {

    @Autowired
    private AftersaleFeign aftersaleFeign;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private IUserStatisticsService userStatisticsService;

    @Autowired
    private PcMeterYearsMapper pcMeterYearsMapper;

    @Autowired
    private PcMeterMonthsMapper pcMeterMonthsMapper;

    @Autowired
    private PcMeterHoursMapper pcMeterHoursMapper;

    @Autowired
    private DataUtil dataUtil;

    private Calendar cal = Calendar.getInstance();


    /**
     * 统计个人用户与企业用户的数量
     * @param pcDataStatisticsVo
     */
    private void countUserSumAndCompanyUserSum(PcDataStatisticsVo pcDataStatisticsVo){
        //获取企业用户和个人用户数量
        //UserNumBo userNumBo = userStatisticsService.getCount(AccountType.ENTERPRISE.value(),AccountType.PERSONAGE.value());
        UserNumBo userNumBo = userFeign.getUserNum();
        //获取企业注册数量
        pcDataStatisticsVo.setUserSum(userNumBo.getUserSum());
        pcDataStatisticsVo.setCompanyUserSum(userNumBo.getCompanyUserSum());
    }

    /**
     * 统计设备总数量
     * @param pcDataStatisticsVo
     */
    private void computeDeviceTotal(PcDataStatisticsVo pcDataStatisticsVo){
        //获取设备总数量
        pcDataStatisticsVo.setDeviceSum(deviceFeign.queryDeviceTotal(null));
    }

    /**
     * 统计总项目数量
     * @param pcDataStatisticsVo
     */
    private void computeProjectTotal(PcDataStatisticsVo pcDataStatisticsVo){
        pcDataStatisticsVo.setProjectSum(userFeign.queryProjectTotal());
    }

    /**
     * @param
     * @Title: selectDataStatistics
     * @Description: 首页数据统计
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result selectDataStatistics() {

        PcDataStatisticsVo pcDataStatisticsVo = new PcDataStatisticsVo();

        //计算个人以及企业的人数
        countUserSumAndCompanyUserSum(pcDataStatisticsVo);
        //计算设备总数
        computeDeviceTotal(pcDataStatisticsVo);
        //计算总项目数量
        computeProjectTotal(pcDataStatisticsVo);

        //设备近一年的增长


        //查询12个月之内的数据
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int oldMonth = 0;
        int oldYear = 0;
        if (month < 12) {
            oldMonth = month + 1;
            oldYear = year - 1;
        }
        List<PcUserStatistics> pcUserStatisticsList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            // 1：平台，2：企业 ，5：个人
            Integer userType = null;
            String userName = null;
            //企业用户
            if (i == 0) {
                // 1：平台，2：企业 ，5：个人
                userType = 2;
                userName = "企业用户";
            } else {
                // 1：平台，2：企业 ，5：个人
                userType = 5;
                userName = "个人用户";
            }
            PcUserStatistics pcUserStatistics = new PcUserStatistics();

            PcDataVo pcDataVo = PcDataVo.builder()
                    .year(year)
                    .month(1)
                    .type(userType)
                    .build();
            if(oldYear > 0 && oldMonth > 0) {
                pcDataVo.setOldMonth(oldMonth);
                pcDataVo.setOldYear(oldYear);
            }

            //个人用户近一年的增长
            List<Map<String, Object>> userTotalList = userStatisticsService.getUserTotalList(pcDataVo);
            pcUserStatistics.setName(userName);
            pcUserStatistics.setType(userType);
            List<Map<String, Object>> mapList = DateUtil.toTimeData(null, null, userTotalList, oldYear, oldMonth, year, month);
            pcUserStatistics.setPcDeviceStatisticsVoList(mapList);
            pcUserStatisticsList.add(pcUserStatistics);
        }
        pcDataStatisticsVo.setPcUserStatisticsList(pcUserStatisticsList);

        //获取设备报修统计
        List<StatisticsVo> afterSaleStatistics = aftersaleFeign.getAfterSaleStatistics(null);
        if (afterSaleStatistics != null) {
            for (StatisticsVo statisticsVo : afterSaleStatistics) {
                Integer status = statisticsVo.getStatus();
                String name = null;
                if (status.equals(1)) {
                    name = "报修";
                } else if (status.equals(2)) {
                    name = "受理";
                } else {
                    name = "维修";
                }
                statisticsVo.setName(name);
            }

        }
        pcDataStatisticsVo.setStatisticsVoList(afterSaleStatistics);


        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(pcDataStatisticsVo);
        return defaultTrue;
    }

    /**
     * @param projectId
     * @Title: selectPcDataAll
     * @Description: 根据项目统计企业设备的数据
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result selectPcDataAll(Integer projectId, Long startTime, Integer type) {
        Result result = Result.getDefaultFalse();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);//获取年份
        log.info("年：{}", year);
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        log.info("当前月：{}", month);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        log.info("当前天：{}", day);

        //开始时间
        Calendar calStartDataUtil = Calendar.getInstance();

        //结束时间
//        Calendar calEndDataUtil = Calendar.getInstance();

        calStartDataUtil.setTimeInMillis(startTime);
//        calEndDataUtil.setTimeInMillis(endTime);

        //开始时间的年
        int startYear = calStartDataUtil.get(Calendar.YEAR);
//        int endYear = calEndDataUtil.get(Calendar.YEAR);
        //月
        int startMonth = calStartDataUtil.get(Calendar.MONTH) + 1;
//        int endMonth = calEndDataUtil.get(Calendar.MONTH)+1;
        //日
        int startDay = calStartDataUtil.get(Calendar.DATE);
//        int endMoDay = calEndDataUtil.get(Calendar.DATE);


        //根据项目获取开关主键
//        List<SerialNumberMasterVo> list = selectDataAllByNumber(projectId, startYear, startMonth, startDay, year, month,
//                day, type);
        List<SerialNumberMasterVo> list = dataUtil.getMasterIndexByProjectId(projectId, startYear, null);


        List<Map<String, Object>> mapList = null;
        Map<String, Object> objectMapDay = null;

        Map<String, Object> objectMapMonth = null;


        if(!CollectionUtils.isEmpty(list)) {
            //获取所有的设备号
//            List<Long> snList = list.stream().map(SerialNumberMasterVo::getMasterSn).collect(Collectors.toList());
            //查询当月的天
//            objectMapDay = pcMeterYearsMapper.queryNowPcMeterByMonth(list, year, month, day, projectId);

            //查询当年的月
//            objectMapMonth = pcMeterYearsMapper.queryNowPcMeterByMasterIndex(list, year,month, projectId);


            //年
            if (type == 0) {
                mapList = pcMeterYearsMapper.queryPcMeterByMasterIndex(list, startYear,projectId);
//                if (year == startYear) {
//                    //当年的数据
//                    Object meterM = null;
//                    //获取月
//                    if(objectMapMonth != null) {
//                        meterM = objectMapMonth.get("value");
//                    }
//                    Object meterD = null;
//
//                    if(objectMapDay != null) {
//                        meterD = objectMapDay.get("value");
//                    }
//                    if(objectMapMonth == null) {
//                        objectMapMonth = new HashMap<>();
//
//                    }
//                    BigDecimal meterMonth= BigDecimal.ZERO;
//                    BigDecimal meterDay= BigDecimal.ZERO;
//                    if(meterD != null && !meterD.equals(0) ) {
//                        meterDay = (BigDecimal) meterD;
//                    }
//                    if(meterM != null && !meterM.equals(0)) {
//                        meterMonth = (BigDecimal) meterM;
//                    }
//                    BigDecimal bigDecimal = meterMonth.add(meterDay);
//                    objectMapMonth.put("value", bigDecimal);
//                    objectMapMonth.put("name", month);
//                    mapList.add(objectMapMonth);
//                }
                //月
            } else if (type == 1) {
                mapList = pcMeterYearsMapper.queryPcMeterByMonth(list, startYear, startMonth, projectId);
//                if (year == startYear && month == startMonth && objectMapDay != null) {
//                    //当月的数据不包括当天的数据
//                    mapList.add(objectMapDay);
//                }
                //天
            } else if (type == 2) {

                //当天的数据
                mapList = pcMeterYearsMapper.queryNowPcMeterByDay(list, startYear, startMonth, startDay, projectId);

            }

        }
        List<Map<String, Object>> meterList = DataUtil.toDataHour(startTime, type , mapList,null);
        Map<String, Object> mapAll = new HashMap<>();
        mapAll.put("meterList", meterList);

        BigDecimal monthAveMeter = BigDecimal.ZERO;
        BigDecimal dayAveMeter = BigDecimal.ZERO;
        BigDecimal maxMonthMeter = BigDecimal.ZERO;
        BigDecimal maxDayMeter = BigDecimal.ZERO;
        //项目的总用电量
        //根据项目id获取所有的开关编号
        List<SerialNumberMasterVo> indexAllByProjectId = dataUtil.getIndexAllByProjectId(projectId);
        if(!CollectionUtils.isEmpty(indexAllByProjectId)) {
            //获取年的数据
            BigDecimal meterByProjectId = pcMeterYearsMapper.getMeterByProjectId(indexAllByProjectId, projectId);

            if(meterByProjectId == null ) {
                meterByProjectId = BigDecimal.ZERO;
            }

//            //获取月的数据
//            BigDecimal monthMeterByProjectId = pcMeterMonthsMapper.getMonthMeterByProjectId(indexAllByProjectId,
//                    year, month, projectId);
//            if(monthMeterByProjectId == null) {
//                monthMeterByProjectId = BigDecimal.ZERO;
//            }
//            //不包括当天的电量
//            BigDecimal decimal = monthMeterByProjectId.add(meterByProjectId);
//
//            //获取日的数据
//            BigDecimal dayMeterByProjectId = pcMeterHoursMapper.getDayMeterByProjectId(indexAllByProjectId,
//                    year, month, day, projectId);
//
//            if(dayMeterByProjectId == null) {
//                dayMeterByProjectId = BigDecimal.ZERO;
//            }
//
//            mapAll.put("sumMeter", dayMeterByProjectId.add(monthMeterByProjectId).add(meterByProjectId).
//                    setScale(1,BigDecimal.ROUND_HALF_UP));
            mapAll.put("sumMeter", meterByProjectId.setScale(1,BigDecimal.ROUND_HALF_UP));
            //获取项目创建时间
            Long date = userFeign.getProjectDateByProjectId(projectId);
            long dateTime = System.currentTimeMillis();
            if(date == null) {
                log.info("根据项目统计企业设备的数据中，获取项目创建时间为空！！");
                date = dateTime;
            }
            Map<String, Object> map = DataUtil.toMonthandDay(date, dateTime);


            int m= (int) map.get("month");
            int d= (int) map.get("day");

            monthAveMeter = meterByProjectId.divide(new BigDecimal(m), 1, BigDecimal.ROUND_HALF_UP);
            //月平均用电(averageMeter)

            dayAveMeter = meterByProjectId.divide(new BigDecimal(d), 1, BigDecimal.ROUND_HALF_UP);

            BigDecimal maxMonthMeterByProjectId = pcMeterMonthsMapper.getMaxMonthMeterByProjectId(indexAllByProjectId, projectId,year);
            if(maxMonthMeterByProjectId == null) {
                maxMonthMeterByProjectId = BigDecimal.ZERO;
            }
            //获取当月的电量
            maxMonthMeter = maxMonthMeterByProjectId.setScale(1,BigDecimal.ROUND_HALF_UP);
            maxDayMeter = pcMeterMonthsMapper.getMaxDayMeterByProjectId(indexAllByProjectId, projectId,year)
                    .setScale(1,BigDecimal.ROUND_HALF_UP);
        }

        mapAll.put("monthAveMeter", monthAveMeter);
        //日平均用电(dayMeter)
        mapAll.put("dayAveMeter", dayAveMeter);

        //最高月用电量(maxMonthMeter)
        mapAll.put("maxMonthMeter", maxMonthMeter);
        //最高日用电量(maxDayMeter)
        mapAll.put("maxDayMeter", maxDayMeter);

        result = Result.getDefaultTrue();
        result.setData(mapAll);
        return result;
    }


        /**
         *
         * @Title: selectPcMonthMeter
         * @Description: 统计近12月的用电峰谷
         * @param projectId
         * @return: cn.meiot.entity.vo.Result
         */
        @Override
        public Result selectPcMonthMeter (Integer projectId){

            //将数据封装到马匹集合中
            Map<String, Object> map = new HashMap<String, Object>();
            AppMeterVo appMeterVo = new AppMeterVo();
            appMeterVo.setTableName(TableConfig.METER);

            cal.setTime(new Date());
            int year = cal.get(Calendar.YEAR);//获取年份
            int realMonth = cal.get(Calendar.MONTH) + 1; //获取当月

            int monthTemporary = 0;
            int yearTemporary = 0;
            int num = 0;
            num = 12;
            //查询12个月之内的数据
            int month = cal.get(Calendar.MONTH);
            if(month >= 11) {
                month = month - 10;
                yearTemporary = year;
                monthTemporary = month;
            } else {
                int oldMonth = month + 2;
                int oldYear = year - 1;
                month = 1;
                appMeterVo.setOldMonth(oldMonth);
                appMeterVo.setOldYear(oldYear);
                map.put("oldMonth", oldMonth);
                map.put("oldYear", oldYear);
                yearTemporary = oldYear;
                monthTemporary = oldMonth;
            }
            appMeterVo.setMonth(month);
            appMeterVo.setYear(year);
            map.put("month", month);
            map.put("year", year);
            map.put("projectId", projectId);
            appMeterVo.setProjectId(projectId);
            //根据项目id获取所有的开关编号
            List<SerialNumberMasterVo> indexAllByProjectId = dataUtil.getIndexAllByProjectId(projectId);


            List<Map<String, Object>> diagramList = null;
            //当月的电量
            //当月的电量
            BigDecimal sum = BigDecimal.ZERO;

            //通过条件查询统计的数据    最大电量 最小电量   等等
            PcMeterBo batteryLeftBo = new PcMeterBo();
            if(!CollectionUtils.isEmpty(indexAllByProjectId)) {


                BigDecimal maxMeterT = BigDecimal.ZERO;
                BigDecimal minMeterT = BigDecimal.ZERO;
                BigDecimal avgMeterT = BigDecimal.ZERO;
                BigDecimal sumMeterT = BigDecimal.ZERO;



                //获取12个月的最高，最低，平均电量
                BatteryLeftBo batteryLeftBoYear = new BatteryLeftBo();
                //总电量 type = 0
                map.put("type", 0);
                BigDecimal data = pcMeterYearsMapper.getPcBatteryLeft(indexAllByProjectId, map);
                if(data == null) {
                    data = BigDecimal.ZERO;
                }
                batteryLeftBoYear.setSumMeter(data);
                //最大值 type = 1
                map.put("type", 1);
                data = pcMeterYearsMapper.getPcBatteryLeft(indexAllByProjectId, map);
                if(data == null) {
                    data = BigDecimal.ZERO;
                }
                batteryLeftBoYear.setMaxMeter(data);
                //最小值 type = 2
                map.put("type", 2);
                data = pcMeterYearsMapper.getPcBatteryLeft(indexAllByProjectId, map);
                if(data == null) {
                    data = BigDecimal.ZERO;
                }
                batteryLeftBoYear.setMinMeter(data);



                Map<String, Object> mapT = map;
                mapT.put("oldMonth",null);
                mapT.put("oldYear",null);
                mapT.put("month", realMonth);
                mapT.put("year", year);
                //获取当月的电量
//                BatteryLeftBo batteryLeftBoMonth= pcMeterMonthsMapper.getPcBatteryLeftMonth(indexAllByProjectId,mapT);

                BigDecimal MeterT = new BigDecimal("0.00");
                BigDecimal maxMeterYear = MeterT;
                BigDecimal minMeterYear = MeterT;
                BigDecimal sumMeterYear = MeterT;


                //判断年的数据
                if(batteryLeftBoYear != null) {
                    maxMeterYear = batteryLeftBoYear.getMaxMeter();
                    minMeterYear = batteryLeftBoYear.getMinMeter();
                    sumMeterYear = batteryLeftBoYear.getSumMeter();
                }

                batteryLeftBo.setMaxMeter(maxMeterYear);
                batteryLeftBo.setMinMeter(minMeterYear);
                batteryLeftBo.setSumMeter(sumMeterYear);


                //判断平均值电量

                //获取项目创建时间
                Long date = userFeign.getProjectDateByProjectId(projectId);

                long dateTime = System.currentTimeMillis();
                if(date == null) {
                    log.info("统计近12月的用电峰谷，获取项目创建时间为空！！");
                    date = dateTime;
                }
                Map<String, Object> mapDate = DataUtil.toMonthandDay(date, dateTime);

                int mCreate= (int) mapDate.get("month");
                //创建时间小于12个月则取小的，大于取12
                if(mCreate > 12) {
                    mCreate = 12;
                }

                BigDecimal avgMeter = batteryLeftBo.getSumMeter().divide(new BigDecimal(mCreate), 3, BigDecimal.ROUND_HALF_UP);
                batteryLeftBo.setAvgMeter(avgMeter);

                //根据条件获取统计图的数据
                diagramList = pcMeterYearsMapper.getYearStatisticalList(indexAllByProjectId,appMeterVo);
            }

            List<Map<String, Object>> listArrayTo = DataUtil.toData(yearTemporary, monthTemporary,
                    num, year, diagramList,null);

            map = new HashMap<String, Object>();
            map.put("meterStatistics",batteryLeftBo);
            map.put("diagramList",listArrayTo);
            Result result = Result.getDefaultTrue();
            result.setData(map);
            return result;
        }


        /**
         *
         * @Title: selectPcEnergy
         * @Description: 统计项目企业设备的能效
         * @param projectId
         * @return: cn.meiot.entity.vo.Result
         */
        @Override
        public Result selectPcEnergy (Integer projectId){

            Map<String, Object> map = new HashMap<>();
            //月电量占全年用电量比重（每个月的用电量/全年用电量的占比）
            cal.setTime(new Date());
            int year = cal.get(Calendar.YEAR);//获取年份
            int month = cal.get(Calendar.MONTH) + 1;//获取月份
            int day = cal.get(Calendar.DAY_OF_MONTH);

            //根据项目获取开关主键
            List<SerialNumberMasterVo> list = selectDataAllByNumber(projectId, year, 1, 1, year, month,
                    day, 0);
            //查询当年的每月的用电量

            BigDecimal meterMonth= BigDecimal.ZERO;

            List<Map<String, Object>> mapList = null;
            if(!CollectionUtils.isEmpty(list)) {
                //获取每个月的用电量
                mapList = pcMeterYearsMapper.queryPcMeterByMasterIndex(list, year, projectId);

//                //查询当年的月
//                Map<String, Object> mapMonth = pcMeterYearsMapper.queryNowPcMeterByMasterIndex(list, year, month, projectId);
//
//
//                //获取的月为null
//                if(mapMonth == null) {
//                    mapMonth = new HashMap<>();
//                    mapMonth.put("value", meterMonth);
//                    mapMonth.put("name", month);
//                }
//                mapList.add(mapMonth);
            }
            List<Map<String, Object>> meterList = DataUtil.toDataHour(System.currentTimeMillis(), 0 , mapList,null);
            map.put("monthMeterList", meterList);


        //用电分类比重
            List<SwitchTypeVo> switchTypeVos = deviceFeign.querySwitch(projectId);
//        List<SwitchTypeVo> switchTypeVos = new ArrayList<>();
//        SwitchTypeVo switchTypeVo7 = new SwitchTypeVo();
//        switchTypeVo7.setName("空调");
//        switchTypeVo7.setId(1L);
//        switchTypeVo7.setMeter(BigDecimal.valueOf(39006));
//
//        SwitchVo switchVo = new SwitchVo();
//        switchVo.setSwtichIndex(7);
//        switchVo.setSwtichSn("1909200003");
//
//        SwitchVo switchVo2 = new SwitchVo();
//        switchVo2.setSwtichIndex(1);
//        switchVo2.setSwtichSn("1909200063");
//
//
//        SwitchVo switchVo3 = new SwitchVo();
//        switchVo3.setSwtichIndex(2);
//        switchVo3.setSwtichSn("1909200053");
//        List<SwitchVo> switchVoList = new ArrayList<>();
//
//        switchVoList.add(switchVo);
//        switchVoList.add(switchVo2);
//        switchVoList.add(switchVo3);
//        switchTypeVo7.setListSwitchVo(switchVoList);
//        switchTypeVos.add(switchTypeVo7);
//
//
//        SwitchTypeVo switchTypeVo6 = new SwitchTypeVo();
//        switchTypeVo6.setName("照明");
//        switchTypeVo6.setId(2L);
//        switchTypeVo6.setMeter(BigDecimal.valueOf(9032));
//
//        switchVo = new SwitchVo();
//        switchVo.setSwtichIndex(7);
//        switchVo.setSwtichSn("1909200003");
//
//        switchVo2 = new SwitchVo();
//        switchVo2.setSwtichIndex(1);
//        switchVo2.setSwtichSn("1909200063");
//
//
//        switchVo3 = new SwitchVo();
//        switchVo3.setSwtichIndex(2);
//        switchVo3.setSwtichSn("1909200053");
//        switchVoList = new ArrayList<>();
//
//        switchVoList.add(switchVo);
//        switchVoList.add(switchVo2);
//        switchVoList.add(switchVo3);
//        switchTypeVo6.setListSwitchVo(switchVoList);
//        switchTypeVos.add(switchTypeVo6);
//
//        SwitchTypeVo switchTypeVo5 = new SwitchTypeVo();
//        switchTypeVo5.setName("生产设备");
//        switchTypeVo5.setId(3L);
//        switchTypeVo5.setMeter(BigDecimal.valueOf(77046));
//
//        switchVo = new SwitchVo();
//        switchVo.setSwtichIndex(7);
//        switchVo.setSwtichSn("1909200003");
//
//        switchVo2 = new SwitchVo();
//        switchVo2.setSwtichIndex(1);
//        switchVo2.setSwtichSn("1909200063");
//
//
//        switchVo3 = new SwitchVo();
//        switchVo3.setSwtichIndex(2);
//        switchVo3.setSwtichSn("1909200053");
//        switchVoList = new ArrayList<>();
//
//        switchVoList.add(switchVo);
//        switchVoList.add(switchVo2);
//        switchVoList.add(switchVo3);
//        switchTypeVo5.setListSwitchVo(switchVoList);
//        switchTypeVos.add(switchTypeVo5);



            if(switchTypeVos == null) {
                map.put("switchTypeList", switchTypeVos);
                Result result = Result.getDefaultTrue();
                result.setData(map);
                return result;
            }
            for (SwitchTypeVo switchTypeVo:switchTypeVos) {
                //获取当前类型的
                List<SwitchVo> listSwitchVo = switchTypeVo.getListSwitchVo();
                BigDecimal pcListSwitch = BigDecimal.ZERO;
                if(!CollectionUtils.isEmpty(listSwitchVo)) {
                    //获取当前年的数据 (根据项目id,开关唯一标识)
                    pcListSwitch = pcMeterYearsMapper.getPcListSwitch(listSwitchVo, projectId, year);
//                    //获取当前月的数据
//                    BigDecimal pcListSwitchMonth = pcMeterMonthsMapper.getPcListSwitchMonth(listSwitchVo, projectId, year, month);
//                    if(pcListSwitchMonth == null) {
//                        pcListSwitchMonth = BigDecimal.ZERO;
//                    }
                    if(pcListSwitch == null) {
                        pcListSwitch = BigDecimal.ZERO;
                    }
//                    pcListSwitch = pcListSwitch.add(pcListSwitchMonth);

                }
                switchTypeVo.setMeter(pcListSwitch.setScale(1, BigDecimal.ROUND_HALF_UP));
            }
            map.put("switchTypeList", switchTypeVos);
            Result result = Result.getDefaultTrue();
            result.setData(map);
            return result;
        }

    /**
         *
         * @Title: selectDataAll
         * @Description: 根据项目id统计企业设备的数据
         * @param projectId
         * @param type 年 月 日 （0 1 2）
         * @return: java.util.List<java.lang.String>
         */
    @Override
    public List<SerialNumberMasterVo> selectDataAllByNumber (Integer projectId,
                                                                int startYear,
                                                                int startMonth,
                                                                int startDay,
                                                                int year,
                                                                int month,
                                                                int day,
                                                                Integer type){
        List<String> stringList = null;
        //如果是当年的数据
        if (type == 0) {
            if (year == startYear) {
                //年的数据
                stringList = pcMeterYearsMapper.queryNumberAll(year, month, day, projectId);
            } else {
                startMonth = 0;
                startDay = 0;
                stringList = pcMeterYearsMapper.queryNumberAll(startYear, startMonth, startDay, projectId);
            }
            //月
        } else if (type == 1) {
            if (year == startYear && month == startMonth) {
                //当月的数据
                stringList = pcMeterYearsMapper.queryNumberByNowMonth(startYear, startMonth, day, projectId, type);
            } else {
                stringList = pcMeterYearsMapper.queryNumberByMonth(startYear, startMonth, projectId);
            }
            //日
        } else if (type == 2) {
            if (year == startYear && month == startMonth && day == startDay) {
                //当天的数据
                stringList = pcMeterYearsMapper.queryNumberByNowMonth(startYear, startMonth, startDay, projectId, type);
            } else {
                stringList = pcMeterYearsMapper.queryNumberByDay(startYear, startMonth, startDay, projectId);
            }
        }

        List<SerialNumberMasterVo> list = null;
        if (stringList != null && stringList.size() > 0) {
            //通过设备号查询主开关编号
            list = deviceFeign.queryMasterIndexBySerialNUmber(stringList);
            if (null == list || list.size() == 0) {
                log.info("通过根据项目id获取主开关查询的结果为空");
                return null;
            }
            log.info("根据项目id获取主开关查询成功");
        }
        return list;
    }
}

