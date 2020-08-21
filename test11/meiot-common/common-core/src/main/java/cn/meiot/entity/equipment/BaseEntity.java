package cn.meiot.entity.equipment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: BaseEntity.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月14日
 */
@Data
public class BaseEntity<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 消息ID，随机数，每发送一条消息后变化
	 */
	private Long messageid;
	
	/**
	 * linux时间戳，无毫秒位
	 */
	private Long  timestamp = System.currentTimeMillis()/1000;
	
	/**
	 * 设备的序列号，0为广播
	 */
	private String deviceid;
	
	/**
	 * 指令编号
	 */
	private String cmd;
	/**
	 * 消息内容
	 */
	private Map desired = new HashMap();

	public BaseEntity() {
		super();
	}
	
	public BaseEntity( String deviceid, String cmd, List<T> desired) {
		super();
		this.messageid = new Long(RandomStringUtils.randomNumeric(5));
		this.deviceid = deviceid;
		this.cmd = cmd;
		this.desired.put("arrays", desired);
	}
	
	public BaseEntity( String deviceid, String cmd, Map map) {
		super();
		this.messageid = new Long(RandomStringUtils.randomNumeric(5));
		this.deviceid = deviceid;
		this.cmd = cmd;
		this.desired = map;
	}

}
