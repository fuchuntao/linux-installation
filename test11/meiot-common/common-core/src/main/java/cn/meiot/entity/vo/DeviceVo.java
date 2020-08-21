package cn.meiot.entity.vo;

import java.io.Serializable;

import cn.meiot.entity.vo.EmailVo.EmailVoBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
	

}
