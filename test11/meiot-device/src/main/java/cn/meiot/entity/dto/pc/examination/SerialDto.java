package cn.meiot.entity.dto.pc.examination;

import java.util.List;

import lombok.Data;

@Data
public class SerialDto {
	private String serialNumber;
	//设备类型
	private Integer type;
	private Long id;
	private String name;
	private Integer status = 0 ;
	private Long buildingId;
	private List<SwitchDto> listSwitch;
}
