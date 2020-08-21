package cn.meiot;

//import org.mybatis.spring.annotation.MapperScan;

import cn.meiot.aop.UpgradeDetectionAspect;
import cn.meiot.controller.BaseBaseController;
import cn.meiot.utils.LogUtil;
import cn.meiot.utils.RedisUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude= DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableHystrix
@EnableFeignClients(basePackages = {"cn.meiot.feign*"})
public class MeiotBaseApplication {



    public static void main(String[] args) {
        ApplicationContext run = SpringApplication.run(MeiotBaseApplication.class, args);
        RedisTemplate<String,String> redisTemplate = run.getBean("stringRedisTemplate",RedisTemplate.class);
        BaseBaseController baseBaseController=run.getBean("baseBaseController",BaseBaseController.class);
        RedisUtil.setRedisTemplate(redisTemplate);
        RedisUtil.setBaseBaseController(baseBaseController);
        LogUtil bean = run.getBean(LogUtil.class);
        bean.setRedisTemplate(redisTemplate);
        UpgradeDetectionAspect bean1 = run.getBean(UpgradeDetectionAspect.class);
        bean1.setRedisTemplate(redisTemplate);


    }






}
