package cn.meiot.service;

import cn.meiot.entity.dto.TimingExamination;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.equipment2.examination.CheckResult;
import cn.meiot.entity.vo.Result;

import java.util.List;

public interface ExaminationService {

	/**
	 * 获取条件内漏电自检列表
	 * @param cond
	 * @return
	 */
	Result query(PcEquipmentUserCond cond);

	/**
	 * 根据设备查询自检
	 * @param cond
	 * @return
	 */
	Result queryBySerialNumber(PcEquipmentUserCond cond);

	/**
	 * 新增漏电自检
	 * @param serialNumber
	 * @param data2
	 */
	void insert(String serialNumber, List<CheckResult> data2,Long time);

	/**
	 * 立即漏电自检
	 * @param serialNumber
	 * @param mainUserId
	 * @param projectId
	 * @return
	 */
	Result test(String serialNumber);

	/**
	 *
	 */
	void sendExaminationVerOne(TimingExamination equipment2);

	void sendExaminationVerTwo(String string, List<TimingExamination> timingExaminations);
}
