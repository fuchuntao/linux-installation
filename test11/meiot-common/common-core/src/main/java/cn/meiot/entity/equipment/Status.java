package cn.meiot.entity.equipment;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class Status extends SwitchEntity implements Serializable {
	/**
	 * 功率，单位W
	 */
	private Integer power;
	/**
	 * 最大功率，单位W
	 */
	private Integer loadmax;
	/**
	 * 温度，单位摄氏度
	 */
	private Integer temp;
	/**
	 * 最高温度，单位摄氏度
	 */
	private Integer tempmax;
	/**
	 * 当日计量电量，单位W.h
	 */
	private BigDecimal meterd;
	/**
	 * 当月计量电量，单位W.h
	 */
	private BigDecimal meterm;
	/**
	 * auto
	 */
	private Integer auto;
	/**
	 * 漏电流，单位mA
	 */
	private Integer leakage;
	/**
	 * 事件0	无事件
		1	漏电保护
		2	过温提醒
		3	过温保护
		4	过载保护
		5	短路保护
		6	过压保护
		7	欠压保护
		8	手动分闸
	 */
	private List<Integer> event;
	/**
	 * 各相电流，单位mA
	 */
	private List<Float> current;
	/**
	 * 各相电压，单位mV
	 */
	private List<Float> voltage;

	/**
	 * 时间点
	 */
	private Integer time;

	/**
	 * 电量值
	 */
	private BigDecimal meterh;

	/**
	 * 上一天的电量
	 */
	private BigDecimal meterdlast;
	/**
	 * 0:主动上传,1表示小时,2:日,3:月
	 */
	private Integer flag;
}

