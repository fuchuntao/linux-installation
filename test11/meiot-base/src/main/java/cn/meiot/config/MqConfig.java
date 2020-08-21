package cn.meiot.config;

import cn.meiot.utils.QueueConstantUtil;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package cn.meiot.config
 * @Description:
 * @author: 武有
 * @date: 2019/11/29 17:01
 * @Copyright: www.spacecg.cn
 */
@Component
public class MqConfig {

    public static final String DEVICE_101="public_device_101";


    /**
     * 延迟队列 TTL 名称
     */
    public static final String DEATH_EQUIPMENT_QUEUE="death_equipment_queue";

    /**
     * routing key 名称
     * 具体消息发送在该 routingKey 的
     */
    public static final String UPGRADE_DELAY_ROUTING_KEY = "upgrade_delay";

    /**
     * DLX，dead letter发送到的 exchange
     * 延时消息就是发送到该交换机的
     */
    public static final String UPGRADE_DELAY_EXCHANGE = "upgrade_delay_exchange";
    public static final String UPGRADE_QUEUE_NAME = "user.upgrade.queue";

    public static final String UPGRADE_EXCHANGE_NAME = "user.upgrade.exchange";
    public static final String UPGRADE_ROUTING_KEY = "upgrade";
    @Bean
    public Queue progressBar(){
        return new Queue(QueueConstantUtil.PROGRESS_BAR);
    }

    /**
     *1、 配置交换机实例
     *
     * @return
     */
    @Bean
    public DirectExchange push() {
        return new DirectExchange(QueueConstantUtil.WSS_CMD_21);
    }




    //声明队列
    @Bean
    public Queue DEVICE101() {
        return new Queue(DEVICE_101, true); // true表示持久化该队列
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

    //绑定
    @Bean
    public Binding binding101() {
        return BindingBuilder.bind(DEVICE101()).to(directExchange101()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_101);
    }

//    @Bean
//    public Queue taskQueue(){
//        return new Queue(TASK_QUEUE);
//    }


    @Bean Queue deathEquipmentQueue(){
        Map<String, Object> params = new HashMap<>();
//         x-dead-letter-exchange 声明了队列里的死信转发到的DLX名称，
        params.put("x-dead-letter-exchange", UPGRADE_EXCHANGE_NAME);
//        // x-dead-letter-routing-key 声明了这些死信在转发时携带的 routing-key 名称。
        params.put("x-dead-letter-routing-key", UPGRADE_ROUTING_KEY);
        return new Queue(UPGRADE_DELAY_EXCHANGE, true, false, false, params);
//        return new Queue(DEATH_EQUIPMENT_QUEUE);
    }

    @Bean
    public DirectExchange upgradeDelayExchange() {
        return new DirectExchange(UPGRADE_DELAY_EXCHANGE);
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(deathEquipmentQueue()).to(upgradeDelayExchange()).with(UPGRADE_DELAY_ROUTING_KEY);
    }

    @Bean
    public Queue upgradeQueue() {
        return new Queue(UPGRADE_QUEUE_NAME, true);
    }

    /**
     * 将路由键和某模式进行匹配。此时队列需要绑定要一个模式上。
     * 符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词。因此“audit.#”能够匹配到“audit.irs.corporate”，但是“audit.*” 只会匹配到“audit.irs”。
     **/
    @Bean
    public TopicExchange orderTopicExchange() {
        return new TopicExchange(UPGRADE_EXCHANGE_NAME);
    }

    @Bean
    public Binding orderBinding() {
        // TODO 如果要让延迟队列之间有关联,这里的 routingKey 和 绑定的交换机很关键
        return BindingBuilder.bind(upgradeQueue()).to(orderTopicExchange()).with(UPGRADE_ROUTING_KEY);
    }

    }
