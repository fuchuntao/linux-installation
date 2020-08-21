package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WaterStatisticsMonths extends Model<WaterStatisticsMonths> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;


    /**
     * 年
     */
    private Integer year;

    /**
     * 月
     */
    private Integer month;

    /**
     * 日
     */
    private Integer day;


    /**
     * 抄表记录id
     */
    private Long id;
//    /**
//     * 客户编号
//     */
//    private String ccid;
    /**
     * 水表编号
     */
    private String meterid;
    /**
     * 设备编号
     */
    private String deviceid;
//    /**
//     * 核对状态
//     */
//    private String checked;
//    /**
//     * 核对者
//     */
//    private String checker;
    /**
     * 抄表时间
     */
    private Long readtime;
    /**
     * 抄表读数
     */
    private BigDecimal readcount;

    /**
     * 单位
     */
    private Double unit;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 项目id
     */
    private Integer projectId;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


    /**
     * 抄表的实际差值度数
     */
    private BigDecimal water;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WaterStatisticsMonths)) return false;
        WaterStatisticsMonths that = (WaterStatisticsMonths) o;
        boolean b = year.equals(that.year) &&
                month.equals(that.month) &&
                day.equals(that.day) &&
                userId.equals(that.userId) &&
                projectId.equals(that.projectId);
        return b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day, userId, projectId);
    }

}
