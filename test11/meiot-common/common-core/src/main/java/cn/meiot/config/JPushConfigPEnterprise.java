package cn.meiot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JPushConfigPEnterprise {


    @Value("${jpush2.appKey}")
    private String appKey;// = "2aa8ffc8e41cc7c351eb6dda";
    @Value("${jpush2.masterSecret}")
    private String masterSecret;// ="b3c188568fd63c06c564cb55";

    @Value("${jpush2.apnsProduction}")
    private boolean apnsProduction;

}
