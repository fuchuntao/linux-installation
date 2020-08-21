package cn.meiot.entity.vo;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppMeterHoursVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 开关序号
     */
    private Integer switchIndex;

    /**
     * 开关编号
     */
    private List<DeviceVo> switchSnList;

    /**
     * 电量
     */
    private BigDecimal meter;

    /**
     * 时间（单位：小时）
     */
    private Integer sTime;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 主账号id
     */
    private Long userId;

    private Integer sYear;

    private Integer sMonth;

    private Integer sDay;

    private String updateTime;

    private Integer projectId;


    private Set<Integer> switchTime;


    /**
     * 1表示小时,2:日,3:月
     */
    private Integer flag;


    private List<Map<String, Object>> mapList;
}
