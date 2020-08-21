package cn.meiot.entity.equipment;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lingzhiying
 * @title: Status1.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月15日
 */
@Data
public class Meter implements Serializable {
	/**
	 * 月份
	 */
	private Integer month;
	/**
	 * 值
	 */
	private BigDecimal value;
	
}

