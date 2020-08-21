package cn.meiot.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  AppMeterMonthsVo implements Serializable{

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 主账户id
     */
    private Integer userId;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 电流
     */
    private BigDecimal leakage;

    /**
     * 负载
     */
    private BigDecimal power;

    /**
     * 温度
     */
    private BigDecimal temp;

    private Long projectId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppMeterMonthsVo)) return false;
        AppMeterMonthsVo that = (AppMeterMonthsVo) o;
        return Objects.equals(serialNumber, that.serialNumber) &&
                Objects.equals(switchIndex, that.switchIndex) &&
                Objects.equals(switchSn, that.switchSn) &&
                Objects.equals(sYear, that.sYear) &&
                Objects.equals(sMonth, that.sMonth) &&
                Objects.equals(sDay, that.sDay) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber, switchIndex, switchSn, sYear, sMonth, sDay, userId, projectId);
    }
}
