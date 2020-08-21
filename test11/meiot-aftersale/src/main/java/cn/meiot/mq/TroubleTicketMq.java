package cn.meiot.mq;

import cn.meiot.entity.TroubleTicket;
import cn.meiot.entity.TroubleTicketUser;
import cn.meiot.entity.TroubleTicketVo;
import cn.meiot.service.ITroubleTicketService;
import cn.meiot.service.TroubleTicketUserService;
import cn.meiot.utils.QueueConstantUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Package cn.meiot.mq
 * @Description:
 * @author: 武有
 * @date: 2020/2/17 16:11
 * @Copyright: www.spacecg.cn
 */

@Component
@Slf4j
public class TroubleTicketMq {

    @Autowired
    private ITroubleTicketService troubleTicketService;
    @Autowired
    private TroubleTicketUserService troubleTicketUserService;

    @RabbitListener(queues = QueueConstantUtil.TROUBLE_TICKET)
    public void troubleTicketMq(String content){
        TroubleTicketVo troubleTicketVo = JSONObject.parseObject(content, TroubleTicketVo.class);
        TroubleTicket troubleTicket=new TroubleTicket();
        BeanUtils.copyProperties(troubleTicketVo,troubleTicket);
        troubleTicket.setRepairTime(DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        troubleTicketService.save(troubleTicket);
        List<TroubleTicketVo.TroubleTicketVoUserAlarm> userAlarms = troubleTicketVo.getUserAlarms();
        for (TroubleTicketVo.TroubleTicketVoUserAlarm userAlarm : userAlarms) {
            TroubleTicketUser troubleTicketUser=new TroubleTicketUser();
            BeanUtils.copyProperties(userAlarm,troubleTicketUser);
            troubleTicketUserService.insert(troubleTicketUser);
        }
        log.info("故障工单生成：{}",troubleTicket);
    }
}
