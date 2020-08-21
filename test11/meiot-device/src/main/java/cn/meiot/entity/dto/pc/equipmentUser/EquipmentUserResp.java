package cn.meiot.entity.dto.pc.equipmentUser;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

//import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class EquipmentUserResp {
	/**
	 * 设备号
	 */
	private String serialNumber;
	/**
	 * 模型
	 */
	private String model;
	/**
	 * 开关数
	 */
	private Integer switchCount;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 用户名
	 */
	private String userPhone;
	/**
	 * 绑定数量
	 */
	private Integer userCount ;
	/**
	 * 企业 名
	 */
	private Integer projectId;
	/**
	 * 设备名
	 */
	private String serialName;
	/**
	 * 企业名称
	 */
	private String enterpriseName;
	/**
	 * 项目名称
	 */
	private String projectName;
	/**
	 * 有效期
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date time = new Date();
}
