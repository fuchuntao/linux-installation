package cn.meiot.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceEventVo implements Serializable {

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 推送的时间（时间戳）
     */
    private String timestamp;

    /**
     * 事件（集合）
     */
    private List<Integer> event;

    /**
     * 开关序列号
     */
    private String switchSn;

    /**
     * 开关index
     */
    private Integer switchIndex;
}
