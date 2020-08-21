package cn.meiot.service;

import cn.meiot.entity.db.PowerAppUser;
import cn.meiot.entity.db.TimerMode;
import cn.meiot.entity.vo.Result;

import java.util.Set;

/**
 * @author lingzhiying
 * @title: TimerModeService.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月2日
 */
public interface TimerModeService {

	Result querySn(String switchSn);

	Result insert(TimerMode timerMode);

	Result delete(Long id,Long userId);

	Result update(TimerMode timerMode);

	Result load(Long id);

	Result querySwitchSn(String switchSn,Long userId);

    Result isSwitch(TimerMode timerMode);

	Result querySerial(String serialNumber, Long userId);

	/**
	 * 修改失效线路
	 */
	void updateInvalidSwitch(Set<PowerAppUser> powerAppUserList,String serialNumber,Long id);
}
