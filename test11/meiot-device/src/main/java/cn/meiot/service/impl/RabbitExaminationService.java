package cn.meiot.service.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.meiot.dao.EquipmentMapper;
import cn.meiot.entity.db.Equipment;
import cn.meiot.utlis.EquipmentUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.meiot.constart.RabbitConstart;
import cn.meiot.service.EquipmentService;
import cn.meiot.utils.RedisConstantUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingzhiying
 * @title: RabbitExaminationService.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月21日
 */
@Component
@Slf4j
public class RabbitExaminationService {
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private EquipmentService equipmentService;

    @Autowired
    private EquipmentMapper equipmentMapper;
	
	
	@RabbitListener(queues=RabbitConstart.DEVICE_108)
	public void showMessage108(String message){
		Map parseObject = JSON.parseObject(message,Map.class);
		Map map = (Map) parseObject.get("payload");
		String serialNumber = (String) map.get("deviceid");
		JSONObject content = (JSONObject) map.get("desired");
		try {
			equipmentService.up108(serialNumber, content);
		}catch (Exception e) {
			log.error("108上传数据有误,上传数据:{},异常数据:{}",message,e.getMessage()) ;
		}
	}

	@RabbitListener(queues=RabbitConstart.DEVICE_101)
	public void showMessage101(String message){
        Map parseObject = JSON.parseObject(message,Map.class);
        Map map = (Map) parseObject.get("payload");
        String serialNumber = (String) map.get("deviceid");
        JSONObject content = (JSONObject) map.get("desired");
        //版本号存在
		try {
			String version = (String) content.get("firmware");
			if (StringUtils.isNotEmpty(version)) {
				Equipment equipment = equipmentMapper.selectByPrimaryKey(serialNumber);
				if (equipment == null) {
					equipment = new Equipment();
					equipment.setSerialNumber(serialNumber);
					equipment.setVersion(version);
					EquipmentUtils.setModelAndVoltage(equipment);
					equipmentMapper.insertSelective(equipment);
				} else {
					equipment.setVersion(version);
					equipmentMapper.updateByPrimaryKeySelective(equipment);
				}
			}
		}catch (Exception e){
			log.error("添加版本号异常:{}",message);
		}
	}


	@RabbitListener(queues=RabbitConstart.DEVICE_104)
	public void showMessage(String message){
		try {
			up104(message);
		}catch (Exception e){

		}
	}
	
	@RabbitListener(queues=RabbitConstart.DEVICE_104)
	public void showMessage2(String message){
		try {
			up104(message);
		}catch (Exception e){

		}
	}
	
	@RabbitListener(queues=RabbitConstart.DEVICE_104)
	public void showMessage3(String message){
		try {
			up104(message);
		}catch (Exception e){

		}
	}

	/*public static void main(String[] args) {
		String str = "{\"clientid\":\"M2202003200003\",\"topic\":\"M2202003200003\\/event\\/raw\",\"payload\":{\"messageid\":12099114,\"timestamp\":1587140996,\"cmd\":\"CMD-104\",\"deviceid\":\"M2202003200003\",\"desired\":{\"arrays\":[{\"device\":{\"index\":5,\"mode\":\"C32\",\"id\":1912300202},\"status\":{\"flag\":3,\"time\":3,\"meterm\":0}}]}},\"qos\":0,\"raw_packet_id\":0,\"is_retain\":false,\"is_resend\":false,\"is_will\":false,\"ip\":\"113.89.96.147\",\"created\":{\"$date\":{\"$numberLong\":\"1587141004526\"}}}";
		up104(str);
	}*/

	private void up104(String message) {
		try{
		Map parseObject = JSON.parseObject(message,Map.class);
		Map map = (Map) parseObject.get("payload");
		//String serialNumber = (String) map.get("deviceid");
		JSONObject content = (JSONObject) map.get("desired");
		JSONArray jsonArray = content.getJSONArray("arrays");
		jsonArray.forEach(map2->{
			Map data =  (Map)map2;
			JSONObject device = (JSONObject)data.get("device");
			String switchSn = device.getString("id");
			JSONObject status = (JSONObject)data.get("status");
			JSONArray jsonArray2 = status.getJSONArray("event");
			if(jsonArray2 == null){
				return;
			}
			for (Object object : jsonArray2) {
				String event = object.toString();
				if("8".equals(event)) {
					redisTemplate.opsForValue().set(RedisConstantUtil.FAULT_SERIALNUMER + "_" + switchSn,1,7, TimeUnit.DAYS);
					log.info("104MQ---------设置开关:{},开关状态:{}",switchSn,status.getInteger("switch"));
					//return;
				}else if("9".equals(event) || "10".equals(event) || "11".equals(event) || "12".equals(event)){
					redisTemplate.opsForValue().set(RedisConstantUtil.FAULT_SERIALNUMER + "_" + switchSn,0,7, TimeUnit.DAYS);
				}
			}
		});
		}catch (Exception e) {
			log.error("104MQ---------参数解析异常:{},异常信息",message,e.getMessage());
		}
	}
	
}
