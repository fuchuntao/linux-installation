package cn.meiot.entity.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/10/16 10:30
 * @Copyright: www.spacecg.cn
 */
@Data
public class MqVo {

    private String serialNumber;

    private List<MqDeviceVo> mqDeviceVos;

    private List<MqStatusVo> mqStatusVos;

    private String timestamp;

    public MqVo(List<MqDeviceVo> mqDeviceVos, List<MqStatusVo> mqStatusVos) {
        this.mqDeviceVos = mqDeviceVos;
        this.mqStatusVos = mqStatusVos;
    }
    public MqVo(){}
}
