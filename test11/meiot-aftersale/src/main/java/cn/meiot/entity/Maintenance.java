package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance extends Model<Maintenance> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 申报的账号
     */
    private String account;

    /**
     * 申报描述
     */
    private String reason;

    /**
     * 状态  1：保修  2：受理  3：维修
     */
    private Integer mStatus;

    /**
     * 报修时间
     */
    private String reportTime;

    /**
     * 受理时间
     */
    private String acceptTime;

    /**
     * 维修时间
     */
    private String maintainTime;

    /**
     * 附件图片路径
     */
    private String imgPath;
//    private List<String> imgPathList;

    /**
     * 故障类型ID
     */
    private Integer mType;

    /**
     * 故障类型名字
     */
    @TableField(exist = false)
    private String typeName;

    /**
     * 用户id
     */
    private Long userId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


}
