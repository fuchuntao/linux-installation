package cn.meiot.entity.dto.pc.time;

import java.util.List;

import cn.meiot.entity.db.PcTimerMode;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

@Data
public class PcTimerModerDto {
	private PcTimerMode pcTimerMode;
	/**
	 * 组织架构
	 */
	private List<Long> listBuilding ;
	/**
	 * 设备
	 */
	private List<Long> listEquiment ;
	/**
	 * 开关号
	 */
	private List<String> listSwitch ;


	public boolean checkList(){
		boolean b = CollectionUtils.isEmpty(listBuilding);
		if(!b) {
			return false;
		}
		boolean b2 = CollectionUtils.isEmpty(listEquiment);
		if(!b2) {
			return false;
		}
		boolean b3 = CollectionUtils.isEmpty(listSwitch);
		if(!b3) {
			return false;
		}
		return true;
	}
}
