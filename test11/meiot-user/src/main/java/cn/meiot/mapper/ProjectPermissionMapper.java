package cn.meiot.mapper;

import cn.meiot.entity.ProjectPermission;
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
 * @since 2019-09-25
 */
@Mapper
public interface ProjectPermissionMapper extends BaseMapper<ProjectPermission> {

    /**
     * 根据项目类型获取权限列表
     * @param id 项目id
     * @param ids  权限ids
     * @return
     */
    List<SysPermission> projectPermissionlist(@Param("id") Integer id, @Param("list") List<Integer> ids);

    /**
     * 根据项目id获取到所拥有的权限ids
     * @param projectId
     * @return
     */
    @Select("select sys_permission_id from project_permission where project_id = #{projectId}")
    List<Integer> findpermissionIds(Integer projectId);

    /**
     * 通过权限id返回有效的权限id
     * @param projectId 项目id
     * @param perimissions 权限id
     * @return
     */
    List<Integer> getPermissionIds(@Param("projectId") Integer projectId,@Param("list") List<Integer> perimissions);
}
