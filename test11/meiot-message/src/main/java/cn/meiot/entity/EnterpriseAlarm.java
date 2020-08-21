package cn.meiot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (EnterpriseAlarm)实体类
 *
 * @author makejava
 * @since 2020-04-20 09:19:03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseAlarm implements Serializable {
    private static final long serialVersionUID = -79551960539616278L;
    
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