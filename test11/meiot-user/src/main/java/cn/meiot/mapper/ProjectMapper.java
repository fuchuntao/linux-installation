package cn.meiot.mapper;

import cn.meiot.entity.Project;
import cn.meiot.entity.vo.QueryProjectVo;
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
 * @since 2019-09-19
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 查询项目列表
     * @param projectVo
     * @return
     */
    List<Project> getList(QueryProjectVo projectVo);

    /**
     * 查询项目列表总记录数
     * @param projectVo
     * @return
     */
    Integer getListCount(QueryProjectVo projectVo);

    /**
     * 通过角色id查询项目列表
     * @param roles
     * @return
     */

    List<Map<String, Object>> getListByRoles(@Param("list") List<Integer> roles);

    /**
     * 通过企业id获取项目列表
     * @param enterpriseId
     * @return
     */

    @Select("  SELECT id,project_name as  projectName FROM `project` WHERE enterprise_id = #{enterpriseId} ")
    List<Map<String, Object>> selectListByEnId(Integer enterpriseId);

    /**
     * 根据鲜蘑菇名称查询项目id
     * @param projectName
     * @return
     */
    @Select(" select id from project where project_name = #{projectName}")
    Integer selectIdByProjectName(String projectName);

    /**
     * 根据条件查询列表
     * @param projectVo
     * @return
     */
    List<Project> selectListByCondition(QueryProjectVo projectVo);

    /**
     * 通过项目id获取企业id
     * @param projectId
     * @return
     */
    @Select(" select enterprise_id from project where id = #{projectId} ")
    Integer selectEnterpriseIdById(Integer projectId);

    /**
     * 通过企业id获取所有的项目
     * @param enterpriseId
     * @return
     */
    @Select(" select id ,project_name as projectName from project where enterprise_id = #{enterpriseId}")
    List<Map<String,Object>> queryProjectByEnterpriseId(Integer enterpriseId);

    /**
     * 通过企业id查询项目id
     * @param enterpriseId
     * @return
     */
    @Select(" select id  from project where enterprise_id = #{enterpriseId}")
    List<Integer> getProjectIdByenId(Integer enterpriseId);

    /**
     * 通过角色id查询项目id
     * @param list
     * @return
     */
    List<Integer> getProjectIdByRoleId(List<Integer> list);

    /**
     * 通过项目类型查询项目id
     * @param projectTypeId
     * @return
     */
    @Select(" select id from project where project_type = #{projectTypeId} ")
    List<Integer> selectIdByType(Integer projectTypeId);

    /**
     * 找出项目权限中多余的权限
     * @param id 项目id
     * @param projectTypeId  项目类型id
     * @return
     */
    List<Integer> querySurplusProjectPermission(@Param("id") Integer id,@Param("projectTypeId") Integer projectTypeId);

    /**
     * 通过企业id查询项目id
     * @param enterpriseId
     * @return
     */
    @Select(" select id from project where enterprise_id = #{enterpriseId}")
    List<Integer> selectIdsByEnterpriseId(Integer enterpriseId);

    /**
     * 通过项目id获取创建时间
     * @param projectId
     * @return
     */
    @Select(" select create_time from project where id=#{projectId} ")
    String selectcreateTimeById(Integer projectId);

    /**
     * 通过项目id查询项目名称
     * @param projectId
     * @return
     */
    @Select(" select project_name from  project  where id=#{projectId} ")
    String queryProjectNameById(Integer projectId);

    /**
     * 通过项目id查询企业id
     * @param projectId
     * @return
     */
    @Select(" SELECT b.enterprise_name  FROM  (SELECT enterprise_id FROM project WHERE id =#{projectId} ) a LEFT JOIN enterprise b ON a.enterprise_id = b.id ")
    String queryEnterpriseName(Integer projectId);
}
