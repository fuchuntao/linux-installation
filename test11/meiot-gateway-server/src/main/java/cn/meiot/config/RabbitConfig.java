package cn.meiot.config;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitConfig {

    public static final String QUEUE_NAME = "refreshTokenTime";

    public static final String DEL_USER_TOKEN = "delUserToken";

    @Bean
    public Queue refreshTokenTime(){

        return new Queue(QUEUE_NAME);
    }


    @Bean
    public Queue delUserToken(){

        return new Queue(DEL_USER_TOKEN);
    }
}
