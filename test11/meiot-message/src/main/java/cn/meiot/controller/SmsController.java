package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SmsVo;
import cn.meiot.enums.JpushTypeEnum;
import cn.meiot.jg.MyJPushClient;
import cn.meiot.service.SmsService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sms")
@Slf4j
public class SmsController {

    private SmsService smsService;

    @Autowired
    private MyJPushClient myJPushClient;

    public SmsController(SmsService smsService){
        this.smsService = smsService;
    }


    /**
     * 获取短信信息
     * @return
     */
    @RequestMapping(value = "getSms",method = RequestMethod.POST)
    @Log(operateContent = "获取短信信息",operateModule = "消息服务")
    public Result getSms(@RequestBody SmsVo smsVo){
        log.info("进入查询验证码步骤");
        return smsService.getSms(smsVo);
    }

    @RequestMapping(value = "hello",method = RequestMethod.GET)
    public String hello(){
        List<String> tagsList = new ArrayList<String>();
        tagsList.add("10000125");
        String a = "{\"mainUserPhone\":\"151****1508\",\"subUserName\":\"null\",\"subUserPhone\":\"188****8888\",\"mainUserName\":\"骚逼凌智颖\",\"subUser\":\"3\",\"mainUser\":\"8\"}";
        Map<String,String> map = new HashMap<String,String>();
        map = new Gson().fromJson(a, Map.class);
        map.put("msgType","2");
//        MyJPushClient myJPushClient = new MyJPushClient();
        myJPushClient.sendToTagsList(tagsList,"通知栏标题","·","故障消息内容", JpushTypeEnum.NOTIFICATION.value(),map,2);
        return "I'm   9527";
    }
}
