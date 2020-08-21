package cn.meiot.entity.vo;

import cn.meiot.utils.ErrorCodeUtil;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ResetPwd {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 旧密码
     */
    @NotEmpty(message = ErrorCodeUtil.OLD_PWD_NOT_BE_NULL)
    private String oldPwd;

    /**
     * 新密码
     */
    @NotEmpty(message = ErrorCodeUtil.NEW_PWD_NOT_BE_NULL)
    private String newPwd;

    /**
     * 确认密码
     */
    private String confirmPwd;
}
