package cn.meiot.entity.db;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class HuaweiEquipment implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 华为设备id
     */
    private String deviceId;

    /**
     * 密码
     */
    private String secret;

    /**
     * 设备号
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private String serialNumber;

    /**
     * psk
     */
    private String psk;
}
