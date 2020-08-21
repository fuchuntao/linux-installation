package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppMeterHours extends Model<AppMeterHours> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
    private Long switchSn;

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

    private Long projectId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
