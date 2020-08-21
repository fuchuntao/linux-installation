package cn.meiot.entity.dto;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: Equipment.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月14日
 */
@Data
public class EquipmentDto {
	//设备号
	private String serialNumber;
	//index
	private Integer index;
	//是否开关  1开  0关
	private byte status;
	//用户id
	private Long userId;
}
