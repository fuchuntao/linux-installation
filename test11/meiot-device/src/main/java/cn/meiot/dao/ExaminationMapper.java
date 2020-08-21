package cn.meiot.dao;

import java.util.List;

import cn.meiot.entity.db.Examination;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.examination.PcExamination;
import tk.mybatis.mapper.common.BaseMapper;

public interface ExaminationMapper extends BaseMapper<Examination>{

	/**
	 * 
	 * @param cond
	 * @return
	 */
	List<PcExamination> query(PcEquipmentUserCond cond);

	/**
	 * 根据设备查询漏电状态
	 * @param cond
	 * @return
	 */
	List<Examination> queryBySerialNumber(PcEquipmentUserCond cond);

}
