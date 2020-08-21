package cn.meiot.entity.db;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserChangeSwitch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 设备号
     */
    private String serialNumber;

    /**
     * index
     */
    private Integer switchIndex;

    /**
     * 旧的开关号
     */
    private Long oldSwitchSn;

    /**
     * 新的开关号
     */
    private Long newSwitchSn;

    /**
     * 类型 0:修改  1删除
     */
    private Integer type = 0;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * user_id
     */
    private Long userId;

    /**
     * address
     */
    private String address;

    /**
     * 设备名
     */
    private String name;
}
