package cn.meiot;

import cn.meiot.utils.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrix
@EnableFeignClients
@ComponentScan("cn.meiot.*")
@MapperScan("cn.meiot.mapper")
//@EnableAsync
public class MeiotMessageApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext= SpringApplication.run(MeiotMessageApplication.class, args);
        SpringUtil.setApplicationContext(applicationContext);
    }

}
