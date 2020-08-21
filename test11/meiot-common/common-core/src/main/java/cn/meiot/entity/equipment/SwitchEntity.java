package cn.meiot.entity.equipment;

import java.io.Serializable;

/**
 * @author lingzhiying
 * @title: SwitchEntity.java
 * @projectName spacepm
 * @description:  111
 * @date 2019年8月15日
 */
public class SwitchEntity implements Serializable {
	/**
	 * 远程控制合分闸
	 */
	protected Integer switch1;
	
	public Integer getSwitch() {
		return switch1;
	}
	public void setSwitch(Integer switch1) {
		this.switch1 = switch1;
	}
}
