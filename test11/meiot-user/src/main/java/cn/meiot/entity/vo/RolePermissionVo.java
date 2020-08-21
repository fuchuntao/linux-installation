package cn.meiot.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionVo {

    /**
     * 权限id
     */
    private Integer roleId;

    /**
     * 权限id
     */
    private List<Integer> perimissions;

    /**
     * 类型： 1：平台   2：企业
     */
    private Integer type;

    /**
     * 项目id
     */
    private Integer projectId;
}
