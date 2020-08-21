package cn.meiot.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 设备用户关系表
 * </p>
 *
 * @author wuyou
 * @since 2019-11-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EquipmentUser extends Model<EquipmentUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 备注名称
     */
    private String name;

    /**
     * 状态:0-待审核 1-正常 2-禁用
     */
    private Integer userStatus;

    /**
     * 是否主账户: 0-否 1-是
     */
    private Integer isPrimary;

    /**
     * 默认设备: 0-否 1-是
     */
    private Integer isDefault;

    private Integer id;

    /**
     * 开关状态
     */
    private Integer isSwitch;

    /**
     * 子用户名
     */
    private String userName;

    /**
     * 0：没有项目   项目id
     */
    private Integer projectId;

    private String createTime;


    @Override
    protected Serializable pkVal() {
        return this.serialNumber;
    }

}
