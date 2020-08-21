package cn.meiot.entity.equipment;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lingzhiying
 * @title: Device.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月15日
 */
@Data
public class Device implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 开关在组合中的序号，从1开始
	 */
	private Integer index;
	/**
	 * 开关的型号
	 */
	private String mode;
	/**
	 * 开关的唯一ID，32位
	 */
	private Long id;
}
