package cn.meiot.service.impl;

import cn.meiot.entity.*;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.AccountType;
import cn.meiot.mapper.*;
import cn.meiot.service.ISysMenuService;
import cn.meiot.service.pc.IRoleProjectService;
import cn.meiot.utils.ConstantUtil;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.MenuTreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@Service
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    private RoleProjectPermissionMapper roleProjectPermissionMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private ProjectPermissionMapper projectPermissionMapper;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private IRoleProjectService roleProjectService;




    @Override
    public Result getList(Long userId, Integer projectId) {
        Result result = Result.getDefaultTrue();
        //通过用户id获取此用户的类型
        Integer type = sysUserMapper.selectTypeById(userId);
        // TODO 通过用户id查询到此用户拥有多少角色
        QueryWrapper<SysUserRole> role = new QueryWrapper<SysUserRole>().eq("sys_user_id",userId);

        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(role);
        if(null == sysUserRoles){
            return result;
        }
        List<Integer> ids = new ArrayList<Integer>();
        for(SysUserRole s : sysUserRoles){
            ids.add(s.getSysRoleId());
        }

        List<Integer> permissionIds = new ArrayList<Integer>();
        if(type == 1){   //平台用户
            //TODO 通过角色id获此拥有多少权限
            QueryWrapper<SysRolePermission> permission = new QueryWrapper<SysRolePermission>().in("sys_role_id",ids);
            List<SysRolePermission> sysRolePermissions = sysRolePermissionMapper.selectList(permission);
            if(null == sysRolePermissions){
                return result;
            }
            for(SysRolePermission s : sysRolePermissions){
                permissionIds.add(s.getSysPermissionId());
            }
        }else{  //其他用户，通过项目关联权限

            // TODO 通过角色id与项目id查询权限id
            List<RoleProjectPermission> roleProjectPermissions = roleProjectPermissionMapper.
                    selectList(new QueryWrapper<RoleProjectPermission>().eq("project_id", projectId).in("role_id", ids));
            if(null == roleProjectPermissions){
                return result;
            }
            for(RoleProjectPermission s : roleProjectPermissions){
                permissionIds.add(s.getPermissionId());
            }

        }
        // TODO 通过权限找到对应的模块
        QueryWrapper<SysMenu> menu = new QueryWrapper<SysMenu>().eq("menu_type",type).in("id",ids);
        List<SysMenu> sysMenus = sysMenuMapper.selectList(menu);
        log.info("=====>{}",sysMenus.toString());
        if(null == sysMenus){
            return result;
        }
        result.setData(menu);
        return result;
    }

    @Override
    public Result listByType(Long userId, Integer type,Integer projectId) {
        Result result = Result.getDefaultTrue();
        if(type == ConstantsUtil.ACCOUNT_TYPE){
            log.info("越权获取管理平台的菜单");
           // result.setMsg("权限不足");
            result.setMsg(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            return result;
        }
        QueryWrapper<SysMenu> sysMenu = new QueryWrapper<SysMenu>().eq("menu_type",type);
        List<SysMenu> sysMenus = sysMenuMapper.selectList(sysMenu);
        result = Result.getDefaultTrue();
        result.setData(sysMenus);
        return result;
    }

    @Override
    public Result getPlatList(Long userId,Integer projectId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if(null == sysUser){
            return  new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        //菜单
        List<SysPermission> menu = new ArrayList<SysPermission>();
        //按钮
        List<SysPermission> button = new ArrayList<SysPermission>();
        //判断当前用户类型
        try{
            if(AccountType.PLATFORM.value() == sysUser.getType()){
                //判断当前用户是否是超级管理员
                if(sysUser.getIsAdmin() == 1){
                    //获取平台的所有菜单列表
                    menu = sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getType, 1).eq(SysPermission::getPType, 1));
                    button = sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getType, 1).eq(SysPermission::getPType, 2));
                }else{
                    //获取当前用户的角色
                    List<Integer> roles = sysUserRoleMapper.selectRoleByUserId(userId);
                    //获取当前角色所拥有的权限id
                    List<Integer> permissions = sysRolePermissionMapper.selectPermissionIdByRoles(roles);
                    //通过权限id获取权限列表
                    menu = sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getType, 1).in(SysPermission::getId,permissions).eq(SysPermission::getPType, 1));
                    button = sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getType, 1).in(SysPermission::getId,permissions).eq(SysPermission::getPType, 2));
                }
            }else{
                List<Integer> permissions =null ;
                //判断当前用户是否是超级管理员
                if(sysUser.getIsAdmin() == 1){
                    //判断当前项目是否属于当前用户
                    Integer count = userProjectMapper.selectCount(new QueryWrapper<UserProject>().lambda().eq(UserProject::getUserId, userId).eq(UserProject::getProjectId, projectId));
                    if(count == 0){
                        return new Result().Faild(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
                    }
                    //根据当前项目获取权限id
                    permissions = projectPermissionMapper.findpermissionIds(projectId);
                    //通过权限id获取权限列表

                }else{
                    ////获取当前用户的角色
                    List<Integer> roles = sysUserRoleMapper.selectRoleByUserId(userId);
                    //通过角色id与项目id获取权限id
                    permissions = roleProjectPermissionMapper.selectPermissionId(roles, projectId);
                    //通过权限id获取权限列表
                }
                menu = sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getType, 2).in(SysPermission::getId,permissions).eq(SysPermission::getPType, 1));
                button =  sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getType, 2).in(SysPermission::getId,permissions).eq(SysPermission::getPType, 2));

            }

        }catch(Exception e){
            e.printStackTrace();
            log.error("错误信息为：{}",e.getMessage());
            log.error("错误发生在：{}",e.getStackTrace());
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("menu",menu);
        map.put("button",button);
        Result  result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }

    @Override
    public List<String> getListByUserId(Long id) {
        //获取当前用户所拥有的角色
        List<Integer> roles = sysUserRoleMapper.selectRoleByUserId(id);
        if(null == roles || roles.size() == 0){
            return null;
        }

        //根据当前角色获取权限id
        List<Integer> permissionIds = sysRolePermissionMapper.selectPermissionIdByRoles(roles);
        if(null == permissionIds || permissionIds.size() == 0){
            return null;
        }
        List<String> list = sysPermissionMapper.selectUriByUserId(permissionIds);
        return list;
    }

//    @Override
//    public Result getEnterpriseList(Long userId,Integer projectId) {
//        //通过用户id查询角色信息
//        List<Integer> roles = sysUserRoleMapper.selectRoleByUserId(userId);
//        if(null == roles){
//            return new Result().Faild("该账户暂无角色");
//        }
//        //通过角色与项目获取权限id
//        List<Integer> permissions = roleProjectPermissionMapper.selectPermissionId(roles,projectId);
//        if(null == permissions || permissions.size() == 0){
//            return new Result().Faild("该账户没有权限");
//        }
//        //获取菜单
//        List<SysPermission> menu = sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().lambda()
//                .in(SysPermission::getId, permissions).eq(SysPermission::getPType,1));
//        //menu = menuTreeUtil.menuList(menu);
//        //获取按钮
//        List<SysPermission> button =  sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().lambda()
//                .in(SysPermission::getId, permissions).eq(SysPermission::getPType,2));
//       // button = menuTreeUtil.menuList(button);
//        Map<String,Object> map = new HashMap<String,Object>();
//        map.put("menu",menu);
//        map.put("button",button);
//        Result result = Result.getDefaultTrue();
//        result.setData(map);
//        return result;
//    }


}
