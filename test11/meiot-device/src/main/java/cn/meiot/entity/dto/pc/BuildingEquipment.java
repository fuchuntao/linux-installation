package cn.meiot.entity.dto.pc;

import lombok.Data;

import java.io.Serializable;

@Data
public class BuildingEquipment implements Serializable {
	/**
	 *  状态  0:完全关闭  1:完全开启  2:部分开启  3:未安装
	 */
	private Integer openStatus = 3;
	/**
	 * 故障状态 0:故障  1:未故障
	 */
	private Integer faultStatus = 1;
	/**
	 * 联网状态 0:未联网  1:联网
	 */
	private Integer networkingStatus = 1;
}
