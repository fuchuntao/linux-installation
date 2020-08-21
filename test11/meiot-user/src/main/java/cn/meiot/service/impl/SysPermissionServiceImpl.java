package cn.meiot.service.impl;

import cn.meiot.entity.*;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.RolePermissionVo;
import cn.meiot.enums.AccountType;
import cn.meiot.mapper.*;
import cn.meiot.service.ISysPermissionService;
import cn.meiot.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-05
 */
@Service
@Slf4j
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;
    @Autowired
    private RoleProjectPermissionMapper roleProjectPermissionMapper;

    @Autowired
    private MenuTreeUtil menuTreeUtil;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public Result list(Long userId, Integer type) {
        Result result = Result.getDefaultFalse();
        //获取管理员账户类型
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("id", userId));
        log.info("用户id：{}的账户类型为：{}，查询列表的类型为：{}", userId, sysUser.getType(), type);
        //判断是否具备权限
        if (sysUser.getType() != ConstantsUtil.ACCOUNT_TYPE && sysUser.getType() != type) {
            log.info("您不具备此操作的权限");
            //result.setMsg("您不具备此操作的权限");
            result.setMsg(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            return result;
        }
        List<SysPermission> list = null;
        //如果是平台账户操作
        if (sysUser.getType() == ConstantsUtil.ACCOUNT_TYPE) {
            list = sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().eq("type", type));
        } else {
            list = getPermission(sysUser.getBelongId(), type);

        }
        result = Result.getDefaultTrue();
        result.setData(list);
        return result;
    }


    public List<SysPermission> getPermission(Long userId, Integer type) {
        //获取各自超级管理员所具有的权限列表
        List<SysPermission> list = null;
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(new QueryWrapper<SysUserRole>()
                .eq("sys_user_id", userId).eq("sys_user_type", type));
        //通过角色id获取权限id
        if (null == sysUserRoles) {
            return null;
        }
        List<Integer> ids = new ArrayList<Integer>();
        for (SysUserRole s : sysUserRoles) {
            ids.add(s.getSysRoleId());
        }
        //通过角色id获取权限id列表
        List<SysRolePermission> sys_role_id = sysRolePermissionMapper.selectList(new QueryWrapper<SysRolePermission>()
                .in("sys_role_id", ids));
        if (sys_role_id == null) {
            return null;
        }
        ids = new ArrayList<Integer>();
        for (SysRolePermission s : sys_role_id) {
            ids.add(s.getSysRoleId());
        }
        //通过权限id查询权限列表信息
        list = sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().in("id", ids));
        return list;
    }

    @Override
    public Result listMenu(Long userId, Integer roleId) {
        //获取操作员的账户类型
        Integer adminType = sysUserMapper.selectTypeById(userId);
        Result result = AccountTypeUtil.check(adminType, AccountType.PLATFORM.value());
        if (!result.isResult()) {
            return result;
        }

        Map<String,Object> map =new HashMap<String,Object>();
        //获取选中的权限id
        List<Integer> permissions = sysRolePermissionMapper.queryCheckedButton(roleId);
        map.put("checked",permissions);
        List<SysPermission> sysPermissions = sysPermissionMapper.queryPermissions();
        sysPermissions = menuTreeUtil.menuList(sysPermissions);
        map.put("list",sysPermissions);
        result.setData(map);
        return result;
    }

    @Override
    public Result enterpriseListById(Long adminId, Integer roleId, Integer projectId) {
//        Result result = Result.getDefaultFalse();
//        //获取操作员的账户类型
//        SysUser sysUser = sysUserMapper.selectById(adminId);
//        if(ConstantsUtil.ENTERPRISE_ACCOUNT != sysUser.getType()){
//            //result.setMsg("越权操作");
//            result.setMsg(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
//            return result;
//        }
//        //通过项目id查询企业id
//        Integer enterpriseId = projectMapper.selectEnterpriseIdById(projectId);
//        if(null == enterpriseId || sysUser.getEnterpriseId() != enterpriseId){
//            return new Result().Faild("请勿越权操作");
//        }
//        Map<String,Object> map = new HashMap<>();
//        //查询已经勾选的权限id
//        List<Integer> checked = roleProjectPermissionMapper.queryCheckedButton(roleId,projectId);
//        map.put("checked",checked);
//        //通过用户id获取到
//        List<SysPermission> list = sysPermissionMapper.queryListByProjectId(projectId);
//        list = menuTreeUtil.menuList(list);
//        map.put("list",list);
//        result = Result.getDefaultTrue();
//        result.setData(map);
//        return result;
        return null;
    }

    @Override
    public List<String> queryEnPermissionIds(SysUser user,List<Integer> roleIds,Integer projectId) {
        List<String> permissions = null;
        //判断当前用户是否是主账号
        if(user.getIsAdmin() == 1 ){
            //查询所有权限
            permissions = sysPermissionMapper.queryIdsByEnterpriseId(projectId);
        }else{
            permissions = sysPermissionMapper.selectUrlByRoleAndProjectId(roleIds,projectId);
        }
        if(null != permissions){
            redisTemplate.opsForValue().set(RedisConstantUtil.USER_PERMISSIONS+user.getId(),permissions);
            redisTemplate.opsForHash().put(RedisConstantUtil.DEFAULT_PROJECT,user.getId().toString(),projectId);
        }

        return null;
    }

    @Override
    public void setRunPlatformPermission(SysUser user, List<Integer> list) {
        List<String> permissions = null;
        //判断当前用户是否是超级管理员
        if(user.getIsAdmin().equals(1) ){
            //查询所有权限
            permissions = sysPermissionMapper.selectAllUrl();
        }else{
            permissions  = sysPermissionMapper.selectUrlByRoles(list);
        }
       log.info("当前用户id：{},所属角色：{}，所有用的权限url：{}",user.getId(),list,permissions);
        if(null != permissions && permissions.size() > 0 ){
            redisTemplate.opsForValue().set(RedisConstantUtil.USER_PERMISSIONS+user.getId(),permissions);
        }

    }

}
