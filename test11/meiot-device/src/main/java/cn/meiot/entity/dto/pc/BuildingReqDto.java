package cn.meiot.entity.dto.pc;

import lombok.Data;

@Data
public class BuildingReqDto {
	/**
	 * 故障数量
	 */
	private Integer faultNum = 0;
	/**
	 *断网数量 
	 */
	private Integer unNetNum = 0;
	
}
