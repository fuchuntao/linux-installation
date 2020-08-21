package cn.meiot.entity.vo;

import cn.meiot.utils.ErrorCodeUtil;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Login {

    /**
     * 账号
     */
    @NotEmpty(message = ErrorCodeUtil.ACCOUNT_NOT_BE_NULL)
    private String account;

    /**
     * 密码
     */
    @NotEmpty(message = ErrorCodeUtil.PWD_NOT_BE_NULL)
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 随机时间戳
     */
    private String randomData;

    /**
     * 设备号
     */
    private String device;
}
