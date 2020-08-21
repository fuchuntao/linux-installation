package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ActionLog implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/**
	 * 管理员ID
	 */
	private Long userId;

	/**
	 * 用户账号
	 */
	private String username;

	/**
	 * 操作模块
	 */
	private String actionModel;

	/**
	 * 请求url
	 */
	private String url;

	/**
	 * 日志内容
	 */
	private String content;

	/**
	 * 参数
	 */
	private String param;

	/**
	 * IP
	 */
	private String ip;

	/**
	 * 客户端
	 */
	private String useragent;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		this.formatCreateTime = df.format(createTime);
	}

	@TableField(exist = false)
	private String formatCreateTime;

	/**
	 *   日志属于？
	 *   账户类型 1 运营 2 企业 3 代理商 4 维修 5 个人
	 */
	private Integer type;

	/**
	 * 作为企业日志的时候需要加账号主id
	 */
	private Long mainUserId;

	/**
	 * 用户昵称
	 */
	private String name;


}
