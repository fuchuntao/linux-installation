package cn.meiot.service.apiservice;

import cn.meiot.entity.dto.apiservice.SerialDto;
import cn.meiot.entity.vo.Result;

import java.util.List;

public interface SwitchApIService {
    /**
     * 设备
     * @param serialNumber
     * @return
     */
    List listSwitchSn(String serialNumber);

    /**
     * 控制设备
     * @param serialDto
     * @return
     */
    Result sendSwitch(SerialDto serialDto);
}
