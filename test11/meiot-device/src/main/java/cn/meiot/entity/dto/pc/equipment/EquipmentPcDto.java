package cn.meiot.entity.dto.pc.equipment;

import lombok.Data;

@Data
public class EquipmentPcDto {
	/**
	 * 最大功率
	 */
	private Integer loadmax;
	/**
	 * id
	 */
	private Long id;
	/**
	 * 设备号
	 */
	private String serialNumber;
	/**
	 * 开关状态  0 关  1开
	 */
	private Integer isSwitch;
	/**
	 * 设备名
	 */
	private String serialName;
	/**
	 * 联网状态
	 */
	private Integer isOnline;
	/**
	 * 故障数量
	 */
	private int faultNum;
}
