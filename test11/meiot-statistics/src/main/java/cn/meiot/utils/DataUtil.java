package cn.meiot.utils;

import cn.meiot.entity.WaterStatistics;
import cn.meiot.entity.WaterStatisticsMonths;
import cn.meiot.entity.bo.MeterStatisticalBo;
import cn.meiot.entity.vo.SerialNumberMasterVo;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.service.IPcMeterMonthsService;
import cn.meiot.service.IPcMeterYearsService;
import cn.meiot.service.IWaterQueueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName: DataUtil
 * @Description: TODO 类的描述
 * @author: 符纯涛
 * @date: 2019/9/25
 */
@Component
@Slf4j
public class DataUtil {

//	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

//    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static  final int[] MONTH = {1,2,3,4,5,6,7,8,9,10,11,12};
    @Autowired
    private DeviceFeign deviceFeign;
    private static  final String[] WEK = {"","一","二","三","四","五","六","日"};

    @Autowired
    private IPcMeterYearsService pcMeterYearsService;

    @Autowired
    private IPcMeterMonthsService pcMeterMonthsService;

    private Calendar cal = Calendar.getInstance();


    public static List<Map<String, Object>> toData(int yearTemporary, int monthTemporary, int num, int year,
                                                   List<Map<String, Object>> diagramList,Integer decimal) {
        //开始时间
        if(decimal == null) {
            decimal = 1;
        }

        List<Map<String, Object>> listArrayTo = new ArrayList<>();
        for (int i = monthTemporary; i < (monthTemporary + num); i++) {
            Map<String, Object> mapL = new HashMap<>();
            int sYear = 0;
            int smonth = 0;
            sYear = yearTemporary;
            smonth = i;
            //判断当前的年是否和倒推的年相等
            if (yearTemporary != year && i > 12) {
                sYear = year;
                smonth = i - 12;
            }
            String s = null;
//            if(smonth<10) {
//                s = "0";
//            }
            mapL.put("date", sYear+"."+smonth);
            mapL.put("meter", 0);
            listArrayTo.add(mapL);
        }
        if (diagramList != null) {
            for (Map<String, Object> m : listArrayTo) {
                String date = (String) m.get("date");
                int index=date.indexOf(".");
                String before=date.substring(0,index);
                String after=date.substring(index+1);

                for (Map<String, Object> mp1 : diagramList) {
                    Object sMonth1 = mp1.get("sMonth");
                    Object meter1 = mp1.get("meter");
                    if (after.equals(sMonth1.toString())) {
                        BigDecimal value = (BigDecimal)meter1;
                        m.put("meter",value.setScale(decimal, BigDecimal.ROUND_HALF_UP));
                    }
                }
            }
        }
        return listArrayTo;
    }
    public static List<Map<String, Object>> toData1(int yearTemporary, int monthTemporary, int num, int year, List<Map<String, Object>> diagramList) {
        List<Map<String, Object>> listArrayTo = new ArrayList<>();
        for (int i = monthTemporary; i < (monthTemporary + num); i++) {
            Map<String, Object> mapL = new HashMap<>();
            int sYear = 0;
            int smonth = 0;
            sYear = yearTemporary;
            smonth = i;
            //判断当前的年是否和倒推的年相等
            if (yearTemporary != year && i > 12) {
                sYear = year;
                smonth = i - 12;
            }
            mapL.put("sYear", sYear);
            mapL.put("sMonth", smonth);
            mapL.put("meter", new BigDecimal("0.00"));
            listArrayTo.add(mapL);
        }
        if (diagramList != null) {
            for (Map<String, Object> m : listArrayTo) {
                Object sMonth = m.get("sMonth");
                for (Map<String, Object> mp1 : diagramList) {
                    Object sMonth1 = mp1.get("sMonth");
                    Object meter1 = mp1.get("meter");
                    if (sMonth.equals(sMonth1)) {
                        BigDecimal value = (BigDecimal)meter1;
                        m.put("meter",value.setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }
            }
        }
        return listArrayTo;
    }

    public static List<Map<String, Object>> toDataHourApp(Long time, Integer type,List<Map<String, Object>> listData,Integer decimal) {
        //开始时间
        Map map1 = dateMap(time, type);
        int size = (int) map1.get("size");
        String company = "";
        List<Map<String, Object>> mapList = toListMap(size, company, listData, decimal);
        return mapList;
    }


    public static List<Map<String, Object>> toDataHour(Long time, Integer type,List<Map<String, Object>> listData,Integer decimal) {
        //开始时间
        Map map1 = dateMap(time, type);
        int size = (int) map1.get("size");
        String company = String.valueOf(map1.get("company"));
        List<Map<String, Object>> mapList = toListMap(size, company, listData, decimal);
        return mapList;
    }

    public static List<Map<String, Object>> toListMap(int size, String company, List<Map<String, Object>> listData,Integer decimal) {

        if(decimal == null) {
            decimal = 1;
        }
        //新数据
        List<Map<String, Object>> newData = new  ArrayList<Map<String, Object>>();
        for (int i = 1; i <= size; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            //数据
            map.put("value", 0);
            if(!CollectionUtils.isEmpty(listData )){
                for (Map<String, Object> data : listData) {
                    //获取时间
                    Integer times = (Integer) data.get("name");
                    if(times != null && times.intValue() == i) {
                        BigDecimal value = (BigDecimal)data.get("value");
                        map.put("value",value.setScale(decimal, BigDecimal.ROUND_HALF_UP));
                        break;
                    }
                }
            }
            map.put("name", i+company);
            newData.add(map);
        }
        return newData;


    }



    public static Map dateMap(Long time, Integer type) {

        Map<String, Object> map = new HashMap<>();
        //int startYear = calStartDataUtil.get(Calendar.YEAR);
        String company = "";
        int size = 0;
        if(type == 0) {
            //一年多少月
            size = 12;
            company = "月";
        }else if(type == 1) {
            Calendar calStartDataUtil = Calendar.getInstance();
            calStartDataUtil.setTimeInMillis(time);
            calStartDataUtil.add(Calendar.MONTH, 1);
            calStartDataUtil.set(Calendar.DAY_OF_MONTH, 0);
            //一月多少天
            size = calStartDataUtil.get(Calendar.DATE);
            company = "日";
        }else {
            //一天多少小时
            size = 24;
        }
        map.put("size", size);
        map.put("company", company);
        return map;
    }




    
    /**
     * 获取当前周的数据
     */
    public static List<Map<String, Object>> toDataWek(Long time,List<Map<String, Object>> listData) {
    	//开始时间
        Calendar calStartDataUtil = Calendar.getInstance();
        calStartDataUtil.setTimeInMillis(time);
        calStartDataUtil.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        //开始时间
        Long fristDay = calStartDataUtil.getTimeInMillis();
        calStartDataUtil.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        //结束时间
        //Long endDay = calStartDataUtil.getTimeInMillis();
        //新数据
        List<Map<String, Object>> newData = new  ArrayList<Map<String, Object>>();
        //int startYear = calStartDataUtil.get(Calendar.YEAR);
        String company = "周";
        int size = 0;
    	for (int i = 1; i <= 7; i++) {
    		String format = ConstantsUtil.getYmdFormat().format(new Date(fristDay));
			Map<String, Object> map = new HashMap<String, Object>();
			//数据
			map.put("value", 0);
			for (Map<String, Object> data : listData) {
				//获取时间
				String times = (String) data.get("name");
				if(times != null && format.equals(times)) {
					map.put("value",data.get("value"));
					break;
				}
			}
			map.put("name", company+WEK[i]);
			newData.add(map);
			fristDay = fristDay + 86400000L;
		}
    	return newData;
    }
    


    /**
     * 将月份补全
     */
    public static List<MeterStatisticalBo> complementedMonth(List<MeterStatisticalBo> list, Integer month) {
        List<MeterStatisticalBo> meterStatisticalBos = new ArrayList<MeterStatisticalBo>();
        MeterStatisticalBo meterStatisticalBo = null;
        for (int i = 1; i <= month; i++) {
            meterStatisticalBo = MeterStatisticalBo.builder().name(i+"月").value(BigDecimal.ZERO).build();
            if(null == list || list.size() == 0){

            }else{
                for (MeterStatisticalBo m : list) {
                    String mon = i+"";
                    if (null != m && mon.equals(m.getName())) {
                        meterStatisticalBo.setValue(m.getValue().setScale(1,BigDecimal.ROUND_HALF_UP));
                        break;
                    }
                }
            }
            meterStatisticalBos.add(meterStatisticalBo);
        }
        return meterStatisticalBos;
    }

    /**
     * 通过项目id获取设备主开关号
     *
     * @param projectId
     * @return
     */
    public List<SerialNumberMasterVo> getMasterIndexByProjectId(Integer projectId, Integer year, Integer month) {
        if (null == projectId) {
            log.info("项目id为空！！！！");
            return null;
        }
        //通过项目id查询设备号
        List<String> serialNumbers = pcMeterYearsService.querySerialNumberByProject(projectId, year, month);
        log.info("获取到的设备列表：{}",serialNumbers.toString());
        if (null == serialNumbers || serialNumbers.size() == 0) {
            log.info("通过条件查询的结果为空");
            return null;
        }
        //通过设备号查询主开关编号
        List<SerialNumberMasterVo> list = deviceFeign.queryMasterIndexBySerialNUmber(serialNumbers);
        log.info("查询到的设备开关信息：{}",list);
        return list;

    }


    /**
     *
     * @Title: getIndexAllByProjectId
     * @Description: 通过项目id获取所有设备主开关号
     * @param projectId
     * @return: java.util.List<cn.meiot.entity.vo.SerialNumberMasterVo>
     */
    public List<SerialNumberMasterVo> getIndexAllByProjectId(Integer projectId) {
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);//获取年份
        log.info("年：{}",year);
        int month=cal.get(Calendar.MONTH)+1;//获取月份
        log.info("月：{}",month);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        log.info("天：{}",day);

        if (null == projectId) {
            log.info("项目id为空！！！！");
            return null;
        }
        //通过项目id查询设备号
        List<String> serialNumbers = pcMeterYearsService.getIndexAllByProjectId(projectId,year,month,day);
        if (null == serialNumbers || serialNumbers.size() == 0) {
            log.info("通过条件查询的结果为空");
            return null;
        }
        //通过设备号查询主开关编号
        List<SerialNumberMasterVo> list = deviceFeign.queryMasterIndexBySerialNUmber(serialNumbers);
        return list;
    }

    /**
     *
     * @Title: toMonthandDay
     * @Description: 根据两个时间戳计算相差几个月，相差几天
     * @param startTime
     * @param endTime
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    public static Map<String, Object> toMonthandDay(Long startTime, Long endTime) {


        Map<String, Object> map = new HashMap<>();
        //开始时间
        Calendar calStartDataUtil = Calendar.getInstance();

        //结束时间
        Calendar calEndDataUtil = Calendar.getInstance();

        calStartDataUtil.setTimeInMillis(startTime);
        calEndDataUtil.setTimeInMillis(endTime);

        //开始时间的年
        int startYear = calStartDataUtil.get(Calendar.YEAR);
        int endYear = calEndDataUtil.get(Calendar.YEAR);
        //月
        int startMonth = calStartDataUtil.get(Calendar.MONTH) + 1;
        int endMonth = calEndDataUtil.get(Calendar.MONTH) + 1;


        //日
        int startDay = calStartDataUtil.get(Calendar.DATE);
        int endMoDay = calEndDataUtil.get(Calendar.DATE);


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = format.format(startTime);

        String endDate = format.format(endTime);

        try {
            calStartDataUtil.setTime(format.parse(startDate));
            calEndDataUtil.setTime(format.parse(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
            log.error("统计服务日期类型转换错误toMonthandDay：{}");
        }

        long time1 = calStartDataUtil.getTimeInMillis();
        long time2 = calEndDataUtil.getTimeInMillis();

        int month = endYear *  12  + endMonth  -  (startYear  *  12  +  startMonth) +1;
        int day= (int) ((time2 - time1) / (24 * 3600 * 1000));
        if(day < 1) {
            day = 1;
        }

        map.put("month", month);
        map.put("day", day);
        return map;
    }


    /**
     *
     * @Title: listTime
     * @Description: 生成一个list集合
     * @param time
     * @return: java.util.List<java.lang.Integer>
     */
    public static List<Integer> listTime(int time) {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= time; i++){
            list.add(i);
        }
        return list;
    }



