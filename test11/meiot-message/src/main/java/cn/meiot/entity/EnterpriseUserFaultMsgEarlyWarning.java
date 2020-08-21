package cn.meiot.entity;

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
    * 
    * </p>
*
* @author wuyou
* @since 2019-10-22
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class EnterpriseUserFaultMsgEarlyWarning extends Model<EnterpriseUserFaultMsgEarlyWarning> {

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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
