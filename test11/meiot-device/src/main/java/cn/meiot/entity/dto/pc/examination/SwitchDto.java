package cn.meiot.entity.dto.pc.examination;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class SwitchDto {
	private String switchSn;
	private String switchName;
	private Integer switchIndex;
	private Integer status = 0;
	private Object temp = BigDecimal.ZERO;
	private List<Integer> numList;
	private Integer num;
}
