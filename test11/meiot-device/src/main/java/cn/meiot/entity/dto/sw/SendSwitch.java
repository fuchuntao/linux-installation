package cn.meiot.entity.dto.sw;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lingzhiying
 * @title: SendSwitch.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月4日
 */
@Data
public class SendSwitch {
	private String serialNumber;
	private Integer status;
	private Long switchSn;
	private Integer index;
	private Long userId;
	private Integer loadMax;
	public void setSwitchIndex(Integer switchIndex) {
		this.index = switchIndex;
	}
	public void setSwitchSn(String switchSn){
		if(StringUtils.isEmpty(switchSn)){
			return;
		}
		this.switchSn = Long.valueOf(switchSn);
	}
	public void setSwitchSn(Long switchSn){
		this.switchSn = switchSn;
	}
}
