package cn.meiot.controller;

import cn.meiot.entity.TroubleTicketVo;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.utils.QueueConstantUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Package cn.meiot.controller
 * @Description:
 * @author: 武有
 * @date: 2020/2/17 16:05
 * @Copyright: www.spacecg.cn
 */

@RestController
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @GetMapping("testRabb")
    public String testRabb(){
        TroubleTicketVo troubleTicketVo=new TroubleTicketVo();
        troubleTicketVo.setAlarmTime("2020-12-12");
        troubleTicketVo.setAlarmType(1);
        troubleTicketVo.setAlarmTypeName(FaultTitleEnum.getTitle(1));
        troubleTicketVo.setDeviceId("12345678900");
        troubleTicketVo.setIsShow(0);
        troubleTicketVo.setUserId(123456789L);
        troubleTicketVo.setDeviceName("wuyou");
        troubleTicketVo.setType(0);
        troubleTicketVo.setNote("");
        troubleTicketVo.setCreateTime("2020-12-12");
        rabbitTemplate.convertAndSend(QueueConstantUtil.TROUBLE_TICKET, JSONObject.toJSONString(troubleTicketVo));
		
		
        return "ok";
    }
}
