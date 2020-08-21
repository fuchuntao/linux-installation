package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 故障消息
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaultMessage extends Model<FaultMessage> {

    private static final long serialVersionUID = 1L;

    /**
     * 故障消息id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户
     */
    private Long userId;

    /**
     * 故障时间
     */
    private String faultTime;

    /**
     * 故障事件
     */
    private Integer switchEvent;

    /**
     * 开关别称
     */
    private String switchAlias;

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
    private String switchSn;

    /**
     * 设备别称
     */
    private String equipmentAlias;

    /**
     * 状态 1-待处理,2=处理中,3=已处理
     */
    private Integer switchStatus;

    /**
     * 是否已读:0=否,1=是
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 最近一次的发送时间
     */
    private String sendTime;

    /**
     * 故障内容
     */
    private String msgContent;

    /**
     * 项目Id
     */
    private Integer  projectId;

    /**
     * 上报的故障值
     */
    private String faultValue;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
