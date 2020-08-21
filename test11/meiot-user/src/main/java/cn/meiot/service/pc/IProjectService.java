package cn.meiot.service.pc;

import cn.meiot.entity.Project;
import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.PermissionVo;
import cn.meiot.entity.vo.QueryProjectVo;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-19
 */
public interface IProjectService extends IService<Project> {

    /**
     * 查询项目列表
     * @param projectVo
     * @return
     */
    Result getList(QueryProjectVo projectVo);

    /**
     * 添加项目
     * @param project
     * @return
     */
    Result addProject(Project project);

    /**
     * 修改项目
     * @param project
     * @return
     */
    Result updateProject(Project project);

    /**
     * 获取项目的权限列表
     * @param id
     * @return
     */
    Result projectPermissionlist(Integer id);
    /**
     * 设置项目的权限信息
     * @param permissionVo
     * @return
     */
    Result setPermission(PermissionVo permissionVo);

    /**
     * 通过用户id查询项目列表
     * @param userId
     * @return
     */
    Result getListById(Long userId);

    /**
     * 导出项目列表
     * @param projectVo
     * @return
     */
    Result exportProject(QueryProjectVo projectVo, HttpServletResponse response);

    /**
     * 通过企业id获取项目列表
     * @param userId
     * @return
     */
    Result queryList(Long userId);

    /**
     * 切换项目
     * @param userId
     * @param projectId
     * @return
     */
    Result cutProjectId(Long userId, Integer projectId);

    /**
     * 通过项目id查询项目名称
     * @param projectId
     * @return
     */
    String queryProjectNameById(Integer projectId);

    /**
     * 通过项目id查询项目名称与企业名称
     * @param projectId
     * @return
     */
    Map<String, String> queryProNameByProjectId(Integer projectId);
}
