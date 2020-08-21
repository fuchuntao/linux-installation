package cn.meiot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@RestController
public class MeiotTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeiotTaskApplication.class, args);
    }


    /**
     * actuator jar包中的/actuator/health路径无法访问，这方法用于服务的健康检查，路径与bootstrap中 health-check-path保持一直即可
     * @return
     */
    @RequestMapping(value = "/health",method = RequestMethod.GET)
    public String test(){

        return "health";
    }

}
