package cn.meiot.utlis;

import cn.meiot.utils.ConstantsUtil;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeUtlis {
	//private TimeUtlis() {}
	//private static SimpleDateFormat sdf = new SimpleDateFormat("dd HH:00");
	//获取当前格式化时间
	public static String getTime() {
		long currentTimeMillis = System.currentTimeMillis();
		String format = ConstantsUtil.getddHH00Format().format(currentTimeMillis);
		return format;
	}

	//private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
	/**
	 * 当前年
	 * @return
	 */
	public static Integer getYearTime() {
		long currentTimeMillis = System.currentTimeMillis();
		String format = ConstantsUtil.getyyyyFormat().format(currentTimeMillis);
		return Integer.valueOf(format);
	}
	//private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
	//private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH.mm");
	/**
	 * 拼接时间
	 * @return
	 */
	public static Long getTime2(Long time,String HHmm) {
		Date date = new Date(time*1000);
		String format = ConstantsUtil.getyyyymmddFormat().format(date) +" " +HHmm;
		Date date2 = null;
		try {
			date2 = ConstantsUtil.getyyyyMMddhh_mmFormat1().parse(format);
		} catch (ParseException e) {
			return null;
		}
		return date2.getTime()/1000;
	}
}
