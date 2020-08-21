package cn.meiot.entity.bo;


import lombok.Data;

@Data
public class LoginUserBo {

    /**
     * 凭证
     */
    private String token;
    /**
     * 用户
     */
    private UserInfoBo user;

    /**
     * 登录默认项目id
     */
    private Integer defaultProjectId;
}
