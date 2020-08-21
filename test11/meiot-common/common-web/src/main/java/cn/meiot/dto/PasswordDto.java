package cn.meiot.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 华为设备号
     */
    private String deviceId;
    /**
     * psk
     */
    private String psk;
    /**
     * 密码
     */
    private String secret;
    /**
     * 设备号
     */
    private String serialNumber;
}
