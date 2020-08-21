package cn.meiot.entity.bo;

import lombok.Data;

@Data
public class UserInfoBo {

    private Long id;

    /**
     * 账号
     */
    private String userName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 账户类型 1 运营 2 企业 3 代理商 4 维修 5 个人
     */
    private Integer type;

    /**
     * 头像
     */
    private Integer avatar;

    /**
     * 1 正常 2 禁用
     */
    private Integer status;

    /**
     * 是否是超级管理员
     */
    private Integer isAdmin;

}
