package cn.meiot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * token
     */
    private String authentication;

    /**
     * 设备型号
     */
    private String device;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * uri
     */
    private String uri;

    /**
     * 登陆账号的类型
     */
    private  Integer type;
}
