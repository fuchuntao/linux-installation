package cn.meiot.entity.equipment.kgcs;

import cn.meiot.entity.equipment.SwitchEntity;
import lombok.Data;

/**
 * @author lingzhiying
 * @title: Status1.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月15日
 */
@Data
public class Status1 extends SwitchEntity{
	/**
	 * 最大功率，单位W
	 */
	private Integer loadmax;
	/**
	 * 最高温度
	 */
	private Integer tempmax;
	
	/**
	 * 远程控制漏电自检测
	 */
	private Integer test;
	/**
	 * auto
	 * @return
	 */
	private Integer auto;
	
}
