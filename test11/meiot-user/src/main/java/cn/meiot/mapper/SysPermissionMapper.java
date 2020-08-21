package cn.meiot.mapper;

import cn.meiot.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-05
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 通过角色id查询权限信息
     * @return
     */
    @Select("  SELECT   re.id,re.name,re.pid,re.p_type,re.type FROM `sys_permission` re where re.type = 1 ")
    List<SysPermission> queryPermissions();

    /**
     * 通过权限id查询权限id
     * @param perimissions
     * @return
     */
    //@Select("select id from  sys_permission where id in :list")
    List<Integer> selectIdsByIds(@Param("list") List<Integer> perimissions);

    /**
     * 通过
     * @param permissionIds
     * @return
     */
   // @Select("select url from  sys_permission where id in :list")
    List<String> selectUriByUserId(@Param("list") List<Integer> permissionIds);

    /**
     * 通过项目id获取权限列表
     * @param projectId
     * @return
     */
    @Select(" SELECT * FROM `sys_permission` WHERE id IN( SELECT  sys_permission_id FROM `project_permission` WHERE project_id = #{projectId}) ")
    List<SysPermission> queryListByProjectId(@Param("projectId") Integer projectId);

    /**
     * 通过项目id查询权限uri
     * @param projectId
     * @return
     */
    @Select(" SELECT url FROM `sys_permission` WHERE id IN(SELECT sys_permission_id FROM `project_permission` WHERE project_id = #{projectId}) ")
    List<String> queryIdsByEnterpriseId(Integer projectId);

    /**
     * 查询所有权限url
     * @return
     */
    @Select(" select url from sys_permission where type = 1 and p_type = 2")
    List<String> selectAllUrl();

    /**
     * 通过角色id查询权限url
     * @param list
     * @return
     */
    List<String> selectUrlByRoles(@Param("list") List<Integer> list);

    /**
     * 通过角色与项目id查询权限url
     * @param roleIds
     * @param projectId
     * @return
     */
    List<String> selectUrlByRoleAndProjectId(@Param("list") List<Integer> roleIds,@Param("projectId") Integer projectId);

    /**
     * 通过权限唯一标识查询权限id
     * @param permission
     * @return
     */
    @Select(" select id from sys_permission where permission = #{permission} limit 1")
    Long selectIdByPermission(String permission);
}
