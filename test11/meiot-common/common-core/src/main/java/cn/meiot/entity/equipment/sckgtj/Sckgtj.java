package cn.meiot.entity.equipment.sckgtj;

import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.Meter;
import lombok.Data;

/**
 * @author lingzhiying
 * @title: sckgtj.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月15日
 */
@Data
public class Sckgtj {
	/**
	 * 设备信息
	 */
	private Device device;
	/**
	 * 
	 */
	private Meter meter;
}
