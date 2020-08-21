package cn.meiot.entity.dto.apiservice;

import lombok.Data;

@Data
public class SwitchApi {
    /**
     * 开关号
     */
    private String switchSn;
    /**
     * 开关顺序
     */
    private Integer switchIndex;
    /**
     * 开关状态
     */
    private Integer type;
}
