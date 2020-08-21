package cn.meiot.entity.dto;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: EquipmentSwitchDto.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月19日
 */
@Data
public class EquipmentSwitchDto {
	/**
	 * 名字
	 */
	private String name;
	/**
	 * 第几个开关
	 */
	private Integer index;
}
