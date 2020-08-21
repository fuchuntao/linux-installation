package cn.meiot.entity.dto.pc.examination;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PcExamination {
	/**
	 *
	 */
	private Long equipmentId;
	/**
	 * 开启状态
	 */
	private Integer examinationStatus;
	/**
	 * 自检时间
	 */
	private String examinationTime;
	/**
	 * 设备号
	 */
	private String serialNumber;
	/**
	 * 设备名
	 */
	private String name;
	/**
	 * 组织架构
	 */
	private Long buildingId;
	/**
	 * 最新自检时间
	 */
	 @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**
	 * 漏电状态 0:漏电 1正常
	 */
	private Integer swicthStatus = 1 ;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 开关列表
	 */
	private List<SwitchDto> switchDtoList;
}
