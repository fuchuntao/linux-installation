package cn.meiot.service;

import cn.meiot.dto.PasswordDto;
import cn.meiot.entity.db.HuaweiEquipment;

public interface HuaweiEquipmentService {

    public void addHuaweiEquipment(HuaweiEquipment huaweiEquipment);

    /**
     * 设备号查询密码
     * @param serialNumber
     * @return
     */
    PasswordDto queryPasswordDtoBySerial(String serialNumber);

    /**
     *通过设备号查询华为设备id
     * @param deviceId
     * @return
     */
    String querySerialNumber(String deviceId);
}
