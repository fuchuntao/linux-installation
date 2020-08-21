package cn.meiot.entity;


import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser   implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

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
     * 密码盐
     */
    private String salt;

    /**
     * 头像
     */
    private Integer avatar;

    /**
     * 1 正常 2 禁用
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 账户类型
     */
    private Integer type;

    /**
     * 更新时间
     */
    private String updateTime;

    private Integer belongId;

    private Integer enterpriseId;

    private Integer isAdmin;

    private Integer deleted;

    /**
     * 最近登录时间
     */
    private String loginTime;

    /**
     * 创建用户id
     */
    private Long createUserId;



}
