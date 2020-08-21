package cn.meiot.entity.equipment.sckgxx;

import java.util.List;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: Status1.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月15日
 */
@Data
public class Timer{
	/**
	 * 定时组序号，0-4组，最多5组
	 */
	private Integer num;
	/**
	 * 定时开关模式，1为时间段起止模式，2为星期重复模式，0为关闭
	 */
	private Integer mode;
	/**
	 * 开始时间戳
	 */
	private Long start;
	/**
	 * 结束时间戳
	 */
	private Long end;
	/**
	 * 星期重复允许，从星期一到星期天,number类型，0：禁止，1：允许
	 */
	private List<Integer> cycle;
	
}

