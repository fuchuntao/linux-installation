package cn.meiot.entity.dto;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: EquipmentRespDto.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月16日
 */
@Data
public class EquipmentRespDto {
	/**
	 * 组织架构id
	 */
	private Long buildingId;
	/**
	 * 型号
	 */
	private String model;
	/**
	 * 设备号
	 */
	private String serialNumber;
	/**
	 * 额定电压
	 */
	private Float voltage;
	/**
	 * 主账户
	 */
	private Long userId;
	/**
	 * 状态
	 */
	private Integer userStatus;
	/**
	 * 用户名
	 */
	private String userPhone;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 设备默认图片
	 */
	private String image;
}
