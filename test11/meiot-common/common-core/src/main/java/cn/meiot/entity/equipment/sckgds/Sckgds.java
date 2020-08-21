package cn.meiot.entity.equipment.sckgds;

import java.io.Serializable;
import java.util.List;

import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.Timeswitch;
import lombok.Data;

/**
 * @author lingzhiying
 * @title: Sckgds.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月15日
 */
@Data
public class Sckgds implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 设备信息
	 */
	private Device device;
	/**
	 * 定时开关参数
	 */
	private List<Timeswitch> timer;
}
