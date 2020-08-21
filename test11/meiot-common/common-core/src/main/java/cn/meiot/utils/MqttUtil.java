package cn.meiot.utils;

import cn.meiot.config.CmdConstart;
import cn.meiot.entity.equipment2.Switch2Entity;
import cn.meiot.entity.equipment2.upswitch.Switchd;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.JsonArray;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author lingzhiying
 * @title: MqttUtil.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月4日
 */
@Data
public class MqttUtil {
	/**
	 * 设置开关参数
	 */
	public static final String CMD_03 = "CMD-03";
	/**
	 * 固件升级命令
	 */
	public static final String CMD_04 = "CMD-04";
	/**
	 * 设置定时参数
	 */
	public static final String CMD_05 = "CMD-05";
	/**
	 * 设备控制和查询
	 */
	public static final String CMD_01 = "CMD-01";
	/**
	 * 查询通讯网关参数应答
	 */
	public static final String CMD_101 = "CMD-101";
	/**
	 * 上传开关的全部信息
	 */
	public static final String CMD_104 = "CMD-104";
	/**
	 * 上传开关的状态信息
	 */
	public static final String CMD_105 = "CMD-105";
	/**
	 * 上传开关的定时信息
	 */
	public static final String CMD_107 = "CMD-107";
	/**
	 * 开关基本信息上传
	 */
	public static final String CMD_108 = "CMD-108";
	/**
	 * 上传开关的统计信息
	 */
	public static final String CMD_106 = "CMD-106";

	public static String findSerialNumber(Map map){
		//设备号
		String clientid = (String) map.get("clientid");
		return clientid;
	}

	public static Long findTime(Map map){
		//设备号
		Long clientid = Long.valueOf((Integer) map.get("created_time"));
		return clientid;
	}

	public static String findVer(Map map){
		//设备号
		map  = (Map) map.get("payload");
		String version = (String) map.get("ver");

		return version;
	}

	/*public static String findModel(Map map){
		//设备号
		String clientid = (String) map.get("clientid");

		return clientid;
	}*/

	/*public static void main(String[] args) {
		//String message = "{\"clientid\":\"P2202005240010\",\"topic\":\"P2202005240010\\/event\\/raw\",\"payload\":{\"messagemd\":5649,\"cmd\":\"CMD-210\",\"resultinfo\":0},\"qos\":0,\"raw_packet_id\":0,\"is_retain\":false,\"is_resend\":false,\"is_will\":false,\"ip\":\"113.102.165.2\",\"created_time\":1590460968}";
		String message = "{\"clientid\":\"P2202005240010\",\"topic\":\"P2202005240010\\/event\\/raw\",\"payload\":{\"messageid\":316681,\"cmd\":\"CMD-210\",\"switchlist\":[{\"sid\":860116442,\"index\":3}]},\"qos\":0,\"raw_packet_id\":0,\"is_retain\":false,\"is_resend\":false,\"is_will\":false,\"ip\":\"113.102.165.2\",\"created_time\":1590461281}";
		Map parseObject = JSON.parseObject(message,Map.class);
		List<Switchd> data2 = findData2(parseObject,Switchd.class);
		data2.get(0).setIndex(1);
		System.out.println(data2);
	}*/

	/**
	 * 二代设备
	 * @param t
	 * @param map
	 * @param name
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> findData2(Map map,Class t){
		map  = (Map) map.get("payload");
		Integer resultinfo = (Integer) map.get("resultinfo");
		if(resultinfo != null && resultinfo.equals(0)){
			return null;
		}
		String cmd = (String)map.get("cmd");
		String name = null;
		if(StringUtils.isEmpty(cmd)){
			return null;
		}
		name = CmdConstart.findValue(cmd);
		JSONArray data = (JSONArray) map.get(name);
		return data.toJavaList(t);
	}



}
