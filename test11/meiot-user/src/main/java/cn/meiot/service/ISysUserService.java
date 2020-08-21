package cn.meiot.service;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-07-29
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 通过账号查询用户信息
     * @param account
     * @return
     */
    SysUser selectUserByUserName(String account,Integer type);

    /**
     * 重置（修改）密码
     * @param resetPwd
     * @return
     */
    Result resetPwd(ResetPwd resetPwd);

    /**
     * 新增用户
     * @param newSysUserVo
     * @return
     */
    Result addUser(NewSysUserVo newSysUserVo);

    /**
     * 修改用户信息
     * @param newSysUserVo
     * @return
     */
    Result updateUser(NewSysUserVo newSysUserVo);

    /**
     * 根据类型获取用户列表
     * @param userId
     * @param type
     * @return
     */
    //Result getList(Long userId, Integer type, Page<SysUser> Page);

    /**
     * 修改昵称
     * @param userVo
     * @return
     */
    Result editNikname(UserVo userVo);

    /**
     * 获取企业用户列表
     * @param userId
     * @return
     */
    Result getEnterpriseList(Long userId, Integer currentPage,Integer pageSize,String keyWord);

    /**
     * 查询个人列表
     * @param userId
     * @param
     * @return
     */
    Result getPersonList(Long userId, Integer currentPage,Integer pageSize,String keyWord);

    /**
     * 新增平台用户
     * @param sysUserVo
     * @return
     */
    Result addSysUser(SysUserVo sysUserVo,Long userId);

    /**
     * 修改平台用户信息
     * @param sysUserVo
     * @return
     */
    Result updateSysUser(SysUserVo sysUserVo,Long userId);

    /**
     * 获取平台所有账户
     * @param map
     * @return
     */
    Result getAdminList(Map<String, Object> map,Long userId);

    /**
     * 删除企业账户
     * @param userId
     * @return
     */
    Result deleteEnUser(Long userId);

    /**
     * 通过账号查询状态
     * @param belongId
     * @return
     */
    Integer getStatusByUserId(Long belongId);


    String getHeadPortrait(String headPortrait);

    /**
     * 校验用户名密码是否正确
     * @param account
     * @param password
     * @return
     */
    Result chechPassword(String account, String password);
}
