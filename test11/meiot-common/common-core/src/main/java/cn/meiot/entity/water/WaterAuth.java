package cn.meiot.entity.water;

import lombok.Data;

@Data
public class WaterAuth {
    /**
     * 状态
     */
    private String status;
    /**
     * token
     */
    private String token;
    /**
     * 信息
     */
    private String message;
}
