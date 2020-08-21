package cn.meiot.entity.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class Crcuit implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 总负载
	 */
	private BigDecimal totalLoad;
	/**
	 * 过流预警
	 */
	private BigDecimal current;
	/**
	 * 过流报警
	 */
	@Column(name="current_wa")
	private BigDecimal currentWA;
	/**
	 * 过压预警
	 */
	private BigDecimal voltage;
	/**
	 * 过压报警
	 */
	@Column(name="voltage_wa")
	private BigDecimal voltageWA;
	/**
	 * 欠压预警
	 */
	private BigDecimal underVoltage;
	/**
	 * 欠压报警
	 */
	@Column(name="under_voltage_wa")
	private BigDecimal underVoltageWA;
	/**
	 * 功率预警
	 */
	private BigDecimal power;
	/**
	 * 功率报警
	 */
	@Column(name="power_wa")
	private BigDecimal powerWA;
	/**
	 * 漏电预警
	 */
	private BigDecimal leakage;
	/**
	 * 漏电报警
	 */
	@Column(name="leakage_wa")
	private BigDecimal leakageWA;
	/**
	 * 温度预警
	 */
	private BigDecimal temp;
	/**
	 * 温度报警
	 */
	@Column(name="temp_wa")
	private BigDecimal tempWA;

	/**
	 * 开关型号
	 */
	private String mode;
	/**
	 * 项目id
	 */
	@Id
	private Integer projectId;
}
