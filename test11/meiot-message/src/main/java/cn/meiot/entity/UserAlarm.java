package cn.meiot.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * (UserAlarm)实体类
 *
 * @author makejava
 * @since 2020-04-15 09:57:44
 */
@Data
public class UserAlarm implements Serializable {
    private static final long serialVersionUID = 996633322307988158L;

    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 故障id
     */
    private Long alarmId;
    /**
     * 是否已读 0未读 1已读
     */
    private Integer isRead;
    /**
     * 设备别名
     */
    private String deviceAlias;
    /**
     * 开关别名
     */
    private String switchAlias;
    /**
     * 消息内容
     */
    private String msgContent;


}