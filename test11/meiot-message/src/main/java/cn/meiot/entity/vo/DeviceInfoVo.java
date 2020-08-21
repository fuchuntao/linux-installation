package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfoVo {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 事件
     */
    private Integer event;

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 总数
     */
    private Integer totalNum;

    /**
     * 项目Id
     */
    private Integer projectId;
}
