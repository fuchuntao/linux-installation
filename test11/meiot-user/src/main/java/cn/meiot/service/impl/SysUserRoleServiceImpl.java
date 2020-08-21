package cn.meiot.service.impl;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.SysUserRole;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SysUserRoleVo;
import cn.meiot.enums.AccountType;
import cn.meiot.mapper.SysRoleMapper;
import cn.meiot.mapper.SysUserMapper;
import cn.meiot.mapper.SysUserRoleMapper;
import cn.meiot.service.ISysUserRoleService;
import cn.meiot.utils.AccountTypeUtil;
import cn.meiot.utils.ErrorCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@Service
@Slf4j
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;


    @Override
    @Transactional
    public Result add(SysUserRoleVo sysUserRoleVo,Long adminId) {
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("id", adminId));
        //获取被操作人的账户类型
        Integer userType = sysUserMapper.selectTypeById(sysUserRoleVo.getUserId());
        Result result = AccountTypeUtil.check(sysUser.getType(), userType);
        if (!result.isResult()) {
            log.info("=========>{}",result.getMsg());
            return result;
        }
        //删除旧数据
        sysUserRoleMapper.delete(new UpdateWrapper<SysUserRole>().eq("sys_user_id", sysUserRoleVo.getUserId())
                .eq("sys_user_type", userType));

        //判断对象是否为空
        if(null == sysUserRoleVo.getRoles()){
            return Result.getDefaultTrue();
        }
        //将数据添加集合中
        List<SysUserRole> list = new ArrayList<SysUserRole>();
        SysUserRole sysUserRole = null;
        for(Integer roleid : sysUserRoleVo.getRoles()){
            sysUserRole = new SysUserRole();
            sysUserRole.setSysUserId(sysUserRoleVo.getUserId());//用户id
            sysUserRole.setSysRoleId(roleid);
            sysUserRole.setSysUserType(sysUserRoleVo.getType());
            list.add(sysUserRole);
        }
        log.info("需要保存的用户角色数据为：{}",list);
        boolean flag = this.saveBatch(list);
        if (flag) {
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();

    }

    @Override
    public Result getListByUserId(Long adminId, Long userId) {
        SysUser adminInfo = sysUserMapper.selectById(adminId);
        //获取被操作人的账户类型
        SysUser userInfo = sysUserMapper.selectById(userId);
        Result result = AccountTypeUtil.check(adminInfo.getType(), userInfo.getType());
        if (!result.isResult()) {
            return result;
        }
        if(AccountType.PLATFORM.value() != userInfo.getType()){
            if(userInfo.getBelongId() != adminInfo.getBelongId() &&  userInfo.getBelongId() != adminInfo.getId()){
                return new Result().Faild(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            }
        }
        //查询
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(new QueryWrapper<SysUserRole>().eq("sys_user_id", userId)
                .eq("sys_user_type", userInfo.getType()));
        result.setData(sysUserRoles);
        return result;
    }

    @Override
    public List<Integer> getRoleList(Long userId) {


        return sysUserRoleMapper.getRoleList(userId);
    }

    @Override
    public List<Long> getUserIdByRoleId(Integer roleId) {
        return sysUserRoleMapper.selectUserIdByRoleId(roleId);
    }

    /**
     * 校验此操作是否合法
     *
     * @param adminType  操作员的账号类型
     * @param userType  被操作员的账号类型
     * @return Result
     *//*
    public Result check(Integer adminType, Integer userType) {
        Result result = Result.getDefaultFalse();
        //获取操作用户的账户类型
        if (null == adminType || null == userType) {
            log.info("操作员或用户的类型有误,操作员类型：{}，被操作员类型{}", adminType, userType);
            result.setMsg("操作员或用户的类型有误");
            return result;
        }
        if (adminType != userType && adminType != ConstantsUtil.ACCOUNT_TYPE) {
            result.setMsg("您未有此接口权限");
            return result;
        }
        return Result.getDefaultTrue();
    }*/

}
