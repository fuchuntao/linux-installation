package cn.meiot.entity.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class WxUserInfoDTO implements Serializable {

    /**
     * 微信openid
     */
    private String  openId;

    /**
     * 用户id
     */
    private long userId;

    private String unionid;

}
