package cn.meiot;

import cn.meiot.jg.JPushClientExample;
import cn.meiot.jg.MyJPushClient;
import cn.meiot.service.IFaultMessageService;
import cn.meiot.utils.*;
import cn.meiot.utils.enums.JpushTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@SuppressWarnings("all")
public class MeiotMessageApplicationTests {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private IFaultMessageService iFaultMessageService;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private EmailTool emailTool;

    @Autowired
    private JPushClientExample jPushClientExample;

    @Test
    public void contextLoads() throws Exception {
//        Map<String, Object> map = new HashMap<String,Object>();
        //emailTool.sendmail("密码通知",map,"361426201@qq.com");
//        rabbitTemplate.convertAndSend(QueueConstantUtil.MQTT_DEVICE_REGISTER,QueueConstantUtil.MQTT_DEVICE_STATUS_KEY,"666");

//        iFaultMessageService.deleteMsgByUserIdAndSerialNumber(10000044L,"AD13-1909050001");
//        List<String> strings=new ArrayList<>();
//        strings.add("10000126");
//jPushClientExample.sendMsg(strings,"sadfsadf","324656","6546456",);

        List<String> tagsList = new ArrayList<String>();
        tagsList.add("10000124");
        String a = "{\"mainUserPhone\":\"151****1508\",\"subUserName\":\"null\",\"subUserPhone\":\"188****8888\",\"mainUserName\":\"骚逼凌智颖\",\"subUser\":\"3\",\"mainUser\":\"8\"}";
        Map<String,String> map = new HashMap<String,String>();
        map = new Gson().fromJson(a, Map.class);
        map.put("msgType","2");
        jPushClientExample.sendMsg(tagsList,"通知栏标题","故障消息标题","故障消息内容", JpushTypeEnum.NOTIFICATION.value(),map,2);

    }




}
