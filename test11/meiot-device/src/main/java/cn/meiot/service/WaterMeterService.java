package cn.meiot.service;

import cn.meiot.entity.db.WaterUser;
import cn.meiot.entity.dto.pc.water.WaterConditionDto;
import cn.meiot.entity.dto.pc.water.WaterMeterDto;
import cn.meiot.entity.excel.FloorWaterExcel;
import cn.meiot.entity.excel.InformationExcel;
import cn.meiot.entity.vo.Result;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WaterMeterService {
    
    Result saveWaterUser(WaterUser waterUser);

    void waterAuthentication(Integer projectId, Long mainUserId, String meterId);

    Result updateWaterUser(WaterUser waterUser);

    Result deleteWaterUser(List<Long> ids, Integer projectId, Long mainUserId);

    /**
     * 水表信息查询
     * @param waterMeterDto
     * @return
     */
    Result information(WaterMeterDto waterMeterDto);

    /**
     * 定时添加水表
     * @return
     */
    void systemAddWater();

    /**
     * 楼层水表
     * @param waterConditionDto
     * @return
     */
    Result floorWater(WaterConditionDto waterConditionDto);

    /**
     * 通过设备号获取项目id ,名称，地址，单位
     * @param setMeterId
     * @return
     */
    List<Map> queryWaterUser(Set<String> setMeterId);

    /**
     * 根据id查询名称以及设备号
     * @param id
     * @return
     */
    List<Map> queryMeters(Long id,Integer projectId,Long userId);

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
     * 刷新
     * @param projectId
     * @return
     */
    Result refresh(Integer projectId);

    /**
     *
     * @param listMap
     */
    void queryMetersByBuildingIds(List<Map> listMap);

    /**
     * 查询组织架构下的水表
     * @param id
     * @param mainUserId
     * @param userId
     * @return
     */
    Result queryBuilding(Long id, Long mainUserId, Long userId);
}
