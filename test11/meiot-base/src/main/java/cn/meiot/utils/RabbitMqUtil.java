package cn.meiot.utils;

import cn.meiot.config.MqConfig;
import cn.meiot.entity.Wss;
import cn.meiot.entity.equipment.Updata;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/12/3 9:00
 * @Copyright: www.spacecg.cn
 */
@Component
@Slf4j
public class RabbitMqUtil {
    private static String ip;
    private static Integer port;
    private static RabbitTemplate rabbitTemplate;
    //延时队列时间
    public static final int TIME=7;
    private RabbitMqUtil() {
    }

    public static void init(String ip, Integer port, RabbitTemplate rabbitTemplate) {
        RabbitMqUtil.ip = ip;
        RabbitMqUtil.port = port;
        RabbitMqUtil.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 给硬件发送更新消息
     *
     * @param serialNumber
     * @param version
     * @return
     */
    public static Map<String, Object> sendMsg(String serialNumber, String version) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("serialNumber", serialNumber);
        Updata updata = new Updata();
        updata.setIp(ip);
        updata.setPort(port);
        updata.setVersion(version);
        map.put("updata", updata);
        rabbitTemplate.convertAndSend(QueueConstantUtil.UP_VERSION, map);
        return map;
    }

    public static void sendMsg(Integer type, String group, Object o) {
        RabbitMqUtil.rabbitTemplate.convertAndSend(QueueConstantUtil.WSS_CMD_21,
                QueueConstantUtil.WSS_KEY, JSONObject.toJSONString(new Wss(type, group, JSONObject.toJSONString(o))));
        log.info("socket消息已经推送，推送 type:{}, group:{},内容:{}",type,group,o);

    }

    /**
     * 发送设备
     */
    public static void sendMsg(String serialNumber) {
        log.info("【升级生成时间】" + new Date().toString() + "【"+TIME+"分钟后检查是否升级】" + serialNumber);
        rabbitTemplate.convertAndSend(MqConfig.UPGRADE_DELAY_EXCHANGE, MqConfig.UPGRADE_DELAY_ROUTING_KEY, serialNumber, message -> {
            // 如果配置了 params.put("x-message-ttl", 5 * 1000); 那么这一句也可以省略,具体根据业务需要是声明 Queue 的时候就指定好延迟时间还是在发送自己控制时间
            message.getMessageProperties().setExpiration(TIME * 1000 * 60 + "");
            return message;
        });
    }


    @Value("${upgrade.ip}")
    public void setIp(String ip) {
        RabbitMqUtil.ip = ip;
    }


    @Value("${upgrade.port}")
    public void setPort(Integer port) {
        RabbitMqUtil.port = port;
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        RabbitMqUtil.rabbitTemplate = rabbitTemplate;
    }
}
