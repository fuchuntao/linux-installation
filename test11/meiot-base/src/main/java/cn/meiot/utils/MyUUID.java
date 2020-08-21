package cn.meiot.utils;

import java.util.UUID;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/11/21 11:53
 * @Copyright: www.spacecg.cn
 */
public class MyUUID {
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
