package cn.meiot.utils;

import lombok.Data;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/12/19 12:17
 * @Copyright: www.spacecg.cn
 */
public class SpringUtil {

    @Setter
    private static ApplicationContext applicationContext;

    public static <V> V getBean(String name){
      return (V) applicationContext.getBean(name);
    }
}
