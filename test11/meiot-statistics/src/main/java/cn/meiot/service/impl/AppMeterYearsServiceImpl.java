package cn.meiot.service.impl;

import cn.meiot.config.TableConfig;
import cn.meiot.entity.AppMeterYears;
import cn.meiot.entity.bo.BatteryLeftBo;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.mapper.AppMeterHoursMapper;
import cn.meiot.mapper.AppMeterMonthsMapper;
import cn.meiot.mapper.AppMeterYearsMapper;
import cn.meiot.service.IAppMeterYearsService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.DataUtil;
import cn.meiot.utils.DateUtil;
import cn.meiot.utils.MeterUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-19
 */
@Service
@Slf4j
public class AppMeterYearsServiceImpl extends ServiceImpl<AppMeterYearsMapper, AppMeterYears> implements IAppMeterYearsService {

    @Autowired
    private AppMeterYearsMapper appMeterYearsMapper;

    @Autowired
    private AppMeterMonthsMapper appMeterMonthsMapper;

    @Autowired
    private AppMeterHoursMapper appMeterHoursMapper;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private DataUtil dataUtil;

    @Autowired
    private UserFeign userFeign;

    private Calendar cal = Calendar.getInstance();


    /**
     *
     * @Title: getNowMonth
     * @Description: 获取app当月的数据
     * @param appMeterVo
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result getNowMonth(AppMeterVo appMeterVo) {
        BigDecimal value = BigDecimal.ZERO;
        BigDecimal monthStatistical = appMeterYearsMapper.getMonthStatistical(appMeterVo);
        if(monthStatistical != null) {
            value = monthStatistical;
        }
        log.info("getNowMonth获取app当月的数据：{}", value);
        Result result = Result.getDefaultTrue();
        result.setData(value.setScale(1, BigDecimal.ROUND_HALF_UP));
        log.info("result获取app当月的数据：{}", result);
        return result;
    }

    @Override
    public Result getList(AppMeterVo appMeterVo) {
        log.info("查询的年份：{}", appMeterVo.getYear());
        List<Map<String, Object>> listArray = new ArrayList<>();

        for(int i = 1; i < 13; i++) {
            Map<String, Object> mapL = new HashMap<>();
            mapL.put("sMonth", i);
            mapL.put("meter", new BigDecimal("0"));
            listArray.add(mapL);
        }
        appMeterVo.setTableName(TableConfig.METER);
        appMeterVo.setProjectId(0);

        //获取某年下所有月份的数据
        List<Map<String, Object>> list = appMeterYearsMapper.getYearStatisticalList(appMeterVo);
        for(Map<String, Object> m : listArray) {
            Object sMonth = m.get("sMonth");
            for(Map<String, Object> mp1: list) {
                Object sMonth1 = mp1.get("sMonth");
                Object meter1 = mp1.get("meter");
                if(sMonth.equals(sMonth1)) {
                    m.put("meter",meter1);
                }
            }
        }
//        //判断查询的年份是否等于当前年份
//        cal.setTime(new Date());
//        int year = cal.get(Calendar.YEAR);//获取年份
//        if (year == appMeterVo.getYear()) {
//            log.info("需要查询当前月的数据");
//            //查询当前月的数据统计
//            int month = cal.get(Calendar.MONTH) + 1;//获取月份
//            appMeterVo.setMonth(month);
//            BigDecimal monthMeter = appMeterMonthsMapper.getSumMeterByMonth(appMeterVo);
//            log.info("{}月电量使用：{},不包含当天", month, monthMeter);
//            //获取当天的用电量
//            int day = cal.get(Calendar.DAY_OF_MONTH);//获取月份
//            appMeterVo.setDay(day);
//            BigDecimal dayMeter = appMeterHoursMapper.getNowDayByCondition(appMeterVo);
//            log.info("当月{}日电量使用：{}", day, dayMeter);
//            if (null == monthMeter) {
//                monthMeter = BigDecimal.ZERO;
//            }
//            if (null == dayMeter) {
//                dayMeter = BigDecimal.ZERO;
//            }
//            BigDecimal meter = monthMeter.add(dayMeter);
//            log.info("当月电量使用：{}", meter);
////            Map<String, Object> map = new HashMap<String, Object>();
////            map.put("sMonth", month);
////            map.put("meter", meter);
////            listArray.add(map);
//            for(Map<String, Object> objectMap : listArray) {
//                Object month1 = objectMap.get("sMonth");
//                if(month1.equals(month)) {
//                    objectMap.put("meter",meter);
//                }
//            }
//
//        }
        BigDecimal totalMeter =  MeterUtil.addMeter(listArray);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("totalMeter",totalMeter);
        map.put("list",listArray);
        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }

    
    /**
     * 
     * @Title: getListApp  
     * @Description: app的数据报告
     * @param parametersDto
     * @param appMeterVo    
     * @return: cn.meiot.entity.vo.Result     
     */
    @Override
    public Result getListApp(ParametersDto parametersDto,AppMeterVo appMeterVo) {
        log.info("查询的type：{},查询的年份：{}", parametersDto.getType(),appMeterVo.getYear());
        
        //获取某年下所有月份的数据
        List<Map<String, Object>> list = appMeterHoursMapper.getMonthlyMeterApp(parametersDto, appMeterVo);

        Calendar calStartDataUtil = Calendar.getInstance();
        calStartDataUtil.set(Calendar.YEAR, appMeterVo.getYear());
        if(appMeterVo.getMonth() != null) {
            calStartDataUtil.set(Calendar.MONTH, appMeterVo.getMonth()-1);
        }
        long timeInMillis = calStartDataUtil.getTimeInMillis();
        List<Map<String, Object>> mapList = DataUtil.toDataHourApp(timeInMillis, parametersDto.getType(), list, null);

        Map<String, Object> map = MeterUtil.meterMaxMin(list,0);
        map.put("type",parametersDto.getType());
        map.put("list",mapList);
        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }



