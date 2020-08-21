package cn.meiot.dao;

import cn.meiot.entity.SysMenu;
import cn.meiot.entity.SysPermission;
import cn.meiot.entity.SysUser;
import cn.meiot.entity.UserProject;
import cn.meiot.entity.bo.AuthUserBo;

import java.util.List;

public interface LoginRedisDao {
    /**
     * 将用户信息保存到redis中
     * @param authUserBo    用户信息
     * @param secretKey    密钥
     */
    void saveToken( AuthUserBo authUserBo,String secretKey);

    /**
     * 通过token获取用户信息
     * @param redisKey
     * @return
     */
    SysUser getUserInfoByToken(String redisKey, String device);

    /**
     * 保存模块
     * @param sysMenus
     */
    void saveMenu(List<SysPermission> sysMenus, Long userId);

    /**
     * 保存用户所参与的所有项目
     * @param projects
     * @param userId
     */
    void saveProject(List<UserProject> projects, Long userId);

    /**
     * 清楚登录信息
     * @param userId
     */
    void clearLoginInfo(Long userId);

    /**
     * 保存权限信息
     * @param list
     * @param id
     */
    void savePersission(List<String> list, Long id);
}
