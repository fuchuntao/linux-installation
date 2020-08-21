package cn.meiot.entity.openapi;

import cn.meiot.enums.CallbackEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CallBackEntity {
    /**
     * appid
     */
    private Long appId;
    /**
     * 设备号
     */
    private String serialNumber;
    /**
     * 类型
     */
    private CallbackEnum callbackEnum;

    /**
     * 数据
     */
    private String data;

}
