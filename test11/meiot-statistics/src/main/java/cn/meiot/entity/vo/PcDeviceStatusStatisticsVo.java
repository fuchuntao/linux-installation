package cn.meiot.entity.vo;

import lombok.Data;

/**
 * @ClassName: PcDeviceStatusStatisticsVo
 * @Description: 首页管理平台设备维修返回数据实体类
 * @author: 符纯涛
 * @date: 2019/9/20
 */
@Data
public class PcDeviceStatusStatisticsVo {

    /**
     * 已处理
     */
    private Long Handled;

    /**
     * 未处理
     */
    private Long Untreated;

    /**
     * 设备保修统计
     */
    private Long DeviceStatusSum;


}
