package cn.meiot.service;

import cn.meiot.entity.SysRolePermission;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.RolePermissionVo;
import cn.meiot.entity.vo.SysRolePermissionVo;
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
public interface ISysRolePermissionService extends IService<SysRolePermission> {


    /**
     * 给管理平台添加权限
     * @param rolePermissionVo
     * @return
     */
    Result addPlatform(RolePermissionVo rolePermissionVo, Long adminId);

    /**
     * 通过角色id查询权限列表
     * @param roleIds
     * @return
     */
    List<String> getPermission(List<Integer> roleIds);

    /**
     * 添加企业用户权限
     * @param rolePermissionVo
     * @param userId
     * @return
     */
    Result addEnPermission(RolePermissionVo rolePermissionVo, Long userId);
}
