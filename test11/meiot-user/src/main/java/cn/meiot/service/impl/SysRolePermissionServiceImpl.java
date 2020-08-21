package cn.meiot.service.impl;

import cn.meiot.entity.SysRolePermission;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.RolePermissionVo;
import cn.meiot.enums.AccountType;
import cn.meiot.exception.MyServiceException;
import cn.meiot.mapper.*;
import cn.meiot.service.IRoleProjectPermissionService;
import cn.meiot.service.ISysPermissionService;
import cn.meiot.service.ISysRolePermissionService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.QueueConstantUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements ISysRolePermissionService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public Result addPlatform(RolePermissionVo rolePermissionVo, Long adminId) {
        Result result = addPermission(rolePermissionVo, adminId);
        if(result.isResult()){
            Map<String,Object> map = (Map<String, Object>) result.getData();
            rabbitTemplate.convertAndSend(QueueConstantUtil.PERMISSION_CHECK,map);
            return Result.getDefaultTrue();
        }else{
            return result;
        }
    }

    @Transactional
    public Result addPermission(RolePermissionVo rolePermissionVo, Long adminId) {
        //获取操作员的账户类型
        Integer adminType = sysUserMapper.selectTypeById(adminId);
        if(ConstantsUtil.ACCOUNT_TYPE != adminType){
            return new Result().Faild(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
        }
        //不能设置超级管理员的权限
        Integer isAdmin = sysRoleMapper.queryIsAdminById(rolePermissionVo.getRoleId());
        if(null == isAdmin || isAdmin == 1){
            //不能设置超级管理员的权限
            return new Result().Faild(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
        }
        //获取旧权限
        List<Integer> roles = new ArrayList<Integer>();
        roles.add(rolePermissionVo.getRoleId());
        List<Integer> oldPermissions = sysRolePermissionMapper.selectPermissionIdByRoles(roles);
        //删除旧权限
        Integer count = sysRolePermissionMapper.delete(
                new UpdateWrapper<SysRolePermission>().eq("sys_role_id", rolePermissionVo.getRoleId()));
        if(null == count){
            return new Result().Faild("删除旧权限失败");
        }
        if(null == rolePermissionVo.getPerimissions() || rolePermissionVo.getPerimissions().size() == 0){
            return Result.getDefaultTrue();
        }
        //校验权限id是否合法
        List<Integer> permission = sysPermissionMapper.selectIdsByIds(rolePermissionVo.getPerimissions());
        if(null == permission || permission.size() == 0){
            throw  new MyServiceException(ErrorCodeUtil.NOT_LEGITIMATE_PERMISSION);
        }
        List<SysRolePermission> list = new ArrayList<SysRolePermission>();
        for(Integer id: permission){
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setSysRoleId(rolePermissionVo.getRoleId());
            sysRolePermission.setSysPermissionId(id);
            sysRolePermission.setType(AccountType.PLATFORM.value());
            list.add(sysRolePermission);
        }
        boolean flag = this.saveBatch(list);
        if(!flag){
            return new Result().Faild(ErrorCodeUtil.SET_PERMISSION_ERROR);
        }
        //批量保存
        //this.saveBatch(list);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("oldPermission",oldPermissions);
        map.put("newPermission",rolePermissionVo.getPerimissions());
        map.put("roleId",rolePermissionVo.getRoleId());
        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;

    }

    @Override
    public List<String> getPermission(List<Integer> roleIds) {
        return sysRolePermissionMapper.getPermission(roleIds);
    }

    @Override
    public Result addEnPermission(RolePermissionVo rolePermissionVo, Long userId) {


        return null;
    }

   /* @Override
    public Result addEnterprise(RolePermissionVo rolePermissionVo, Integer adminId) {
        Result result = Result.getDefaultFalse();
        //获取操作员的账户类型
        Integer adminType = sysUserMapper.selectTypeById(adminId);
        if(ConstantsUtil.ACCOUNT_TYPE != adminType){
            result.setMsg("越权操作");
            return result;
        }
        //删除旧权限
        roleProjectPermissionMapper.delete(new UpdateWrapper<RoleProjectPermission>()
                .eq("role_id", rolePermissionVo.getRoleId()).eq("project_id", rolePermissionVo.getProjectId()));

        //添加新权限
        List<RoleProjectPermission> list = new ArrayList<RoleProjectPermission>();
        for(Integer id: rolePermissionVo.getPerimissions()){
            RoleProjectPermission roleProjectPermission = new RoleProjectPermission();
            roleProjectPermission.setRoleId(rolePermissionVo.getRoleId());
            roleProjectPermission.setProjectId(rolePermissionVo.getProjectId());
            roleProjectPermission.setPermissionId(id);
            list.add(roleProjectPermission);
        }
        //批量保存
        Boolean falg = roleProjectPermissionService.saveBatch(list);
        if(falg){
            return Result.getDefaultTrue();
        }
        return result;
    }*/


}
