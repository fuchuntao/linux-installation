package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: PcDeviceStatisticsVo
 * @Description: 首页管理平台设备统计数据实体类
 * @author: 符纯涛
 * @date: 2019/9/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PcDeviceStatisticsVo implements Serializable {
    /**
     * 设备量
     */
    private Long value;

    /**
     * 年
     */
    private Integer year;

    /**
     * 月
     *
     */
    private Integer month;


}
