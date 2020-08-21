package cn.meiot.entity.vo;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class SwitchTypeVo {
	private Long id;
	private String name;
	private List<SwitchVo> listSwitchVo;
	private BigDecimal meter;
}
