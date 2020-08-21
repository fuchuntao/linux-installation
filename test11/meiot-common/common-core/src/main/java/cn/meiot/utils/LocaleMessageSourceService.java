package cn.meiot.utils;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public class LocaleMessageSourceService {

    /**
     * @return
     * @paramcode：对应messages配置的key.
     */
    public static String getMessage(String code) {
        return getMessage(code, null);
    }

    /**
     * @return
     * @paramcode：对应messages配置的key.
     * @paramargs:数组参数.
     */
    public static String getMessage(String code, Object[] args) {
        return getMessage(code, args, "");
    }


    /**
     * @return
     * @paramcode：对应messages配置的key.
     * @paramargs:数组参数.
     * @paramdefaultMessage:没有设置key的时候的默认值.
     */
    public static String getMessage(String code, Object[] args, String defaultMessage) {
        //这里使用比较方便的方法，不依赖request.
        Locale locale = LocaleContextHolder.getLocale();
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(ConstantsUtil.INTERNATIONALIZATION);
        String content = messageSource.getMessage(code, args, locale);
        System.out.println("msg:  " + content);
        return content;

    }
}
