package cn.meiot.dao;

import cn.meiot.entity.db.WaterMeter;
import cn.meiot.entity.dto.pc.water.WaterConditionDto;
import cn.meiot.entity.excel.FloorWaterExcel;
import cn.meiot.entity.excel.InformationExcel;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Set;

public interface WaterMeterMapper extends BaseMapper<WaterMeter> {
    /**
     * 查询所有水表id
     * @return
     */
    Set<String> queryMeterIdAll();

    /**
     * 根据组织架构查询设备
     * @param longSet
     * @return
     */
    List<String> queryMeterIdByBuilding(Set<Long> longSet);

    /**
     * 楼层水表导出
     * @param waterConditionDto
     * @return
     */
    List<FloorWaterExcel> floorWaterExcel(WaterConditionDto waterConditionDto);

    /**
     * 水表信息导出
     * @param waterConditionDto
     * @return
     */
    List<InformationExcel> informationExcel(WaterConditionDto waterConditionDto);



    /**
     * 根据项目id查询水表id和水表号
     * @param projectId
     * @return
     */
    List<WaterMeter> queryMeterIdAllByProjectId(Integer projectId);
}
