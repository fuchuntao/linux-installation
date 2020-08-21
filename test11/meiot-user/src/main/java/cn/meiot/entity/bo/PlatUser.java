package cn.meiot.entity.bo;

import lombok.Data;

@Data
public class PlatUser {

    private Long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 角色id
     */
    private Integer roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否是超级管理员   1：是   0：否
     */
    private  Integer isAdmin;
}
