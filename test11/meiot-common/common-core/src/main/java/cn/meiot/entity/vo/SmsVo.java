package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsVo implements Serializable {

    private Long userId;

    /**
     * 账号
     */
    private String account;

    private String code;

    /**
     * 发送的账号里类型    1：运营平台   2：企业平台（企业app）   5：app个人端平台
     */
    private Integer type;

    /**
     * 类型
     */
    private Integer smsType;

    /**
     * 签名
     */
    private String sign;
}
