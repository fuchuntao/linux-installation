package cn.meiot.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Package cn.meiot.utils
 * @Description: 日期格式话
 * @author: 武有
 * @date: 2019/11/21 12:30
 * @Copyright: www.spacecg.cn
 */
public class DateUtil {
    public static  final SimpleDateFormat SDF =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date getDate(String time) {
        String format = SDF.format(Long.valueOf(time));

        try {
            return  SDF.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer compare(Date date1,Date date2){
        String format1 = SDF.format(date1);
        String format2 = SDF.format(date2);
       return format1.compareTo(format2);
    }
}
