package cn.meiot.entity.vo;

import cn.meiot.config.TableConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParametersDto{
	/**
	 * 最高日用电量（0）
	 */
	private Integer state;
	private String function;
	private Long switchSn;
	private Integer projectId;
	private Long userId;
	/**
	 * 保留小数
	 */
	private Integer decimal = 1 ;
	/**
	 * 0年
	 * 1月
	 * 2日
	 */
	private Integer type;
	/**
	 * 时间戳
	 */
	private Long time;
	private String tableName;
	/**
	 * 年月日表后缀
	 */
	private String timeName;
	/**
	 * 同年表后缀
	 */
	private String nowTimeName;
	private Integer years;
	private Integer months;
	private Integer hours;
	public String getTimeName() {
		if(type == null){
			return TableConfig.HOURS;
		}
		if(type == 0) {
			return TableConfig.YEARS;
		}
		if(type == 1) {
			return TableConfig.MONTHS;
		}
		return TableConfig.HOURS;
	}
	public String getNowTimeName() {
		if(type == null){
			return TableConfig.HOURS;
		}
		if(type == 0) {
			return TableConfig.MONTHS;
		}
		return TableConfig.HOURS;
	}

	private List<SerialNumberMasterVo> switchSnList;


	/**
	 * 电量 1， 电流 2， 温度 3
	 */
	private Integer typeSwitch;

	/**
	 * 平台 app,pc
	 */
	private String platform;


	/**
	 * 设备号
	 */
	private String serialNumber;
	/**
	 * index
	 */
	private Integer switchIndex;
	/**
	 * 旧的开关号   被替换的
	 */
	private Long oldSwitchSn;
	/**
	 * 新的开关号   替换旧的
	 */
	private Long newSwitchSn;

	private Integer sYear;

	private Integer sMonth;

	private Integer sDay;

	/**
	 * 时间（单位：小时）
	 */
	private Integer sTime;


	/**
	 * 数据
	 */
	private BigDecimal data;

	private Long id;

	/**
	 * 创建时间
	 */
	private String createTime;


//	public String getPlatform() {
//		if(projectId > 0) {
//			return TableConfig.PC;
//		}
//		return TableConfig.APP;
//	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
		if(projectId != null) {
			if(!this.tableName.equals(TableConfig.METER)){
				this.platform = TableConfig.PC;
				return;
			}
			if(projectId > 0) {
				this.platform = TableConfig.PC;
				return;
			}
			this.platform = TableConfig.APP;
			return;
		}
	}
}
