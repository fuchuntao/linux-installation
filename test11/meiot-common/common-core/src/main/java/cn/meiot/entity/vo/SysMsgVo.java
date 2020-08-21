package cn.meiot.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 系统消息
 */
@Data
public class SysMsgVo implements Serializable {

	/**
	 * 需要推送的用户id
	 */
	private List<String> userId;

	/**
	 * 关联id（扩展用)
	 */
	private Long extendId;

	/**
	 * 设备序列号
	 */
	private String serialNumber;

	/**
	 * 设备名称
	 */
	private String serialName;

	/**
	 * 消息类型(0-系统公告,1-绑定信息)
	 */
	private Integer type;

	/**
	 * 消息子标题
	 */
	private String subtitle;

	/**
	 * 消息内容
	 */
	private String content;
	/**
	 * key: mainUser //主账户
	 * key: subUser //子账户
	 * key: subUserPhone
	 * key: subUserName
	 * key: mainUserPhone
	 * key: mainUserName
	 */
	private Map<String,String> extras;


	/**
	 * 处理结果   0:已拒绝 1：待处理  2：已同意
	 */
	private Integer dealStatus;



}
