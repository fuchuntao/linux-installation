package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadDataDto {
	private String serialNumber;

	private Integer switchIndex;

	private Long switchSn;

	private Integer year;

	private Integer month;

	private Integer day;

	private Integer hour;

	private Integer minute;

	private Long userId;

	private Long userType;

	private String createTime;

	private BigDecimal data;

	private Long oldData;


}
