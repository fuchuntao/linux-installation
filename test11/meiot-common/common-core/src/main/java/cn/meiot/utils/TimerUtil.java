package cn.meiot.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimerUtil {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	//获取当前格式化时间
	public static Long getTime(String time) {
		Date format = null;
		try {
			format = sdf.parse(time);
		} catch (ParseException e) {
			return null;
		}
		return format.getTime()/1000;
	}
	
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	//获取当前格式化时间
	public static Long getTime2(String time) {
		Date format = null;
		try {
			format = sdf2.parse(time);
		} catch (ParseException e) {
			return null;
		}
		return format.getTime()/1000;
	}
}
