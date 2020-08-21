/*package cn.meiot.config;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.meiot.dao.EquipmentMapper;
import cn.meiot.dao.SwitchMapper;
import cn.meiot.entity.db.Equipment;
import cn.meiot.entity.db.Switch;
import cn.meiot.entity.equipment.Device;
import cn.meiot.utils.MqttUtil;

@Configuration
@IntegrationComponentScan
public class MqttReceiveConfig {
	
	@Value("${spring.mqtt.username}")
	private String username;

	@Value("${spring.mqtt.password}")
	private String password;

	@Value("${spring.mqtt.url}")
	private String hostUrl;

	@Value("${spring.mqtt.client.id}")
	private String clientId;

	@Value("${spring.mqtt.default.topic}")
	private String defaultTopic;

	@Value("${spring.mqtt.completionTimeout}")
	private int completionTimeout; // 连接超时

	@Bean
	public MqttConnectOptions getMqttConnectOptions() {
		MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
		mqttConnectOptions.setUserName(username);
		mqttConnectOptions.setPassword(password.toCharArray());
		mqttConnectOptions.setServerURIs(new String[] { hostUrl });
		mqttConnectOptions.setKeepAliveInterval(60);
		return mqttConnectOptions;
	}

	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		factory.setConnectionOptions(getMqttConnectOptions());
		return factory;
	}

//接收通道
	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}

//配置client,监听的topic 
	@Bean
	public MessageProducer inbound() {
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId + "_inbound",
				mqttClientFactory(), "#");
		adapter.setCompletionTimeout(completionTimeout);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(1);
		adapter.setOutputChannel(mqttInputChannel());
		return adapter;
	}
	
	@Autowired
	private SwitchMapper switchMapper;
	
	@Autowired
	private EquipmentMapper equipmentMapper;

//通过通道获取数据
	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				String payload = message.getPayload().toString();
				System.out.println(payload);
				Map parseObject = JSON.parseObject(payload,Map.class);
				String cmd = (String) parseObject.get("cmd");
				String serialNumber = (String) parseObject.get("deviceid");
				JSONObject content = (JSONObject) parseObject.get("desired");
				if(MqttUtil.CMD_108.equals(cmd)) {
					//a(serialNumber, content);
				}
				//System.out.println("-----------"+payload+"------------------");
				String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
				String type = topic.substring(topic.lastIndexOf("/") + 1, topic.length());
				if ("hello".equalsIgnoreCase(topic)) {
					System.out.println("hello,fuckXX1," + message.getPayload().toString());
				} else if ("hello1".equalsIgnoreCase(topic)) {
					System.out.println("hello1,fuckXX2," + message.getPayload().toString());
				}
			}

		};
	}
}
*/