package cn.meiot.entity.bo;

import lombok.Data;

/**
 * 用户信息
 */
@Data
public class UserInfo {

    /**
     * 用户id
     */
    private Long userId;

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
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县
     */
    private String district;

    /**
     * 头像
     */
    private String headPortrait;
    /**
     * 头像缩略图
     */
    private String thumHeadPortrait;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 微信绑定状态   1：已绑定   0：未绑定
     */
    private Integer wxBindstatus;

    /**
     * 微信登录绑定状态  1:已绑定   0：未绑定
     */
    private Integer wxLoginstatus;

    /**
     * 微信头像
     */
    private String wxNickName;

    /**
     * 微信头像
     */
    private String headImgurl;
}
