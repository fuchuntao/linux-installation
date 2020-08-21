package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;

/**
 * <p>
 *
 * </p>
 *
 * @author wuyou
 * @since 2019-10-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class AppUserFaultMsgAlarm extends Model<AppUserFaultMsgAlarm> {
    @Tolerate
    public AppUserFaultMsgAlarm() {
    }

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String faultTime;

    private Integer event;

    private String switchAlias;

    private String serialNumber;

    private Integer switchIndex;

    private String switchSn;

    private String equipmentAlias;

    private Integer switchStatus;

    private Integer isRead;

    private String createTime;

    private String updateTime;

    private String sendTime;

    private String msgContent;

    private Integer projectId;

    private String faultValue;

    private Integer isShow;

    private Integer type;
    /**
     * 0 一键报修 1待受理 2待维修 3已维修
     */
    private Integer status;

    /**
     * 备注
     */
    private String note;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
