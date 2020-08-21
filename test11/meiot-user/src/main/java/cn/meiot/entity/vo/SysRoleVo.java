package cn.meiot.entity.vo;

import cn.meiot.utils.ErrorCodeUtil;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SysRoleVo {

    /**
     * 角色id
     */
    private Integer id;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 角色名称
     */
    @NotEmpty(message= ErrorCodeUtil.ROLE_NAME_NOT_BE_NULL)
    private String name;

    /**
     * 角色状态
     */
    private Integer rStatus;

    /**
     * 类型  1 平台 2 企业 3 代理商 4 维修 5 个人
     */
    private Integer type;
}
