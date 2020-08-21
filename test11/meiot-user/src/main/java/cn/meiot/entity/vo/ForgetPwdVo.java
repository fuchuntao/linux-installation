package cn.meiot.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ForgetPwdVo {

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

    /**
     * 类型   1：系统    2：企业    5：个人
     */
    private Integer type;
}
