package cn.meiot.service;

import cn.meiot.entity.SysPermission;
import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-05
 */
public interface ISysPermissionService extends IService<SysPermission> {

    /**
     * 根据类型获取权限列表
     * @param userId
     * @param type
     * @return
     */
    Result list(Long userId, Integer type);

    /**
     * 通过用户id查询权限
     * @param userId 用户id
     * @param type  类型
     * @return
     */
    List<SysPermission> getPermission(Long userId, Integer type);

    /**
     * 根据类型获取菜单列表
     * @param userId   操作员id
     * @param roleId  ；角色id
     * @return
     */
    Result listMenu(Long userId, Integer roleId);

    /**
     * 获取企业用户所拥有的权限列表
     * @param adminId
     * @param roleId
     * @param projectId
     * @return
     */
    Result enterpriseListById(Long adminId, Integer roleId, Integer projectId);

    /**
     * 通过用户查询权限信息
     * @param user
     * @return
     */
    List<String> queryEnPermissionIds(SysUser user,List<Integer> roleIds,Integer projectId);

    /**
     * 设置用户权限信息
     * @param user
     * @param list
     */
    void setRunPlatformPermission(SysUser user, List<Integer> list);
}
