package cn.meiot.utils;

import org.springframework.context.ApplicationContext;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/12/30 11:00
 * @Copyright: www.spacecg.cn
 */
public class BeanUtils {
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        BeanUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name){
       return applicationContext.getBean(name);
    }

    public static Object getBean(Class c){
       return applicationContext.getBean(c);
    }


}
