package cn.meiot.entity.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessToken {

    /**
     * access_token
     */
    private String access_token;

    /**
     * 有效时长
     */
    private long expires_in;
}
