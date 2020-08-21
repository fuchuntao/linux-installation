package cn.meiot.dao;

import cn.meiot.entity.db.WaterUser;
import cn.meiot.entity.dto.pc.water.WaterConditionDto;
import cn.meiot.entity.dto.pc.water.WaterMeterDto;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WaterUserMapper extends BaseMapper<WaterUser> {

    /**
     * 根据水表号查询设备拥有人
     * @param meterId
     * @return
     */
    WaterUser queryWaterUserByMeterId(String meterId);

    /**
     * 修改水表
     * @param waterUser
     * @return
     */
    int updateWaterUser(WaterUser waterUser);

    /**
     * 删除水表
     * @param ids
     * @param projectId
     * @param mainUserId
     */
    void deleteWaterUsers(@Param("ids") List<Long> ids, @Param("projectId") Integer projectId, @Param("userId") Long mainUserId);

    /**
     * 查询水表信息
     * @param waterMeterDto
     * @return
     */
    List<WaterMeterDto> information(WaterMeterDto waterMeterDto);

    /**
     * 查询楼层水表
     * @param
     * @return
     */
    List<WaterMeterDto> floorWater(WaterConditionDto waterConditionDto);

    /**
     *
     * @param setMeterId
     * @return
     */
    List<Map> queryWaterUser(@Param("ids") Set<String> setMeterId);

    /**
     * 查询水表
     * @param ids
     * @return
     */
    List<String> queryMetersByBuildingIds(@Param("ids") List<Long> ids);

    /**
     * 根据组织架构
     * @param longs
     * @return
     */
    String findOneIdByBuildingIds(@Param("ids") List<Long> longs);

    /**
     * 查询该项目的设备号
     * @param projectId
     * @return
     */
    List<String> queryMetersByProjectId(@Param("projectId") Integer projectId);

    /**
     * 根据组织架构查询水表
     * @param id
     * @param mainUserId
     * @return
     */
    List<Map> queryBuilding(@Param("buildingId") Long buildingId, @Param("userId") Long userId);
}
