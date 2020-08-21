package cn.meiot.entity.vo;


import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserWXBindVo {

    /**
     * wx  code
     */
    @NotEmpty(message = "微信code不能为空")
    private String wxCode;

    /**
     * 账号
     */
    @NotEmpty(message = "账号不能为空")
    private String account;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    private String password;
}
