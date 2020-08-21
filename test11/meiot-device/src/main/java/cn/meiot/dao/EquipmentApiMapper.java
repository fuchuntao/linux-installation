package cn.meiot.dao;

import cn.meiot.entity.db.EquipmentApi;
import cn.meiot.entity.dto.apiservice.SerialDto;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;

public interface EquipmentApiMapper extends BaseMapper<EquipmentApi> {

    Long selectIdBySerialNuber(String serialNumber);

    int insertSerial(SerialDto serialDto);

    /**
     * 删除设备
     * @param serialDto
     */
    void deleteSerial(SerialDto serialDto);

    /**
     * 分页查询设备
     * @param page
     * @param pageSize
     * @param appId
     * @return
     */
    List<Map> listSerial(@Param("page") Integer page, @Param("pageSize") Integer pageSize, @Param("appId") Long appId);

    /**
     * 查询总数
     * @param appId
     * @return
     */
    Integer totalSerial(Long appId);
}
