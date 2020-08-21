package cn.meiot.service;

import cn.meiot.entity.SysRole;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SysRoleVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 获取角色列表
     * @param userId  用户id
     * @param type  查看的角色类型
     * @return
     */
    Result getList(Long userId, Integer type,String keyword, Page<SysRole> page);

    /**
     * 新增角色
     * @param sysRoleVo
     * @return
     */
    Result saveRole(SysRoleVo sysRoleVo);

    /**
     * 修改角色
     * @param sysRoleVo
     * @return
     */
    Result edit(SysRoleVo sysRoleVo);

    /**
     * 删除角色
     * @param id
     * @param userId
     * @return
     */
    Result deleteRole(Integer id, Long userId) throws Exception;

    /**
     * 通过用户id查询角色名称
     * @param id
     * @return
     */
    String queryNameByUserId(Long id);
}