    @SuppressWarnings("Duplicates")
    @Override
    @Transactional
    public Result monthStatistics(Integer year, Integer month) {
        List<AppMeterYears> list = null;
        try {
            //判断查询时是否时当前月份
            //cal.setTime(new Date());
            //int year = cal.get(Calendar.YEAR);//获取年份
            log.info("年：{}", year);
            //int month = cal.get(Calendar.MONTH);//获取月份
            log.info("当前月：{}", month);
            AppMeterVo appMeterVo = AppMeterVo.builder().year(year).month(month).build();

            //通过设备序列号查询列表
            List<AppMeterMonthsVo> appMeterYears = appMeterMonthsMapper.selectListBySerialNumber(appMeterVo);
            if(null == appMeterYears){
                log.info("app上个月没有设备用电");
                return  Result.getDefaultTrue();
            }

            ParametersDto parametersDto = ParametersDto.builder()
                    .tableName(TableConfig.METER)
                    .platform(TableConfig.APP)
                    .type(0)
                    .sYear(year)
                    .sMonth(month)
                    .build();
            list =  commonUtil.appMeterYearsVoToAppMeterMonths(parametersDto, appMeterYears);

            this.saveBatch(list);
            log.info("app统计月度成功！");
            return Result.getDefaultTrue();
        } catch (Exception e) {
            log.error("app统计月度失败！:年份{},月份{},数据{}",year,month, list);
            log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
            return Result.getDefaultFalse();
        }


    }

