package cn.meiot.service;

import cn.meiot.entity.db.PcTimerMode;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.time.PcTimerModerDto;
import cn.meiot.entity.vo.Result;

import java.util.List;

public interface PcTimerModeService {

	/**
	 * 分页查询情景模式
	 * @param cond
	 * @return
	 */
	Result query(PcEquipmentUserCond cond);

	/**
	 * 查询情景模式点击详情
	 * @param id
	 * @param userId
	 * @param projectId
	 * @param currentUserId 
	 * @return
	 */
	Result queryById(Long id, Long userId, Integer projectId, Long currentUserId);

	/**
	 * 添加情景模式
	 * @param pcTimerModerDto
	 * @return
	 */
	Result insert(PcTimerModerDto pcTimerModerDto);

	/**
	 * 修改情景模式
	 * @param pcTimerModerDto
	 * @return
	 */
	Result update(PcTimerModerDto pcTimerModerDto);

	/**
	 * 删除情景模式
	 * @param pcTimerMode
	 * @return
	 */
	Result delete(PcTimerMode pcTimerMode);

	/**
	 * 获取开关定时信息
	 * @param cond
	 * @return
	 */
	Result querySwitch(PcEquipmentUserCond cond);

	/**
	 * 添加情景模式
	 */

	public void inserTimerEqAndSw(PcTimerModerDto pcTimerModerDto, Long id,String table);

	/**
	 * 全删
	 * @param id
	 */
	public void deleteTimer(Long id);

	//public void OnTimer(Object pcTimerMode, List<String> listSwitch);

	/**
	 * 分页查询开关列表
	 * @param cond
	 * @return
	 */
	Result querySwitchList(PcEquipmentUserCond cond);

	/**
	 * 鉴权
	 */
	void authentication(Long id,Integer projectId);

	/**
	 * 查询开关设备
	 * @param cond
	 * @return
	 */
	Result queryEquipment(PcEquipmentUserCond cond);

	/**
	 *
	 * @param pcTimerMode
	 * @return
	 */
    Result isSwitch(PcTimerMode pcTimerMode);

	/**
	 * 是否已有执行开关
	 * @param id
	 * @return
	 */
	Result isImplement(Long id,List<String> switchSn,Integer projectId);
}
