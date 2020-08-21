package cn.meiot.entity.equipment;

import lombok.Data;

import java.io.Serializable;

@Data
public class Updata implements Serializable {
    /**
     * 版本号
     */
    private String version;
    /**
     * ip
     */
    private String ip;
    /**
     * 端口
     */
    private int port;
}
