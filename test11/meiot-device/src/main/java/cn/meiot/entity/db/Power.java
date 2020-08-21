package cn.meiot.entity.db;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.Set;

@Data
public class Power {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 功率
     */
    private Integer power;

    /**
     * user_id
     */
    private Long userId;

    /**
     * 是否开启
     */
    private Boolean isSwitch;


    /**
     * 当前时间
     */
    private Date createTime;

    /**
     * 设备号
     */
    private String serialNumber;

    /**
     *
     */
    private Set<PowerAppUser> powerAppUserList;

}
