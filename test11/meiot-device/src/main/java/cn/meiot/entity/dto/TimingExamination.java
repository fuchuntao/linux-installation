package cn.meiot.entity.dto;

import lombok.Data;

@Data
public class TimingExamination {
	private String serialNumber;
	private String time;
	private String switchSn;
	private Integer switchIndex;
	private Integer parentIndex;
	public Integer getSwitchSn() {
		if(this.switchSn == null) {
			return null;
		}
		return new Integer(this.switchSn);
	}
}
