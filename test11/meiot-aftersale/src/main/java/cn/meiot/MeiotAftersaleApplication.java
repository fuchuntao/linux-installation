package cn.meiot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("cn.meiot.*")
@EnableTransactionManagement
@EnableHystrix
@EnableFeignClients
public class MeiotAftersaleApplication {


    public static void main(String[] args) {
        SpringApplication.run(MeiotAftersaleApplication.class, args);
    }

}
