package cn.meiot.service.apiservice;

import cn.meiot.entity.dto.apiservice.SerialDto;
import cn.meiot.entity.vo.Result;

public interface EquipmentApiService {
    /**
     * 添加设备
     * @param serialDto
     * @return
     */
    Result insertSerial(SerialDto serialDto);

    /**
     * 删除设备
     * @param serialDto
     * @return
     */
    Result deleteSerial(SerialDto serialDto);

    /**
     * 分页查询设备
     * @param page
     * @param pageSize
     * @return
     */
    Result listSerial(Integer page, Integer pageSize,Long appId);

    /**
     * 鉴权 后续放缓存
     */
    void authentication(String serialNumber,Long appId);

    /**
     * 查询设备是否被api添加
     */
    Long selectIdBySerialNumber(String serialNumber);
}
