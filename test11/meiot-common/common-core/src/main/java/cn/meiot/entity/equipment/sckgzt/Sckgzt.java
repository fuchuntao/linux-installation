package cn.meiot.entity.equipment.sckgzt;

import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.Status;
import lombok.Data;

import java.util.Map;

/**
 * @author lingzhiying
 * @title: sckgzt.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月15日
 */
@Data
public class Sckgzt {
	private Device device;
	private Status status;

	public Sckgzt (){}
	public Sckgzt (Device device,Status status){
		this.device = device;
		this.status = status;
	}

}