    /**
     *
     * @Title: batteryLeft
     * @Description: 企业版app6个月的电量数据
     * @param serialNumber
     * @param rtuserId
     * @param type
     * @param projectId
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result batteryLeft(String serialNumber, Long rtuserId, Integer type,Integer projectId) {
        //将数据封装到马匹集合中
        Map<String, Object> map = new HashMap<String, Object>();
        AppMeterVo appMeterVo = AppMeterVo.builder().serialNumber(serialNumber).userId(rtuserId).build();
        appMeterVo.setTableName(TableConfig.METER);
        appMeterVo.setProjectId(projectId);
        //获取主开关编号sn
        Long masterSn = commonUtil.getMasterSn(serialNumber);

        if(null == masterSn){
            log.info("未获取到主开关编号");
            Result result = Result.getDefaultFalse();
            result.setMsg("未获取到主开关编号");
            return result;
        }
        appMeterVo.setSwitchSn(masterSn);
        log.info("主开关编号sn：{}",masterSn);
        map.put("serialNumber", serialNumber);
        map.put("rtuserId", rtuserId);
        map.put("switchSn",masterSn);
        map.put("type", type);
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);//获取年份
        int monthTemporary = 0;
        int yearTemporary = 0;
        int num = 0;

        //查询6个月之内的数据
        if (1 == type) {
            num = 6;
            //查询6个月之内的数据
            int month = cal.get(Calendar.MONTH);
            if(month >= 5) {
                month = month - 4;
                yearTemporary = year;
                monthTemporary = month;
            } else {
                int oldMonth = month + 8;
                int oldYear = year - 1;
                month = 1;
                map.put("oldMonth", oldMonth);
                map.put("oldYear", oldYear);
                appMeterVo.setOldMonth(oldMonth);
                appMeterVo.setOldYear(oldYear);
                yearTemporary = oldYear;
                monthTemporary = oldMonth;
            }
            map.put("month", month);
            appMeterVo.setMonth(month);
        }
//        if(type == 2) {
//            num = 12;
//            //查询12个月之内的数据
//            int month = cal.get(Calendar.MONTH);
//            if(month >= 11) {
//                month = month - 10;
//                yearTemporary = year;
//                monthTemporary = month;
//            } else {
//                int oldMonth = month + 2;
//                int oldYear = year - 1;
//                month = 1;
//                map.put("oldMonth", oldMonth);
//                map.put("oldYear", oldYear);
//                appMeterVo.setOldMonth(oldMonth);
//                appMeterVo.setOldYear(oldYear);
//                yearTemporary = oldYear;
//                monthTemporary = oldMonth;
//            }
//            map.put("month", month);
//            appMeterVo.setMonth(month);
//        }
        appMeterVo.setYear(year);
        map.put("year",year);
        log.info("查询条件：{}",map);
        //根据条件获取统计图的数据
        List<Map<String, Object>> diagramList = appMeterYearsMapper.getYearStatisticalList(appMeterVo);

        Map<String, Object> mapList = MeterUtil.meterMaxMin(diagramList,1);


        List<Map<String, Object>> listArrayTo = DataUtil.toData1(yearTemporary, monthTemporary, num, year, diagramList);

        mapList.put("diagramList",listArrayTo);
        Result result = Result.getDefaultTrue();
        result.setData(mapList);
        return result;
    }

    @Override
    public Result pullAppMonthStatistics(int year, int month) {
        //判断查询时是否时当前月份
//        cal.setTime(new Date());
//        int year = cal.get(Calendar.YEAR);//获取年份
        log.info("年：{}", year);
//        int month = cal.get(Calendar.MONTH);//获取月份
        log.info("月：{}", month);
        AppMeterVo appMeterVo = AppMeterVo.builder().year(year).month(month).build();
       /* //获取昨天所有的设备号
        List<String> serialNumbers = appMeterMonthsMapper.getLastMonthSerialNumber(year,month);
        if(null == serialNumbers){
            log.info("上个月没有设备号");
            return Result.getDefaultTrue();
        }*/
        //通过设备序列号查询列表
        List<AppMeterMonthsVo> appMeterYears = appMeterMonthsMapper.selectListBySerialNumber(appMeterVo);


        if(null == appMeterYears){
            log.info("手动拉取,app上个月没有设备用电");
            return  Result.getDefaultTrue();
        }

