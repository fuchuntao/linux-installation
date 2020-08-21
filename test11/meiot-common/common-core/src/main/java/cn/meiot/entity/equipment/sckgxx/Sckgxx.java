package cn.meiot.entity.equipment.sckgxx;


import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.Status;

/**
 * @author lingzhiying
 * @title: sckgxx.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月15日
 */

import lombok.Data;

@Data
public class Sckgxx {
	
	/**
	 * 开关的设备信息
	 */
	private Device device;
	/**
	 * 开关的状态参数
	 */
	private Status status;
}