    /**
     *
     * @Title: lastTime
     * @Description: 根据时间计算出上一天的时间
     * @param time
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> lastTime(Long time) {
        Calendar calStartDataUtil = Calendar.getInstance();
        calStartDataUtil.setTimeInMillis(time);
        //开始时间的年
        calStartDataUtil.set(Calendar.HOUR_OF_DAY, 0);
        calStartDataUtil.set(Calendar.MINUTE, 0);
        calStartDataUtil.set(Calendar.SECOND, 0);
        long date = calStartDataUtil.getTime().getTime() - 60000;
        calStartDataUtil.setTimeInMillis(date);

        int startYear = calStartDataUtil.get(Calendar.YEAR);
        //月
        int startMonth = calStartDataUtil.get(Calendar.MONTH) + 1;
        //日
        int startDay = calStartDataUtil.get(Calendar.DATE);
        Map<String, Object> map = new HashMap<>();
        map.put("year", startYear);
        map.put("month", startMonth);
        map.put("day", startDay);
        return map;
    }

    public static List<Map<String, Object>> toDataHour1(Long time, Integer type,List<Map<String, Object>> listData,Integer decimal) {
        //开始时间
        if(decimal == null) {
            decimal = 1;
        }
        //新数据
        List<Map<String, Object>> newData = new  ArrayList<Map<String, Object>>();
        //int startYear = calStartDataUtil.get(Calendar.YEAR);
        String company = "";
        int size = 0;
        if(type == 0) {
            //一年多少月
            size = 12;
//            company = "月";
        }else if(type == 1) {
            Calendar calStartDataUtil = Calendar.getInstance();
            calStartDataUtil.setTimeInMillis(time);
            calStartDataUtil.add(Calendar.MONTH, 1);
            calStartDataUtil.set(Calendar.DAY_OF_MONTH, 0);
            //一月多少天
            size = calStartDataUtil.get(Calendar.DATE);
//            company = "日";
        }else {
            //一天多少小时
            size = 24;
        }
        for (int i = 1; i <= size; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            //数据
            map.put("value", 0);
            if(!CollectionUtils.isEmpty(listData )){
                for (Map<String, Object> data : listData) {
                    //获取时间
                    Integer times = (Integer) data.get("name");
                    if(times != null && times.intValue() == i) {
                        BigDecimal value = (BigDecimal)data.get("value");
                        map.put("value",value.setScale(decimal, BigDecimal.ROUND_HALF_UP));
                        break;
                    }
                }
            }
            map.put("name", i+company);
            newData.add(map);
        }
        return newData;
    }

    //根据时间戳获取当前年的1月1号0:0:0的时间戳（type = 0）,当前年当月1号0:0:0的时间戳(type = 1)
    public Long getRealTime(Long time, Integer type) {
        Calendar calStartDataUtil = Calendar.getInstance();
        calStartDataUtil.setTimeInMillis(time);

        //设置为1号,当前日期既为本月第一天
        calStartDataUtil.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calStartDataUtil.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calStartDataUtil.set(Calendar.MINUTE, 0);
        //将秒至0
        calStartDataUtil.set(Calendar.SECOND,0);
        if(type != null && type.equals(0)) {
            calStartDataUtil.set(Calendar.MONTH, 0);
        }
        return calStartDataUtil.getTimeInMillis();
    }

    public static String getDateLong(String time) {
        String res;
        long lt = new Long(time);
        Date date = new Date(lt);
        res = ConstantsUtil.getSimpleDateFormat().format(date);
        return res;
    }

    public static void main(String[] args) {


//        Map<List<WaterStatisticsMonths>,String> map = new HashMap();
//        Map<WaterStatisticsMonths, String> strings = new HashMap<WaterStatisticsMonths, String>();
//        Map<String, WaterStatisticsMonths> strings = new HashMap<String, WaterStatisticsMonths>();


        List<WaterStatisticsMonths> strings = new ArrayList<>();
//        Set<WaterStatisticsMonths> strings = new HashSet<>();

        WaterStatisticsMonths waterStatisticsMonths = new WaterStatisticsMonths();
        waterStatisticsMonths.setYear(2019);
        waterStatisticsMonths.setDay(28);
        waterStatisticsMonths.setMonth(4);
        waterStatisticsMonths.setUserId(1L);
        waterStatisticsMonths.setProjectId(1);
        waterStatisticsMonths.setId(5L);
//        strings.put("1",waterStatisticsMonths);
        strings.add(waterStatisticsMonths);


        WaterStatisticsMonths waterStatisticsMonths2 = new WaterStatisticsMonths();
        waterStatisticsMonths2.setYear(2019);
        waterStatisticsMonths2.setDay(28);
        waterStatisticsMonths2.setMonth(4);
        waterStatisticsMonths2.setUserId(1L);
        waterStatisticsMonths2.setProjectId(1);
        waterStatisticsMonths2.setId(2L);
        strings.add(waterStatisticsMonths2);
//        strings.put("1",waterStatisticsMonths2);


        WaterStatisticsMonths waterStatisticsMonths3 = new WaterStatisticsMonths();
        waterStatisticsMonths3.setYear(2019);
        waterStatisticsMonths3.setDay(29);
        waterStatisticsMonths3.setMonth(4);
        waterStatisticsMonths3.setUserId(1L);
        waterStatisticsMonths3.setProjectId(1);
        waterStatisticsMonths3.setId(5L);
//        strings.put("1",waterStatisticsMonths3);
        strings.add(waterStatisticsMonths3);

//        List<WaterStatisticsMonths> collect = strings.stream().distinct().collect(Collectors.toList());

//
//        for(int i = 0;i<strings.size();i++){
//            //循环list
//            for(int j = i+1;j<strings.size();j++){
//                if(strings.get(i).equals(strings.get(j))){
//                    strings.remove(i);
//                    //删除一样的元素
//                    i--;
//                    break;
//                }
//            }
//        }
//        strings.forEach(f-> System.out.println(f));


        if(waterStatisticsMonths.getId().equals(waterStatisticsMonths3.getId())) {
            System.out.println("12313");
        }



//        strings.forEach(f -> System.out.println(f));

//        System.out.println(waterStatisticsMonths.equals(waterStatisticsMonths2));
//        System.out.println(strings);
//        map.put(strings,"1");
//
//        map.put(strings,"2");
//
//        System.out.println("map====="+ map);





//
//        List<Integer> nums = Lists.newArrayList(1,1,null,2,3,4,null,5,6,7,8,9,10);
//        System.out.println("sum is:"+nums.stream().filter(num -> num != null)
//                .distinct()
//                .mapToInt(num -> num * 2)
//                .peek(System.out::println) //2,4,6,8,10,12,17
//
////                .skip(2)
//                .limit(4)
//                .sum());
//

//        BigDecimal totalMeter = null;
//        if (totalMeter == null || totalMeter.compareTo(BigDecimal.ZERO) == 0) {
//            System.out.println("132123");
////            //数据空
////            log.info("电量汇总数据为空" , userId, projectId);
////            return Result.faild(ResultCodeEnum.DATA_IS_NULL.getCode(),ResultCodeEnum.DATA_IS_NULL.getMsg());
//        }
    }

}
