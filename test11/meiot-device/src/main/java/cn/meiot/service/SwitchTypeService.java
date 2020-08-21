package cn.meiot.service;

import java.util.List;

import cn.meiot.entity.db.SwitchType;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SwitchTypeVo;

public interface SwitchTypeService {

	Result insert(SwitchType switchType);

	Result delete(List<Long> ids);

	Result update(SwitchType switchType);

	Result query(PcEquipmentUserCond cond);

	List<SwitchTypeVo> querySwitch(Integer projectId);

    boolean queryNameCount(String name,Integer projectId,Long id);

    Long queryDefaultId(Integer projectId);
}
