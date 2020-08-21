package cn.meiot.mapper;

import cn.meiot.entity.RoleProjectPermission;
import cn.meiot.entity.SysPermission;
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
public interface RoleProjectPermissionMapper extends BaseMapper<RoleProjectPermission> {

    /**
     * 保存
     * @param roleProjectPermission
     */
    void save(@Param("roleProjectPermission") RoleProjectPermission roleProjectPermission);

    /**
     * 通过角色项目查询权限信息
     * @param roleIds
     * @param projectId
     * @return
     */
    List<String> getPermission(@Param("list") List<Integer> roleIds, @Param("projectId") Integer projectId);

    /**
     * 获取权限列表
     * @param roleId 角色id
     * @param projectId 项目id
     * @param permission 权限ids
     * @return
     */
    List<SysPermission> getList(@Param("roleId") Integer roleId,@Param("projectId") Integer projectId,@Param("list") List<Integer> permission);

    /**
     * 通过角色id与项目查询权限ids
     * @param roles
     * @param projectId
     * @return
     */
    List<Integer> selectPermissionId(@Param("list") List<Integer> roles, @Param("projectId") Integer projectId);

    /**
     *
     * @param roleId
     * @param projectId
     * @return
     */
    @Select(" SELECT * FROM `sys_permission` WHERE p_type = 2 AND  id IN(SELECT permission_id FROM `role_project_permission` WHERE project_id = #{projectId} AND role_id = #{roleId}) ")
    List<Integer> queryCheckedButton(@Param("roleId") Integer roleId,@Param("projectId") Integer projectId);

    /**
     * 查询多余的权限
     * @param projectId
     * @return
     */
    List<Integer> querySurplusPermission(@Param("projectId") Integer projectId);

    /**
     * 通过角色id与项目id获取就权限id
     * @param id 角色id
     * @param projectId 项目id
     * @return
     */
    @Select(" select id from  role_project_permission where role_id = #{id} and project_id = #{projectId} ")
    List<Integer> selectOldPermissionId(@Param("id") Integer id, @Param("projectId") Integer projectId);

    @Select(" select role_id from  role_project_permission where permission_id = #{permissionId} and project_id = #{projectId} ")
    List<Integer> listRoleIds(@Param("permissionId") Integer permissionId, @Param("projectId") Integer projectId);
}