        ParametersDto parametersDto = ParametersDto.builder()
                .tableName(TableConfig.METER)
                .platform(TableConfig.APP)
                .type(0)
                .sYear(year)
                .sMonth(month)
                .build();
        List<AppMeterYears> list = commonUtil.appMeterYearsVoToAppMeterMonths(parametersDto, appMeterYears);
        this.saveBatch(list);
        log.info("手动拉取，app统计月度成功！");
        return Result.getDefaultTrue();
    }

    /**
     *
     * @Title: getInformation
     * @Description: 运维报告基本信息
     * @param userId
     * @param projectId
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result getInformation(Long userId, Integer projectId) {
        if(projectId != null) {
//            Integer projectIdApp = null;
//            if(!projectId.equals(0)) {
//                projectIdApp = projectId;
//            }
            Map<String, Object> map = deviceFeign.queryUseTime(userId, projectId);
            log.info("设备服务获取运维报告信息：{}", map);
            if(map == null) {
//                log.info("个人app年度用电谷峰 :map1" , map);
//                Result result = Result.getDefaultFalse();
//                result.setMsg("未获取到设备的开始时间");
//                return result;
                return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(),ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
            }
            //获取设备号，主账号，获取主开关
            List<PersonalSerialVo> personalSerialVos = getPersonalSerialVos(userId, projectId);
            BigDecimal totalMeter = BigDecimal.ZERO;
            if(!CollectionUtils.isEmpty(personalSerialVos)) {
                //判断是企业，还是个人的
                ParametersDto parametersDto = new ParametersDto();
                parametersDto.setTableName(TableConfig.METER);
                parametersDto.setProjectId(projectId);
                parametersDto.setType(0);

                totalMeter = appMeterYearsMapper.getTotalMeter(personalSerialVos, parametersDto);
            }else {
                log.info("根据userId，app获取设备号，主账号，获取主开关为空，userId:{}", userId);
            }
            if(totalMeter == null) {
                totalMeter = BigDecimal.ZERO;
            }
            map.put("totalMeter", totalMeter.setScale(1, BigDecimal.ROUND_HALF_UP));
            Result result = Result.getDefaultTrue();
            result.setData(map);
            log.info("运维基本数据 map:{}",map);
            return result;
        }else {
            log.info("运维报告基本信息，app获取项目id为空，projectId:{}", projectId);
            return  Result.getDefaultTrue();
        }
    }

    /**
     *
     * @Title: getPersonalSerialVos
     * @Description: 获取设备号，主账号，获取主开关
     * @param userId
     * @param projectId
     * @return: java.util.List<cn.meiot.entity.vo.PersonalSerialVo>
     */
    public List<PersonalSerialVo> getPersonalSerialVos(Long userId, Integer projectId) {
        //获取累计电量
        //获取设备号，主账号，获取主开关
        List<PersonalSerialVo> personalSerialVos = new ArrayList<>();
        //如果是个人app
        if(projectId.equals(0)) {
            personalSerialVos = deviceFeign.querySerialAndMaster(userId);

            //如果是企业app
        }else {
            List<SerialNumberMasterVo> indexAllByProjectId = dataUtil.getIndexAllByProjectId(projectId);
            if(!CollectionUtils.isEmpty(indexAllByProjectId)) {
                for (SerialNumberMasterVo serialNumberMasterVo : indexAllByProjectId) {
                    PersonalSerialVo personalSerialVo = new PersonalSerialVo();
                    personalSerialVo.setSerial(serialNumberMasterVo.getSerialNumber());
                    personalSerialVo.setMasterSn(serialNumberMasterVo.getMasterSn());
                    //添加企业主账号
                    if(userId == null) {
                        log.info("根据账号获取企业主账号为空！！！");
                    }else {
                        personalSerialVo.setMasterId(userId);
                    }
                    personalSerialVos.add(personalSerialVo);



                }
            }else {
                log.info("根据projectId，app获取设备号，主账号，获取主开关为空，projectId:{}", projectId);
            }
        }
        return personalSerialVos;
    }



    /**
     *
     * @Title: getMonthlyMeter
     * @Description: 个人app的每月用电量
     * @param userId
     * @param projectId
     * @param startTime
     * @param type 年 月 日 （0 1 2）
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result getMonthlyMeter(Long userId, Integer projectId, Long startTime, Integer type) {
        if(projectId != null) {
            //开始时间
            Calendar calStartDataUtil = Calendar.getInstance();
            calStartDataUtil.setTimeInMillis(startTime);
            //开始时间的年
            int startYear = calStartDataUtil.get(Calendar.YEAR);
            //月
            int startMonth = calStartDataUtil.get(Calendar.MONTH) + 1;
            Map<String, Object> map = new HashMap<>();
            //获取设备号，主账号，获取主开关
            List<PersonalSerialVo> personalSerialVos = getPersonalSerialVos(userId, projectId);
            BigDecimal totalMeter = BigDecimal.ZERO;
            BigDecimal maxDayMeter = BigDecimal.ZERO;
            BigDecimal monthAveMeter = BigDecimal.ZERO;
            BigDecimal dayAveMeter = BigDecimal.ZERO;
            BigDecimal maxMonthMeter = BigDecimal.ZERO;
            BigDecimal minMonthMeter = null;

            List<Map<String, Object>> monthlyMeter = new ArrayList<>();
            if(!CollectionUtils.isEmpty(personalSerialVos)) {
                //判断是企业，还是个人的
                ParametersDto parametersDto = new ParametersDto();
                parametersDto.setTableName(TableConfig.METER);
                parametersDto.setProjectId(projectId);
                parametersDto.setSYear(startYear);
                if(type != null && type.equals(0)) {
                    //年
                    parametersDto.setType(0);
                }else if(type != null && type.equals(1)) {
                    //月
                    parametersDto.setType(1);
                    parametersDto.setSMonth(startMonth);
                }
                //电量汇总
                totalMeter = appMeterYearsMapper.getTotalMeter(personalSerialVos, parametersDto);
                if( totalMeter == null || totalMeter.compareTo(BigDecimal.ZERO) == 0) {
                    //数据空
                    log.info("电量汇总数据为空" , userId, projectId);
                    return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(),ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
                }

                //月（天）用电量：月（年表）、日（月表）
                //type = 0 根据月份分组在年表中，type = 1 根据天分组在月表中，
                monthlyMeter = appMeterYearsMapper.getMonthlyMeter(personalSerialVos, parametersDto);
                if(CollectionUtils.isEmpty(monthlyMeter)) {
                    log.info("app的每月用电量为空，userId:{}, projectId:{}" , userId, projectId);
                }

                if(CollectionUtils.isEmpty(monthlyMeter)) {
                    log.info("app年度用电谷峰为空，userId:{}, projectId:{}" , userId, projectId);
                }else {
                    //最大电量(maxMonthMeter)
                    for (Map<String,Object> objectMap:monthlyMeter) {
                        Object value = objectMap.get("value");
                        if(value == null) {
                            value = BigDecimal.ZERO;
                        }
                        if(maxMonthMeter == null){
                            maxMonthMeter  = (BigDecimal) value;
                        }else {
                            maxMonthMeter = maxMonthMeter.max((BigDecimal) value);
                        }
                        if(minMonthMeter == null){
                            minMonthMeter  = (BigDecimal) value;
                        }else {
                            minMonthMeter = minMonthMeter.min((BigDecimal) value);
                        }

                    }
                }

                //最高日用电量
                parametersDto.setType(1);
                parametersDto.setSMonth(null);
                parametersDto.setState(0);
                maxDayMeter = appMeterYearsMapper.getTotalMeter(personalSerialVos, parametersDto);
            }else {
                log.info("根据userId，app获取设备号，主账号，获取主开关为空，userId:{}", userId);
                return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(),ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
            }
            if(totalMeter == null) {
                totalMeter = BigDecimal.ZERO;
                log.info("app的每月用电量汇总为空，userId:{}, projectId:{}" , userId, projectId);
            }
            List<Map<String, Object>> meterList = DataUtil.toDataHour1(startTime, type , monthlyMeter,1);
            map.put("meterList", meterList);
            map.put("totalMeter", totalMeter.setScale(1, BigDecimal.ROUND_HALF_UP));

            Map<String, Object> map1 = deviceFeign.queryUseTime(userId, projectId);
            if(map1 == null) {
                log.info("个人app的每月用电量 :map1" , map1);
                Result result = Result.getDefaultFalse();
                result.setMsg("未获取到设备的开始时间");
                return result;
            }



            //获取开始时间
            Long time = (Long) map1.get("startTime");
//            Long time = 1576769017000L;
            //获取结束时间
            Long endTime = (Long) map1.get("endTime");
//            Long endTime = 1582125817000L;
            //如果选择时间大于设备开始时间则为选择时间，反之为设备开始时间
            Long realTime = dataUtil.getRealTime(startTime, type);
            if(realTime > time){
                time = realTime;
            }
            Map<String, Object> map2 = DataUtil.toMonthandDay(time, endTime);


            int m= (int) map2.get("month");
            int d= (int) map2.get("day");


            //月平均用电(averageMeter)
            monthAveMeter = totalMeter.divide(new BigDecimal(m), 1, BigDecimal.ROUND_HALF_UP);
            map.put("monthAveMeter", monthAveMeter == null ? BigDecimal.ZERO : monthAveMeter);
            //日平均用电(dayAveMeter)
            dayAveMeter = totalMeter.divide(new BigDecimal(d), 1, BigDecimal.ROUND_HALF_UP);
            map.put("dayAveMeter", dayAveMeter == null ? BigDecimal.ZERO : dayAveMeter);

            //最高月用电量(maxMonthMeter)
            if(maxMonthMeter == null) {
                maxMonthMeter = BigDecimal.ZERO;
                log.info("app的用电量最高月用电量汇总，userId:{}, projectId:{}" , userId, projectId);
            }
            map.put("maxMonthMeter", maxMonthMeter.setScale(1, BigDecimal.ROUND_HALF_UP));
            //最低月用电量(minMonthMeter)
            if(minMonthMeter == null) {
                minMonthMeter = BigDecimal.ZERO;
                log.info("app的用电量最低月用电量汇总，userId:{}, projectId:{}" , userId, projectId);
            }
            map.put("minMonthMeter", minMonthMeter.setScale(1, BigDecimal.ROUND_HALF_UP));

            //最高日用电量(maxDayMeter)
            if(maxDayMeter == null) {
                maxDayMeter = BigDecimal.ZERO;
                log.info("app的用电量最高日用电量汇总，userId:{}, projectId:{}" , userId, projectId);
            }
            map.put("maxDayMeter", maxDayMeter.setScale(1, BigDecimal.ROUND_HALF_UP));
            Result result = Result.getDefaultTrue();
            result.setData(map);
            return result;
        }else {
            log.info("app的每月用电量，app获取项目id为空，projectId:{}", projectId);
            return  Result.getDefaultTrue();
        }
    }

    /**
     *
     * @Title: getPeakValleyMeter
     * @Description: 个人app年度用电谷峰
     * @param userId
     * @param projectId
     * @param startTime
     * @param type
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result getPeakValleyMeter(Long userId, Integer projectId, Long startTime, Integer type) {
        if(projectId != null) {
//            Integer projectIdApp = null;
//            if(!projectId.equals(0)) {
//                projectIdApp = projectId;
//            }
            Map<String, Object> map1 = deviceFeign.queryUseTime(userId, projectId);
            if(map1 == null) {
                log.info("个人app年度用电谷峰 :map1" , map1);
                return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(),ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
            }
            //获取开始时间
            Long time = (Long) map1.get("startTime");
//            Long time = 1576769017000L;
            //获取结束时间
            Long endTime = (Long) map1.get("endTime");
//            Long endTime = 1582125817000L;
            Long realTime = dataUtil.getRealTime(startTime, type);
            //如果选择时间大于设备开始时间则为选择时间，反之为设备开始时间
            if(realTime > time){
                time = startTime;
            }
            Map<String, Object> map2 = DataUtil.toMonthandDay(time, endTime);


            int m= (int) map2.get("month");
            int d= (int) map2.get("day");



            //开始时间
            Calendar calStartDataUtil = Calendar.getInstance();
            calStartDataUtil.setTimeInMillis(startTime);
            //开始时间的年
            int startYear = calStartDataUtil.get(Calendar.YEAR);
            //月
            int startMonth = calStartDataUtil.get(Calendar.MONTH) + 1;
            Map<String, Object> map = new HashMap<>();
            //获取设备号，主账号，获取主开关
            List<PersonalSerialVo> personalSerialVos = getPersonalSerialVos(userId, projectId);
            BigDecimal totalMeter = BigDecimal.ZERO;
            List<Map<String, Object>> monthlyMeter = new ArrayList<>();
            BigDecimal maxMeter =  BigDecimal.ZERO;
            BigDecimal minMeter =  null;
            BigDecimal meter =  null;

            if(!CollectionUtils.isEmpty(personalSerialVos)) {
                //判断是企业，还是个人的
                ParametersDto parametersDto = new ParametersDto();
                parametersDto.setTableName(TableConfig.METER);
                parametersDto.setProjectId(projectId);
                parametersDto.setSYear(startYear);
                if(type != null && type.equals(0)) {
                    //年
                    parametersDto.setType(0);
                }else if(type != null && type.equals(1)) {
                    //月
                    parametersDto.setType(1);
                    parametersDto.setSMonth(startMonth);
                }
                //电量汇总
                totalMeter = appMeterYearsMapper.getTotalMeter(personalSerialVos, parametersDto);
                if( totalMeter == null || totalMeter.compareTo(BigDecimal.ZERO) == 0) {
                    //数据空
                    log.info("电量汇总数据为空" , userId, projectId);
                    return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(),ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
                }
                //月（天）用电量：月（年表）、日（月表）
                //type = 0 根据月份分组在年表中，type = 1 根据天分组在月表中，
                monthlyMeter = appMeterYearsMapper.getMonthlyMeter(personalSerialVos, parametersDto);
                if(CollectionUtils.isEmpty(monthlyMeter)) {
                    log.info("app年度用电谷峰为空，userId:{}, projectId:{}" , userId, projectId);
                }else {
                    //最大电量(maxMeter)
                    for (Map<String,Object> objectMap:monthlyMeter) {
                        Object value = objectMap.get("value");
                        if(value == null) {
                            value = BigDecimal.ZERO;
                        }
                        if(maxMeter == null){
                            maxMeter  = (BigDecimal) value;
                        }else {
                            maxMeter = maxMeter.max((BigDecimal) value);
                        }
                        if(minMeter == null){
                            minMeter  = (BigDecimal) value;
                        }else {
                            minMeter = minMeter.min((BigDecimal) value);
                        }

                    }
                }
            }else {
                log.info("根据userId，app获取设备号，主账号，获取主开关为空，userId:{}", userId);
                return Result.faild(ResultCodeEnum.STATISTICS_DATA_IS_NULL.getCode(),ResultCodeEnum.STATISTICS_DATA_IS_NULL.getMsg());
            }
            if(totalMeter == null) {
                totalMeter = BigDecimal.ZERO;
                log.info("app年度用电谷峰汇总为空，userId:{}, projectId:{}" , userId, projectId);
            }

            map.put("maxMeter", maxMeter.setScale(1, BigDecimal.ROUND_HALF_UP));
            map.put("minMeter", minMeter == null ? BigDecimal.ZERO : minMeter.setScale(1, BigDecimal.ROUND_HALF_UP));
            List<Map<String, Object>> meterList = DataUtil.toDataHour1(startTime, type , monthlyMeter,1);
            map.put("meterList", meterList);
            //月平均用电(averageMeter)
            BigDecimal monthAveMeter = totalMeter.divide(new BigDecimal(m), 1, BigDecimal.ROUND_HALF_UP);
            map.put("monthAveMeter", monthAveMeter == null ? BigDecimal.ZERO : monthAveMeter);
            //日平均用电(dayAveMeter)
            BigDecimal dayAveMeter = totalMeter.divide(new BigDecimal(d), 1, BigDecimal.ROUND_HALF_UP);
            map.put("dayAveMeter", dayAveMeter == null ? BigDecimal.ZERO : dayAveMeter);

//            map.put("totalMeter", totalMeter.setScale(1, BigDecimal.ROUND_HALF_UP));
            Result result = Result.getDefaultTrue();
            result.setData(map);
            return result;
        }else {
            log.info("app年度用电谷峰，app获取项目id为空，projectId:{}", projectId);
            return  Result.getDefaultTrue();
        }
    }
}
