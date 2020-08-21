package cn.meiot.entity.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import cn.meiot.entity.vo.AppMeterVo.AppMeterVoBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
	private Integer year;

    private Integer month;

    private Integer day;
    
    private String tableName;
    
    /**
	 * 设备序列号
	 */
	private String serialNumber;

	/**
	 * 开关序号
	 */
	private Integer switchIndex;

	/**
	 * 开关编号
	 */
	private Long switchSn;

	/**
	 * 数据
	 */
	private BigDecimal data;


	/**
	 * 主账户id
	 */
	private Integer userId;

	/**
	 * 项目id
	 */
	private Long projectId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StatisticsDto)) return false;
		StatisticsDto that = (StatisticsDto) o;
		return Objects.equals(year, that.year) &&
				Objects.equals(month, that.month) &&
				Objects.equals(day, that.day) &&
				Objects.equals(serialNumber, that.serialNumber) &&
				Objects.equals(switchIndex, that.switchIndex) &&
				Objects.equals(switchSn, that.switchSn) &&
				Objects.equals(userId, that.userId) &&
				Objects.equals(projectId, that.projectId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(year, month, day, serialNumber, switchIndex, switchSn, userId, projectId);
	}
}
