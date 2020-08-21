package cn.meiot.entity.equipment;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.meiot.entity.equipment.sckgxx.Timer;
import cn.meiot.utils.TimerUtil;
import lombok.Data;

/**
 * @author lingzhiying
 * @title: Timeswitch.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月15日
 */
@Data
public class Timeswitch implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 定时开关组序号，0-4，最多5组
	 */
	private Integer num = 0;
	/**
	 * 定时开关模式，1为时间段起止模式，2为星期重复模式，3为时间点开，4为时间点关，0为禁止
	 */
	private int mode = 0;
	/**
	 * 定时开始时间戳
	 */
	private Long start;
	/**
	 * 结束end
	 */
	private Long end;
	/**
	 * 开关打开时间，时时分分  00.00
	 */
	private String on;
	/**
	 * 开关关闭时间，时时分分
	 */
	private String off;
	/**
	 * 星期重复允许，从星期一到星期天，number类型，0：禁止，1：允许
	 */
	private List<Integer> cycle;

	/**
	 * 定时最大功率，单位W
	 */
	private Integer loadmax;

	/**
	 * '1开关，2功率',
	 */
	private Integer flag = 1;
	public void setStart(String start) {
		if(null == start ||"".equals(start)) {
			return ;
		}
		this.start = TimerUtil.getTime(start);
	}
	public void setEnd(String end) {
		if(null == end || "".equals(end)) {
			return ;
		}
		this.end = TimerUtil.getTime(end)+86399L;
	}
	public void setStart(Long start) {
		if(start == null) {
			return ;
		}
		if(start > 9999999999L) {
			this.start = start/1000;
			return ;
		}
		this.start = start;
	}
	public void setEnd(Long end) {
		if(end == null) {
			return ;
		}
		if(end > 9999999999L) {
			this.end = end/1000;
			return ;
		}
		this.end = end;
	}
	public Long getEnd() {
		if(this.end == null) {
			//如果结束时间为空返回一个超长结束时间
			return 4726364363L;
		}
		return this.end;
	}
	public void setOn(String on) {
		if(StringUtils.isBlank(on)) {
			this.on = null;
			return;
		}
		this.on = on.replaceAll(":", ".");
	}
	public void setOff(String off) {
		if(StringUtils.isBlank(off)) {
			this.off = null;
			return;
		}
		this.off = off.replaceAll(":", ".");
	}
}
