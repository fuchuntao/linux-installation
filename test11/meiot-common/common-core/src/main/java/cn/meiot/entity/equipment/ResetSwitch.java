package cn.meiot.entity.equipment;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ResetSwitch implements Serializable{
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
	/**
	 * 开关状态
	 */
	private Status status;
}
