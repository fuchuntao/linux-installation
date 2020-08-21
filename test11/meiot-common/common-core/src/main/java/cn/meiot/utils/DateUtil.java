package cn.meiot.utils;

import cn.meiot.entity.vo.YearAndMonth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    public static final SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final  Calendar calendar = Calendar.getInstance();
    /**
     * 获取当前时间
     * @return
     */
    public static  String getNowDate(){

        return sd.format(new Date());
    }


	/**
	 * 通过时间戳获取年月
	 * @param startTime
	 * @return
	 */
	public static YearAndMonth getYearByTimestamp(Long startTime) {
		calendar.setTimeInMillis(startTime);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return YearAndMonth.builder().year(year).month(month).day(day).build();
    }


	/**
	 * 获取随机整数
	 * @param size   范围
	 * @return
	 */
	public static   int getRandomNum(int size) {
		Random random = new Random();
		int num = random.nextInt(size);
		return num;

	}

	public static void main(String[] args) {
		long num = new DateUtil().getRandomNum(110);
		System.out.println(num);
	}

    public List<Map<String, Object>> toDataNearlyYear(List<Map<String, Object>> listData,Long time){
    	//Date date = new Date();//获取当前时间    
    	List<Map<String, Object>> newData = new  ArrayList<Map<String, Object>>();
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.setTimeInMillis(time);
    	int sunYear = calendar.get(Calendar.YEAR);//获取当前年份
        int sunMonth = calendar.get(Calendar.MONTH) + 1;//获取当前月份
        int sunDay = calendar.get(Calendar.DATE)+1;//获取当日
    	calendar.add(Calendar.YEAR, -1);//当前时间减去一年，即一年前的时间    
    	calendar.add(Calendar.MONTH, +1);
    	calendar.add(Calendar.DATE, +1);
    	int monYear = calendar.get(Calendar.YEAR);//获取过去年份
        int monMonth = calendar.get(Calendar.MONTH) + 1;//获取过去月份
        int monDay = calendar.get(Calendar.DATE);//获取当日
        if(sunYear != monYear) {
        	for (int i = monMonth; i <= 12 ; i++) {
        		Map<String, Object> map = new HashMap<String, Object>();
        		map.put("value", 0);
				String str = monYear + "-" + i;
				for (Map<String, Object> map2 : listData) {
					if(str.equals((String)map2.get("name"))){
						map.put("value", map2.get("value"));
						break;
					}
				}
				map.put("name", str);
				newData.add(map);
			}
        	for (int i = 1; i <= sunMonth ; i++) {
        		Map<String, Object> map = new HashMap<String, Object>();
        		map.put("value", 0);
				String str = sunYear + "-" + i;
				for (Map<String, Object> map2 : listData) {
					if(str.equals((String)map2.get("name"))){
						map.put("value", map2.get("value"));
						break;
					}
				}
				map.put("name", str);
				newData.add(map);
			}
        }else {
        	for (int i = 1; i <= 12 ; i++) {
        		Map<String, Object> map = new HashMap<String, Object>();
        		map.put("value", 0);
				String str = monYear + "-" + i;
				for (Map<String, Object> map2 : listData) {
					if(str.equals((String)map2.get("name"))){
						map.put("value", map2.get("value"));
						break;
					}
				}
				map.put("name", str);
				newData.add(map);
			}
        }
        return newData;
    }

	/**
	 *
	 * @Title: toDataHour
	 * @Description: 根据天补全用电量
	 * @param startTime
	 * @param endTime
	 * @param meterList
	 * @Title: toDataHour
	 * @Description: 根据天补全用电量
	 * @return: java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
	 */
	public static List<Map<String, Object>> toTimeData(Long startTime, Long endTime, List<Map<String, Object>> meterList,
													   int startYear,int startMonth, int endYear, int endMonth) {

		List<Map<String, Object>> newData = new ArrayList<Map<String, Object>>();
		//开始时间
		Calendar calStartDataUtil = Calendar.getInstance();

		//结束时间
		Calendar calEndDataUtil = Calendar.getInstance();


		if(startTime != null) {
			calStartDataUtil.setTimeInMillis(startTime);
			//开始时间的年
			 startYear = calStartDataUtil.get(Calendar.YEAR);
			//月
			 startMonth = calStartDataUtil.get(Calendar.MONTH) + 1;
		}
		if(endTime != null) {
			calEndDataUtil.setTimeInMillis(endTime);
			endYear = calEndDataUtil.get(Calendar.YEAR);
			endMonth = calEndDataUtil.get(Calendar.MONTH) + 1;
		}
		//日
//		int startDay = calStartDataUtil.get(Calendar.DATE);
//		int endMoDay = calEndDataUtil.get(Calendar.DATE);


		if (startYear != endYear) {
			for (int i = startMonth; i <= 12; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("value", 0);
				map.put("year", startYear);//年
				map.put("month", i);//月

				for (Map<String, Object> map2 : meterList) {
					//获取时间
					Integer yearS = (Integer) map2.get("year");
					Integer monthS = (Integer) map2.get("month");
					//年相等
					if (yearS != null && yearS.intValue() == startYear && monthS.intValue() == i) {
						map.put("value", map2.get("value"));
						break;
					}
				}
				newData.add(map);

			}
			for (int i = 1; i <= endMonth; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("value", 0);
				map.put("year", endYear);//年
				map.put("month", i);//月

				for (Map<String, Object> map2 : meterList) {
					//获取时间
					Integer yearS = (Integer) map2.get("year");
					Integer monthS = (Integer) map2.get("month");
					//年相等
					if (yearS != null && yearS.intValue() == endYear && monthS.intValue() == i) {
						map.put("value", map2.get("value"));
						break;
					}
				}
				newData.add(map);
			}
		} else {
			for (int i = 1; i <= 12; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("value", 0);
				map.put("year", startYear);//年
				map.put("month", i);//月

				for (Map<String, Object> map2 : meterList) {
					//获取时间
					Integer yearS = (Integer) map2.get("year");
					Integer monthS = (Integer) map2.get("month");
					//年相等
					if (yearS != null && yearS.intValue() == startYear && monthS.intValue() == i) {
						map.put("value", map2.get("value"));
						break;
					}
				}
				newData.add(map);
			}
		}
		return newData;
	}

		/**
         * 将字符串时间转成时间戳格式
         * @param time
         * @return
         */
    public static Long StringToTimestamp(String time){
        try {
            long value = ConstantsUtil.getSimpleDateFormat().parse(time).getTime();
            return value;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

	/**
	 * 获取当前年份
	 * @return
	 */
	public static Integer getYear(){
		Calendar date = Calendar.getInstance();
		String nowYear = String.valueOf(date.get(Calendar.YEAR));
		return Integer.valueOf(nowYear);
	}

}
