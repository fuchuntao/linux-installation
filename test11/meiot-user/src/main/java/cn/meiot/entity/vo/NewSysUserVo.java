package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewSysUserVo {

    private Integer id;

    /**
     * 账号
     */
    private String account;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 职位id
     */
    private Integer positionId;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 确认密码
     */
    private String confirmPwd;
}
