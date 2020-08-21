package cn.meiot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class WaterConstart {
    /**
     * 机构码
     */
    @Value("${water.ccode}")
    private String ccode ;
    /**
     * 账号
     */
    @Value("${water.username}")
    private String username;
    /**
     * 密码
     */
    @Value("${water.password}")
    private String password;

    /**
     * 域名
     */
    private final static String URL = "https://app.iowater.com.cn/api/";
    /**
     * 登陆鉴权
     */
    public final static String LOGIN = URL+"auth";
    /**
     *获取客户列表
     */
    public final static String CUSTOMER=  URL+"customer?token=";
    /**
     *获取水箱列表
     */
    public final static String IMETER=  URL+"imeter?token=";
    /**
     *获取抄表列表
     */
    public final static String RECORD=  URL+"record?token=";

}
