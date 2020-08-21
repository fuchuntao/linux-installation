package cn.meiot.dao;

import java.util.List;

import cn.meiot.entity.db.SwitchType;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.vo.SwitchTypeVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

public interface SwitchTypeMapper extends BaseMapper<SwitchType>{

	/**
	 * 根据项目id查询该项目的设备  按名称分组
	 * @param projectId
	 * @return
	 */
	List<SwitchTypeVo> querySwitchByProjectId(Integer projectId);

    List<SwitchType> selectPage(PcEquipmentUserCond cond);

    Integer queryNameCount(@Param("name") String name, @Param("projectId") Integer projectId, @Param("id") Long id);

	/**
	 * 查询默认id
	 * @param projectId
	 * @return
	 */
	Long queryDefaultId(Integer projectId);
}
