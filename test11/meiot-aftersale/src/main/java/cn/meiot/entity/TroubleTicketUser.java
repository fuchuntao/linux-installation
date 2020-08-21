package cn.meiot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (TroubleTicketUser)实体类
 *
 * @author makejava
 * @since 2020-04-17 17:21:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TroubleTicketUser implements Serializable {
    private static final long serialVersionUID = 224154402487071004L;
    /**
    * 主键
    */
    private Long id;
    /**
    * 用户ID
    */
    private Long userId;
    /**
    * 故障ID
    */
    private Long alarmId;
    /**
    * 设备别名
    */
    private String deviceAlias;
    /**
    * 开关别名
    */
    private String switchAlias;




}