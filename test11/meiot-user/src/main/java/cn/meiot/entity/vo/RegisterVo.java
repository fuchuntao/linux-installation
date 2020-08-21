package cn.meiot.entity.vo;

import lombok.Data;

@Data
public class RegisterVo {

    /**
     * 账号
     */
    private String account;


    /**
     * 新密码
     */
    private String newPwd;

    /**
     * 确认密码
     */
    private String confirmPwd;

    /**
     * 验证码
     */
    private String code;
}
