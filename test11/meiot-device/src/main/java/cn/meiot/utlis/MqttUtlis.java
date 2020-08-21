package cn.meiot.utlis;

import cn.meiot.utils.MqttUtil;

/**
 * @author lingzhiying
 * @title: MqttUtlis.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月14日
 */
public class MqttUtlis {
	
	private MqttUtlis() {}
	
	public static String getCmd(Integer flag){
		switch (flag){
			/*case 1:
			 return MqttUtil.CMD_03;*/
			case 2:
				return MqttUtil.CMD_05;
			default: return  MqttUtil.CMD_03;
		}
	}

}
