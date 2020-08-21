package cn.meiot.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/9/18 11:28
 * @Copyright: www.spacecg.cn
 */
public class DateUtil {

    public  static String getCurrentTime(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }
}
