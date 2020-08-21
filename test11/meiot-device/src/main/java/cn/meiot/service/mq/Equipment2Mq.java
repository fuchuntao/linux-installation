package cn.meiot.service.mq;

import cn.meiot.constart.RabbitConstart;
import cn.meiot.constart.RedisConstart;
import cn.meiot.dao.EquipmentApiMapper;
import cn.meiot.entity.equipment2.examination.CheckResult;
import cn.meiot.entity.equipment2.upstatus.Sl;
import cn.meiot.entity.equipment2.upswitch.Switchd;
import cn.meiot.entity.equipment2.upwarn.WarnInfo;
import cn.meiot.entity.openapi.CallBackEntity;
import cn.meiot.enums.CallbackEnum;
import cn.meiot.service.EquipmentService;
import cn.meiot.service.ExaminationService;
import cn.meiot.service.apiservice.EquipmentApiService;
import cn.meiot.utils.MqttUtil;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Equipment2Mq {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private EquipmentApiService equipmentApiService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues= RabbitConstart.DEVICE_210)
    public void registerSerial(String message){
        try {
            Map parseObject = JSON.parseObject(message,Map.class);
            //设备号
            String serialNumber = MqttUtil.findSerialNumber(parseObject);
            //开关数组
            List<Switchd> data2 = MqttUtil.findData2(parseObject,Switchd.class);

            synchronized ("registerSerial"+serialNumber){
                equipmentService.insertEquipment2(serialNumber, data2);
            }
            Long appId = equipmentApiService.selectIdBySerialNumber(serialNumber);
            if(appId == null){
                return;
            }
            CallBackEntity build = CallBackEntity.builder().appId(appId).serialNumber(serialNumber).callbackEnum(CallbackEnum.SWITCH_VARIATION)
                    .data(JSONArray.toJSONString(data2)).build();
            String s = JSON.toJSONString(build);
            rabbitTemplate.convertAndSend(QueueConstantUtil.OpenApi.CALL_BACK_ADD_EQUIPMENT,"",s);
        }catch (Exception e){
            e.printStackTrace();
            log.error("开关数据上报异常----message:"+message);
        }
    }



    /**
     * 漏电自检
     * @param message
     */
    @RabbitListener(queues= RabbitConstart.DEVICE_221)
    public void examination(String message){
        try {
            Map parseObject = JSON.parseObject(message,Map.class);
            //设备号
            String serialNumber = MqttUtil.findSerialNumber(parseObject);
            Long time = MqttUtil.findTime(parseObject) * 1000L;
            //开关数组
            List<CheckResult> data2 = MqttUtil.findData2(parseObject, CheckResult.class);
            if(CollectionUtils.isEmpty(data2)){
                return;
            }
            examinationService.insert(serialNumber, data2,time);
            Long appId = equipmentApiService.selectIdBySerialNumber(serialNumber);
            if(appId == null){
                return;
            }
            CallBackEntity build = CallBackEntity.builder().appId(appId).serialNumber(serialNumber).callbackEnum(CallbackEnum.LEAKAGE_SELF_TEST)
                    .data(JSONArray.toJSONString(data2)).build();
            String s = JSON.toJSONString(build);
            rabbitTemplate.convertAndSend(QueueConstantUtil.OpenApi.CALL_BACK_ADD_EQUIPMENT,"",s);
        }catch (Exception e){
            //e.printStackTrace();
            log.error("漏电自检~~~:"+message);
        }
    }

    @RabbitListener(queues= RabbitConstart.DEVICE_208)
    public void updateVersion(String message){
        Map parseObject = JSON.parseObject(message,Map.class);
        //设备号
        String serialNumber = MqttUtil.findSerialNumber(parseObject);
        String ver = MqttUtil.findVer(parseObject);
        if(StringUtils.isEmpty(ver)){
            return;
        }
        try {
            equipmentService.updateVersion(serialNumber,ver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RabbitListener(queues= RabbitConstart.DEVICE_213)
    public void updateStatus(String message){
        updateFault(message);
    }

    @RabbitListener(queues= RabbitConstart.DEVICE_213)
    public void updateStatus2(String message){
        updateFault(message);
    }

    @RabbitListener(queues= RabbitConstart.DEVICE_216)
    public void updateStatus3(String message){
        updateFault(message);
    }

    @RabbitListener(queues= RabbitConstart.DEVICE_216)
    public void updateStatus4(String message){
        updateFault(message);
    }

    private void updateFault(String message){
        try {
            //System.out.println(message);
            Map parseObject = JSON.parseObject(message,Map.class);
            //设备号
            String serialNumber = MqttUtil.findSerialNumber(parseObject);
            List<Sl> data2 = MqttUtil.findData2(parseObject, Sl.class);
            String str1 = RedisConstart.DEVICE+serialNumber;
            //Long lastTime = System.currentTimeMillis();
            //查询设备是否被开放平台绑定
            Long appId = equipmentApiService.selectIdBySerialNumber(serialNumber);
            String serial = RedisConstart.DEVICE+serialNumber;
            for (Sl sl: data2) {
                Long switchSn = sl.getSid();
                Map map = sl.getMap();
            /*Object o =
            System.out.println(o);*/
           /* Map oldMap = (Map) redisTemplate.opsForHash().get(str1, switchSn.toString());
            Map map = sl.getMap();
            if(oldMap == null){
                oldMap = map;
            }else {
                oldMap.putAll(map);
            }*/
                redisTemplate.opsForValue().set(RedisConstantUtil.FAULT_SERIALNUMER + "_" + switchSn,sl.getFaultStatus(),7, TimeUnit.DAYS);
                //redisTemplate.opsForHash().put(str1,switchSn.toString(),oldMap);
                if(appId != null){
                    CallBackEntity build = CallBackEntity.builder().appId(appId).serialNumber(serialNumber).callbackEnum(CallbackEnum.LEAKAGE_SELF_TEST)
                            .data(JSON.toJSONString( redisTemplate.opsForHash().get(serial,switchSn.toString()))).build();
                    String s = JSON.toJSONString(build);
                    rabbitTemplate.convertAndSend(QueueConstantUtil.OpenApi.CALL_BACK_ADD_EQUIPMENT,"",s);
                }

            }
        }catch (Exception e){
            log.error("216--213 上传开关信息"+e);
        }

    }

    @RabbitListener(queues= RabbitConstart.DEVICE_215)
    public void updateStatus215(String message){
        try{
            Map parseObject = JSON.parseObject(message,Map.class);
            //设备号
            String serialNumber = MqttUtil.findSerialNumber(parseObject);
            List<WarnInfo> data2 = MqttUtil.findData2(parseObject, WarnInfo.class);
            String str1 = RedisConstart.DEVICE+serialNumber;
            //Long lastTime = System.currentTimeMillis();
            for (WarnInfo sl: data2) {
                Long switchSn = sl.getSid();
                redisTemplate.opsForValue().set(RedisConstantUtil.FAULT_SERIALNUMER + "_" + switchSn,sl.getFaultStatus(),7, TimeUnit.DAYS);
            }
            Long appId = equipmentApiService.selectIdBySerialNumber(serialNumber);
            if(appId == null){
                return;
            }
            CallBackEntity build = CallBackEntity.builder().appId(appId).serialNumber(serialNumber).callbackEnum(CallbackEnum.LEAKAGE_SELF_TEST)
                    .data(JSONArray.toJSONString(data2)).build();
            String s = JSON.toJSONString(build);
            rabbitTemplate.convertAndSend(QueueConstantUtil.OpenApi.CALL_BACK_EXAMINATION,"",s);
        }catch (Exception e){
            log.error("故障上报异常"+e);
        }


    }
}
