package cn.meiot.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Package cn.meiot.aop
 * @Description:
 * @author: 武有
 * @date: 2019/12/11 14:16
 * @Copyright: www.spacecg.cn
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpgradeDetection {
    String value() default "设备升级中...";
}
