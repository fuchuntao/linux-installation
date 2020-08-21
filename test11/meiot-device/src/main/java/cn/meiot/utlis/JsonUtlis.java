package cn.meiot.utlis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.meiot.entity.db.Switch;
import cn.meiot.entity.device.DeviceBase;
import cn.meiot.entity.device.Payload;
import cn.meiot.entity.device.SckData;
import cn.meiot.entity.equipment.Device;

/**
 * @author lingzhiying
 * @title: JsonUtlis.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月31日
 */
public class JsonUtlis {
	private JsonUtlis() {}
	
	/**
	 * 
	 * @param json
	 * @return
	 */
	public static Map getCMD104(String json){
		DeviceBase bean = JSONObject.parseObject(json, DeviceBase.class);
		Map map = new HashMap();
		Payload payload = bean.getPayload();
		if(payload.getDesired().getArrays().get(0).getStatus().getEvent().get(0) == 0) {
			map.put("event", true);
		}else {
			map.put("event", false);
			return map;
		}
		//获取设备号
		String serialNumber = payload.getDeviceid();
		map.put("serialNumber", serialNumber);
		//定义设备信息
		List<SckData> arrays = payload.getDesired().getArrays();
		Switch swq = null;
		//Examination ex =  null;
		for (SckData sckData : arrays) {
			//定义开关
			swq =  new Switch();
			//获取开关信息
			Device device = sckData.getDevice();
			//添加开关编号
			swq.setSwitchSn(device.getId().toString());
			swq.setSerialNumber(serialNumber);
			swq.setSwitchModel(device.getMode());
			swq.setSwitchIndex(device.getIndex());
			map.put("switch",swq);
			/*ex = new Examination();
			ex.set*/
		}
		return map;
	}
}
