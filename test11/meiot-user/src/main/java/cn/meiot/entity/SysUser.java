package cn.meiot.entity;

import cn.meiot.entity.bo.UserInfo;
import cn.meiot.entity.bo.UserInfoBo;
import cn.meiot.utils.DTOConvert;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser  extends Model<SysUser> implements  Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
     * 账户类型 1 运营 2 企业 3 代理商 4 维修 5 个人
     */
    private Integer  type;

    /**
     * 1 正常 2 禁用
     */
    private Integer status;

    /**
     * 所属id（目前仅有企业id）
     */
    private Long belongId;

    /**
     * 所属企业id
     */
    private Integer enterpriseId;

    /**
     * 创建用户id
     */
    private Long createUserId;

    /**
     * 所属企业类型
     */
    private Integer enterpriseType;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;

    /**
     * 是否是超级管理员
     */
    private  Integer isAdmin;

    /**
     * 是否删除（0：未删除 1：已删除）
     */
    @TableLogic
    private Integer deleted;

    /**
     * 最近登录时间
     */
    private String loginTime;




    @Override
    protected Serializable pkVal() {
        return null;
    }


    public UserInfoBo convertToUserBo(){
        SysUserDTOConvert sysUserDTOConvert = new SysUserDTOConvert();
        UserInfoBo convert = sysUserDTOConvert.convert(this);
        return convert;
    }

    private static class SysUserDTOConvert implements DTOConvert<SysUser, UserInfoBo> {
        @Override
        public UserInfoBo convert(SysUser sysUser){
            UserInfoBo userInfoBo = new UserInfoBo();
            BeanUtils.copyProperties(sysUser,userInfoBo);
            return userInfoBo;
        }
    }


    public UserInfo convertUserIfo(){
        SysUserDTOConvertToUserInfo sysUserDTOConvertToUserInfo = new SysUserDTOConvertToUserInfo();
        UserInfo convert = sysUserDTOConvertToUserInfo.convert(this);
        return convert;
    }

    private static class SysUserDTOConvertToUserInfo implements  DTOConvert<SysUser,UserInfo>{


        @Override
        public UserInfo convert(SysUser sysUser) {
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(sysUser,userInfo);
            return userInfo;
        }
    }




}
