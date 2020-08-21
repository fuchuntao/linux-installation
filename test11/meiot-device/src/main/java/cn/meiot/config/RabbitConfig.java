package cn.meiot.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.meiot.constart.RabbitConstart;
import cn.meiot.utils.QueueConstantUtil;

/**
 * @author lingzhiying
 * @title: RabbitConfig.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月13日
 */

@Configuration
public class RabbitConfig{


	
	//声明队列104
    @Bean
    public Queue DEVICE104() {
        return new Queue(RabbitConstart.DEVICE_104, true); // true表示持久化该队列
    }

    //声明队列210
    @Bean
    public Queue DEVICE210() {
        return new Queue(RabbitConstart.DEVICE_210, true); // true表示持久化该队列
    }

    //声明队列210
    @Bean
    public Queue DEVICE221() {
        return new Queue(RabbitConstart.DEVICE_221, true); // true表示持久化该队列
    }

    //声明队列210
    @Bean
    public Queue DEVICE208() {
        return new Queue(RabbitConstart.DEVICE_208, true); // true表示持久化该队列
    }
    //声明队列210
    @Bean
    public Queue DEVICE213() {
        return new Queue(RabbitConstart.DEVICE_213, true); // true表示持久化该队列
    }
    @Bean
    public Queue DEVICE216() {
        return new Queue(RabbitConstart.DEVICE_216, true); // true表示持久化该队列
    }

    //声明队列210
    @Bean
    public Queue DEVICE215() {
        return new Queue(RabbitConstart.DEVICE_215, true); // true表示持久化该队列
    }


    //声明华为注册设备队列
    @Bean
    public Queue registeRserial() {
        return new Queue(QueueConstantUtil.REGISTE_RSERIAL, true); // true表示持久化该队列
    }

    //声明华为注册设备队列
    @Bean
    public Queue activationRserial() {
        return new Queue(QueueConstantUtil.ACTIVATION_RSERIAL, true); // true表示持久化该队列
    }
    
  //声明队列
    @Bean
    public Queue DEVICE108() {
        return new Queue(RabbitConstart.DEVICE_108, true); // true表示持久化该队列
    }

    //声明队列
    @Bean
    public Queue DEVICE101() {
        return new Queue(RabbitConstart.DEVICE_101, true); // true表示持久化该队列
    }
    

    
    /**
     * 配置交换机实例  104
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_STATUS);
    }
    
    /**
     * 配置交换机实例  108
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange2() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_REGISTER);
    }

    /**
     * 配置交换机实例  101
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange101() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_101);
    }

    /**
     * 配置交换机实例  210
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange210() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_210);
    }

    /**
     * 配置交换机实例  221
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange221() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_221);
    }

    /**
     * 配置交换机实例  208
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange208() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_208);
    }


    /**
     * 配置交换机实例  213
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange213() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_213);
    }

    /**
     * 配置交换机实例  216
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange216() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_216);
    }

    /**
     * 配置交换机实例  215
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange215() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_215);
    }



    //绑定
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(DEVICE104()).to(directExchange()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_KEY);
    }
    
  //绑定
    @Bean
    public Binding binding2() {
        return BindingBuilder.bind(DEVICE108()).to(directExchange2()).with(QueueConstantUtil.MQTT_DEVICE_REGISTER_KEY);
    }

    //绑定
    @Bean
    public Binding binding101() {
        return BindingBuilder.bind(DEVICE101()).to(directExchange101()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_101);
    }

    //绑定
    @Bean
    public Binding binding210() {
        return BindingBuilder.bind(DEVICE210()).to(directExchange210()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_210);
    }

    //绑定
    @Bean
    public Binding binding221() {
        return BindingBuilder.bind(DEVICE221()).to(directExchange221()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_221);
    }

    //绑定
    @Bean
    public Binding binding208() {
        return BindingBuilder.bind(DEVICE208()).to(directExchange208()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_208);
    }

    //绑定
    @Bean
    public Binding binding213() {
        return BindingBuilder.bind(DEVICE213()).to(directExchange213()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_213);
    }

    //绑定
    @Bean
    public Binding binding216() {
        return BindingBuilder.bind(DEVICE216()).to(directExchange216()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_216);
    }

    //绑定
    @Bean
    public Binding binding215() {
        return BindingBuilder.bind(DEVICE215()).to(directExchange215()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_215);
    }

}
