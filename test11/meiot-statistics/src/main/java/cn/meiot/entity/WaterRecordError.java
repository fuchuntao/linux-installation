package cn.meiot.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 水表抄表记录异常表
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WaterRecordError extends Model<WaterRecordError> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * 抄表记录id
     */
    private Long id;

    /**
     * 客户编号
     */
    private String ccid;

    /**
     * 水表编号
     */
    private String meterid;

    /**
     * 设备编号
     */
    private String deviceid;

    /**
     * 抄表时间
     */
    private Long readtime;

    /**
     * 抄表读数
     */
    private BigDecimal readcount;

    /**
     * 核对情况
     */
    private String checked;

    /**
     * 核对人
     */
    private String checker;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 0：插入失败，1：插入成功
     */
    private Integer status;


    @Override
    protected Serializable pkVal() {
        return this.recordId;
    }

}
