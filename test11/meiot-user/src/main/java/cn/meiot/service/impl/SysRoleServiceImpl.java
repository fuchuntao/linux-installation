package cn.meiot.service.impl;

import cn.meiot.entity.*;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SysRoleVo;
import cn.meiot.enums.AccountType;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.mapper.*;
import cn.meiot.service.ISysRoleService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.ErrorCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private RoleProjectPermissionMapper roleProjectPermissionMapper;


    @Override
    public Result getList(Long userId, Integer type,String keyword, Page<SysRole> page) {
        Result result = Result.getDefaultFalse();
        SysUser sysUser = sysUserMapper.selectById(userId);
        if( null == sysUser || null == sysUser.getType()){
            result.setMsg(ErrorCodeUtil.NOT_FOUND_ACCOUNT_TYPE);
            return result;
        }
        if(sysUser.getType() != type){
            result.setMsg(ErrorCodeUtil.PROJECT_IS_NOT_EXIST);
            return result;
        }
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<SysRole>();
        queryWrapper.eq("type",type);
        queryWrapper.eq("is_super_admin",0);
        if(!StringUtils.isEmpty(keyword)){
            queryWrapper.in("name",keyword);
        }
        if(sysUser.getType() == AccountType.ENTERPRISE.value()){
            queryWrapper.eq("belong_user_id",sysUser.getBelongId() == 0 ? sysUser.getId() : sysUser.getBelongId());
        }
        IPage<SysRole> sysRoleIPage = sysRoleMapper.selectPage(page, queryWrapper);
        if(null == sysRoleIPage || null == sysRoleIPage.getRecords()){
        return Result.getDefaultTrue();
        }
        for(SysRole sysRole :sysRoleIPage.getRecords()){
            //获取绑定此角色的账号数量
            Integer count = sysUserRoleMapper.selectCount(new QueryWrapper<SysUserRole>().eq("sys_role_id",sysRole.getId()));
            sysRole.setUserNum(count);
        }
        result = Result.getDefaultTrue();
        result.setData(sysRoleIPage);
        return result;
    }

    @Override
    public Result saveRole(SysRoleVo sysRoleVo) {
        Result result = Result.getDefaultFalse();
        SysUser sysUser = sysUserMapper.selectById(sysRoleVo.getUserId());
        if(null == sysUser || null == sysRoleVo.getType()){
            result.setMsg(ErrorCodeUtil.USER_NOT_EXIST);
            return result;
        }
        if(sysRoleVo.getType() != sysUser.getType()){
            result.setMsg(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            return result;
        }
        SysRole sysRole = SysRole.builder().status(ConstantsUtil.NORMAL_ROLE_STATUS)
                .type(sysRoleVo.getType()).name(sysRoleVo.getName()).build();
        if(AccountType.ENTERPRISE.value() == sysRoleVo.getType()){
            sysRole.setBelongUserId(sysUser.getBelongId() == 0 ? sysUser.getId() : sysUser.getBelongId());
        }

        Integer count = sysRoleMapper.insert(sysRole);
        if(count == 1){
            return Result.getDefaultTrue();
        }
        return result;
    }

    @Override
    public Result edit(SysRoleVo sysRoleVo) {
        //判断是否存在越权操作
        Result result = verify(sysRoleVo.getUserId(),sysRoleVo.getId());
        if(!result.isResult()){
            return result;
        }
        //修改
        SysRole sysRole = SysRole.builder().name(sysRoleVo.getName()).build();
        Integer  count = sysRoleMapper.update(sysRole, new UpdateWrapper<SysRole>().eq("id", sysRoleVo.getId()).eq("is_super_admin",0));
        if(count == 1){
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }

    @Override
    @Transactional
    public Result deleteRole(Integer id, Long userId) throws Exception {
        //判断是否存在越权操作
        Result result = verify(userId,id);
        if(!result.isResult()){
            return result;
        }
        //查询此角色是否已被使用
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("roleId",id);
        Integer count =sysUserRoleMapper.selectCountByMap(map);
        if(null != count && count > 0 ){
            result = Result.getDefaultFalse();
            //result.setMsg("此角色已被使用，无法删除");
            result.setMsg(ErrorCodeUtil.ROLE__USING_NOT_DEL);
            return result;
        }
        //判断删除的是平台角色还是企业人角色
        // TODO 删除角色表
        count = sysRoleMapper.delete(new UpdateWrapper<SysRole>().eq("is_super_admin",0).eq("id",id));
        if(count == 1){
            if(result.getData() == AccountType.ENTERPRISE.value()){
                //删除角色项目权限表信息
                roleProjectPermissionMapper.delete(new UpdateWrapper<RoleProjectPermission>().lambda().eq(RoleProjectPermission::getRoleId,id));
                //删除组织架构的角色信息
                Result delete = deviceFeign.delete(id);
                if(!delete.isResult()){
                    throw  new MyServiceException(ErrorCodeUtil.DELETE_ORG_CHART_ROLE_FAILD);
                }
            }else{
                //删除角色权限表中的信息
                sysRolePermissionMapper.delete(new UpdateWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getSysRoleId, id));
            }
        }

        return Result.getDefaultTrue();
    }

    @Override
    public String queryNameByUserId(Long id) {
        return sysRoleMapper.queryNameByUserId(id);
    }

    /**
     * 校验
     * @param userId 用户id
     * @param type 账户类型
     * @return
     */
    private Result check(Long userId, Integer type){
        Result result = Result.getDefaultFalse();
        //获取账户类型
        Integer accountType = sysUserMapper.selectTypeById(userId);
        if (null == accountType) {
            //result.setMsg("管理账户类型错误");
            result.setMsg(ErrorCodeUtil.ADMIN_TYPE_ERROR);
            return result;
        }
        //判断用户是否具有查看角色的权限
        if (accountType != type && accountType != ConstantsUtil.ACCOUNT_TYPE) {
            //result.setMsg("你没有权限操作此功能");
            result.setMsg(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            return result;
        }
        return Result.getDefaultTrue();
    }

    /**
     * 校验
     * @param userId
     * @param id
     * @return
     */
    private Result verify (Long userId,Integer  id){
        Result result = Result.getDefaultFalse();
        //查询此角色属于什么账户类型下
        SysUser sysUser = sysUserMapper.selectById(userId);

        //获取当前角色所属的类型
        SysRole sysRole = sysRoleMapper.selectById(id);
        if(null == sysRole){
            log.info("未找到此角色");
            result.setMsg(ErrorCodeUtil.NOT_FOUND_ROLE);
            return result;
        }
        if( null == sysUser){
            log.info("未找到用户信息或者类型");
            result.setMsg(ErrorCodeUtil.USER_NOT_EXIST);
            return result;
        }
        if(sysUser.getType() != sysRole.getType()){
            log.info("请勿越权操作！！！");
            result.setMsg(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            return result;
        }
        if(sysRole.getType() == AccountType.ENTERPRISE.value()){
            if(!sysUser.getId().equals(sysRole.getBelongUserId())  && !sysRole.getBelongUserId().equals( sysUser.getBelongId())){
                log.info("请勿越权操作！！！");
                result.setMsg(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
                return result;
            }
        }
        result = Result.getDefaultTrue();
        result.setData(sysRole.getType());
        return result;
    }

}
