package cn.meiot.receive;

import cn.meiot.entity.SystemMessage;
import cn.meiot.entity.vo.PublishSystemBulletin;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SysMsgVo;
import cn.meiot.enums.AccountType;
import cn.meiot.enums.DeviceBindStatus;
import cn.meiot.enums.SysMsgType;
import cn.meiot.feign.UserFeign;
import cn.meiot.jg.JPushClientExample;
import cn.meiot.service.IFaultMessageService;
import cn.meiot.service.ISystemMessageService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.enums.JpushTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class SysMsgMq {

    @Autowired
    private JPushClientExample jPushClientExample;

    @Autowired
    private ISystemMessageService systemMessageService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IFaultMessageService iFaultMessageService;

    @Autowired
    private UserFeign userFeign;



    /**
     * 接收系统绑定请求队列
     */
    @RabbitListener(queues = QueueConstantUtil.SYS_MSG_QUEUE)
    public void sysMsgRe(SysMsgVo sysMsgVo) {
        try {
            //判断需要发送的用户id是否为空
            log.info("接收到的参数：{}", sysMsgVo);
            if (null == sysMsgVo.getExtras()) {
                log.info("扩展参数为空，终止执行");
                return;
            }
            // log.info("需要推送的主用户id：{}，子账户id:{}", sysMsgVo.getExtras().get("mainUser"), sysMsgVo.getExtras().get("subUser"));
            Map<String, String> map = sysMsgVo.getExtras();
            Gson gson = new Gson();

            Integer status = sysMsgVo.getDealStatus();
            //获取当前事件
            String dateTime = ConstantsUtil.DF.format(new Date());
            //判断是否绑定请求
            if (DeviceBindStatus.PENDING.value().equals(status)) {
                log.info("执行了插入操作===============>");
                //执行插入操作
                String extras = gson.toJson(map);
                SystemMessage systemMessage = SystemMessage.builder().serialName(sysMsgVo.getSerialName())
                        .serialNumber(sysMsgVo.getSerialNumber()).userId(Long.valueOf(map.get("mainUser")))
                        .extendId(sysMsgVo.getExtendId())
                        .dealStatus(status)
                        .subtitle(sysMsgVo.getSubtitle()).isRead(0).type(sysMsgVo.getType()).createTime(dateTime)
                        .updateTime(dateTime)
                        .content(sysMsgVo.getContent()).extras(extras).build();
                systemMessageService.save(systemMessage);
                systemMessage.setUserId(Long.valueOf(map.get("subUser")));
                extras = gson.toJson(map);
                systemMessage.setExtras(extras);
                systemMessageService.save(systemMessage);

            } else {
                log.info("执行了更新操作===============>");
                SystemMessage systemMessage = SystemMessage.builder()
                        .dealStatus(status)
                        .updateTime(dateTime)
                        .build();
                systemMessageService.update(systemMessage, new UpdateWrapper<SystemMessage>()
                        .eq("extend_id", sysMsgVo.getExtendId()).eq("type", SysMsgType.PEND_REQ.value()));
            }
            List<String> list = new ArrayList<String>();
            list.add(map.get("mainUser"));
            list.add(map.get("subUser"));
            log.info("推送的用户列表：{}", list);
            //推送给用户
            sysMsgVo.getExtras().put("msgType","1");
            sysMsgVo.getExtras().put("deviceStatus","1");
            sysMsgVo.getExtras().put("serialNumber",sysMsgVo.getSerialNumber());
            jPushClientExample.sendMsg(list, "绑定消息提醒", "绑定消息提醒",
                    "绑定消息提醒", JpushTypeEnum.NOTIFICATION.value(), sysMsgVo.getExtras(),5);
        } catch (Exception e) {
            log(e,sysMsgVo,QueueConstantUtil.SYS_MSG_QUEUE);
        }
    }

    /**
     * 解绑消息通知
     *
     * @param sysMsgVo
     */
    @RabbitListener(queues = QueueConstantUtil.UNBIND_DEVICE_NOTIFICATION)
    public void unbindDevice(SysMsgVo sysMsgVo) {
        try{
            log.info("接收到的解绑参数：{}", sysMsgVo);
            //判断是否有需要推送的用户
            if (null == sysMsgVo || (null == sysMsgVo.getUserId() || sysMsgVo.getUserId().size() == 0)) {
                log.info("解绑参数为空，或者此账户未有子账户，不需要推送消息");
                return;
            }
            //将信息保存到数据库中
            String dateTime = ConstantsUtil.DF.format(new Date());//获取当前时间
            SystemMessage systemMessage = SystemMessage.builder().type(/*SysMsgType.UNBIND.value()*/ sysMsgVo.getType()).serialNumber(sysMsgVo.getSerialNumber()).dealStatus(sysMsgVo.getDealStatus())
                    .isRead(0).createTime(dateTime).updateTime(dateTime).serialName(sysMsgVo.getSerialName()).build();
            Map<String, String> map = sysMsgVo.getExtras();
            if (null != map && map.size() > 0) {
                //获取扩展参数
                String extra = new Gson().toJson(map);
                systemMessage.setExtras(extra);
            }
            sysMsgVo.getUserId().forEach(userId -> {
                systemMessage.setUserId(Long.valueOf(userId));
                //保存
                systemMessageService.save(systemMessage);
            });
            //推送
            sysMsgVo.getExtras().put("msgType","1");//1：系统消息  2：故障消息
            sysMsgVo.getExtras().put("deviceStatus","2");//1是绑定 2是解綁
            sysMsgVo.getExtras().put("serialNumber",sysMsgVo.getSerialNumber());
            jPushClientExample.sendMsg(sysMsgVo.getUserId(), "设备解绑提醒", "设备解绑提醒",
                    "设备解绑提醒", JpushTypeEnum.NOTIFICATION.value(), map,5);
        }catch (Exception e){
            log(e,sysMsgVo,QueueConstantUtil.UNBIND_DEVICE_NOTIFICATION);
        }
    }


    /**
     * 系统公告
     *
     * @param publishSystemBulletin
     */
    @RabbitListener(queues = QueueConstantUtil.PUBLISH_SYSTEM_BULLETIN)
    public void sysNotice(PublishSystemBulletin publishSystemBulletin) {
       try {
           log.info("接收到的参数：{}", publishSystemBulletin);
           if (null == publishSystemBulletin) {
               log.info("入参为空，终止操作");
               return;
           }
           if (null == publishSystemBulletin.getType()) {
               log.info("类型为空，无法执行");
               return;
           }
           //需要推送消息
           if (1 == publishSystemBulletin.getType()) {
               //获取所有的用户
               Result allUserId = userFeign.findAllUserId(AccountType.PERSONAGE.value());
               if (!allUserId.isResult() || null == allUserId.getData()) {
                   log.info("为获取到用户id信息，错误信息：{}", allUserId.getMsg());
                   return;
               }
               List<String> ids = (List<String>) allUserId.getData();
               //用于保存到数据的数据列表
               List<SystemMessage> systemMessagelist = new ArrayList<SystemMessage>();
               //遍历id
               for (String id : ids) {
                   SystemMessage systemMessage = SystemMessage.builder().type(0).isRead(0).userId(Long.valueOf(id))
                           .extendId(publishSystemBulletin.getId())
                           .subtitle(publishSystemBulletin.getTitle()).createTime(publishSystemBulletin.getCreateTime()).build();
                   systemMessagelist.add(systemMessage);
               }
               log.info("需要存储的数据：{}", systemMessagelist);
               //保存到数据库
               systemMessageService.saveBatch(systemMessagelist);
               //推送给所有用户
               jPushClientExample.sendMsg(ids, publishSystemBulletin.getTitle(), publishSystemBulletin.getTitle(),
                       publishSystemBulletin.getTitle(), JpushTypeEnum.PASSTHROUGH.value(), null,5);
           } else if (2 == publishSystemBulletin.getType()) {
               //执行更新操作
               if (null == publishSystemBulletin.getId()) {
                   log.info("公告id为空，无法继续执行！");
                   return;
               }
               //执行更新操作
               systemMessageService.update(new UpdateWrapper<SystemMessage>().set("subtitle", publishSystemBulletin.getTitle())
                       .eq("extend_id", publishSystemBulletin.getId()).eq("type", 0));
           }

       }catch (Exception e){
           log(e,publishSystemBulletin,QueueConstantUtil.PUBLISH_SYSTEM_BULLETIN);
       }

    }


    private void log(Exception e,Object o,String queue){
        log.error(" ----------------start:队列名称："+queue+"------------------");
        log.error("队列发生错误，队列名称:{}",queue);
        log.error("错误消息：{}",e.getMessage());
        log.error("接受到的参数为：{}",JSONObject.toJSONString(o));
        log.error(" ---------------- end ------------------");
    }
//    private String getTag(String userId){
//        JSONObject tag = JSONObject.parseObject((String) redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN + "10000003"));
//        return  tag.getString("tag");
//    }
//
//    private List<String> getTagList(List<String> userids){
//        List<String> tagList=new ArrayList<>();
//        for (String userId:userids) {
//            tagList.add(getTag(userId));
//        }
//        return tagList;
//    }
}
