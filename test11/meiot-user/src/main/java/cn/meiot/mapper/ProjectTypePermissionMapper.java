package cn.meiot.mapper;

import cn.meiot.entity.ProjectTypePermission;
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
public interface ProjectTypePermissionMapper extends BaseMapper<ProjectTypePermission> {

    /**
     * 根据项目类型id查询权限信息
     * @param id
     * @return
     */
    List<SysPermission> typePermissionlist(@Param("id") Integer id);

    /**
     * 通过项目类型id获取权限id
     * @param projectType
     * @return
     */
    @Select(" select permission_id from project_type_permission where project_type_id = #{projectType}")
    List<Integer> selectIdsBypTypeId(Integer projectType);

    /**
     * 通过项目项目类型id查询所有用的按钮权限id
     * @param id
     * @return
     */
    @Select(" SELECT id FROM `sys_permission` WHERE p_type = 2 AND  id IN(SELECT permission_id FROM `project_type_permission` WHERE project_type_id = #{id}) ")
    List<Long> selectCheckButtonIds(Integer id);

    /**
     * 获取当前项目所拥有的所有按钮权限
     * @param id
     * @return
     */
    @Select(" select * from `sys_permission` where p_type = 2 and  id in(select sys_permission_id from `project_permission` where project_id = #{id}) ")
    List<Long> queryCheckedButton(Integer id);
}
