package cn.meiot.mapper;

import cn.meiot.entity.SysRolePermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 保存
     * @param sysRolePermission
     */
    void save(@Param("sysRolePermission") SysRolePermission sysRolePermission);

    /**
     * 根据角色id获取权限列表
     * @param roleIds
     * @return
     */
    List<String> getPermission(@Param("list") List<Integer> roleIds);

    /**
     * 通过角色获取权限id列表
     * @param roleIds
     * @return
     */
    List<Integer> selectPermissionIdByRoles(@Param("list") List<Integer> roleIds);

    /**
     * 通过角色id获取已经选中的权限id（按钮）
     * @param roleId
     * @return
     */
    @Select(" SELECT * FROM `sys_permission` WHERE p_type = 2 AND  id IN(SELECT sys_permission_id FROM `sys_role_permission` WHERE sys_role_id = #{roleId}) ")
    List<Integer> queryCheckedButton(Integer roleId);
}
