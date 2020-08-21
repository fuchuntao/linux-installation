package cn.meiot.service;

import cn.meiot.entity.SysMenu;
import cn.meiot.entity.SysPermission;
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
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 获取模块列表
     * @param
     * @return
     */
    Result getList(Long userId, Integer projectId);

    /**
     * 根据用户类型获取指定账户类型下的所有模块
     * @param userId 用户id
     * @param type   账户类型
     * @param projectId   项目id
     * @return
     */
    Result listByType(Long userId, Integer type,Integer projectId);

    /**
     * 查询平台用户所拥有的菜单
     * @param userId
     * @return
     */
    Result getPlatList(Long userId,Integer projectId);

    /**
     * 通过用户id查询权限列表
     * @param id
     * @return
     */
    List<String> getListByUserId(Long id);

    /**
     * 查询企业账号的菜单列表
     * @param userId
     * @return
     */
//    Result getEnterpriseList(Long userId,Integer projectId);
}
