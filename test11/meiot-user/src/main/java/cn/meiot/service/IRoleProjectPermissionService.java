package cn.meiot.service;

import cn.meiot.entity.RoleProjectPermission;
import cn.meiot.entity.vo.PermissionVo;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
public interface IRoleProjectPermissionService extends IService<RoleProjectPermission> {

    /**
     * 通过角色项目查询权限信息
     * @param roleIds
     * @param projectId
     * @return
     */
    List<String> getPermission(List<Integer> roleIds, Integer projectId);

    /**
     * 根据角色和项目后去权限列表
     * @param roleId 角色id
     * @param projectId 项目id
     * @return
     */
    Result getList(Integer roleId, Integer projectId,Long userId);

    /**
     * 设置权限信息
     * @param permissionVo
     * @return
     */
    Result setPermission(PermissionVo permissionVo,Long userId);

    /**
     * 查询企业多余的权限
     * @param projectId
     * @return
     */
    List<Integer> querySurplusPermission(Integer projectId);
}
