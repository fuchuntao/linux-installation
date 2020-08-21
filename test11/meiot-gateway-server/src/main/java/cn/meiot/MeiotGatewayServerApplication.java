package cn.meiot;

import cn.meiot.filter.AccessGatewayFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableDiscoveryClient
public class MeiotGatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeiotGatewayServerApplication.class, args);
    }

    @Bean
    public AccessGatewayFilter accessGatewayFilter() {
        return new AccessGatewayFilter();
    }

}
