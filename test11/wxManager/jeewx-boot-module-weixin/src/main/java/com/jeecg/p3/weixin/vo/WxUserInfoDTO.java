package com.jeecg.p3.weixin.vo;


import java.io.Serializable;

public class WxUserInfoDTO implements Serializable {

    /**
     * 微信openid
     */
    private String  openId;

    /**
     * 用户id
     */
    private long userId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
