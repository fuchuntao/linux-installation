package cn.meiot.service;

import cn.meiot.entity.SysUserRole;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SysUserRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

    /**
     * 给用户新增角色()
     * @param sysUserRoleVo
     */
    Result add(SysUserRoleVo sysUserRoleVo,Long adminId);

    /**
     * 获取此用户所拥有的角色
     * @param adminId 操作用户id
     * @param userId   被操作用户id
     * @return
     */
    Result getListByUserId(Long adminId, Long userId);

    /**
     * 根据用户id与类型查询
     * @param userId
     * @return
     */
    List<Integer> getRoleList(Long userId);

    /**
     * 通过角色id获取用户id
     * @param roleId
     * @return
     */
    List<Long> getUserIdByRoleId(Integer roleId);
}
