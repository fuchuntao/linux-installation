package cn.meiot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mail")
@Data
@Deprecated
public class MailProperties {

    private String from;

    private String to;

    private String host;

    private String password;

    private String platformLoginUrl;

    private String enterpriseLoginUrl;
}
