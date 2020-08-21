package cn.meiot.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 企业平台天数据统计表
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PcMeterHours extends Model<PcMeterHours> {

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
     * 年
     */
    private Integer sYear;

    /**
     * 月
     */
    private Integer sMonth;

    /**
     * 日
     */
    private Integer sDay;

    /**
     * 时
     */
    private Integer sTime;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 主账户id
     */
    private Long userId;

    /**
     * 修改时间
     */
    private String updateTime;

    private Long projectId;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
