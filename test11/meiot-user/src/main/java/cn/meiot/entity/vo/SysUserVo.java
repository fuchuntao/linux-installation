package cn.meiot.entity.vo;

import cn.meiot.common.ErrorCode;
import cn.meiot.utils.ErrorCodeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserVo {

    private Long id;

    /**
     *账号
     */
    @NotEmpty(message= ErrorCodeUtil.ACCOUNT_NOT_BE_NULL)
    private String account;

    /**
     *昵称
     */
    private String nikName;

    /**
     *角色
     */
    private List<Integer> roles;

    /**
     *密码
     */

    private String password;

    /**
     * 邮箱
     */
    //@Email(message="邮箱格式有误！")
    private String email;

    /**
     * 类型  1：平台   2：企业
     */
    @NotNull(message = ErrorCodeUtil.TYPE_NOT_BE_NULL)
    private Integer type;
}
