package cn.meiot.service.impl;

import cn.meiot.entity.*;
import cn.meiot.entity.vo.PermissionVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.exception.MyServiceException;
import cn.meiot.mapper.*;
import cn.meiot.service.IRoleProjectPermissionService;
import cn.meiot.service.pc.IRoleProjectService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.MenuTreeUtil;
import cn.meiot.utils.QueueConstantUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 *  服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@Service
@Slf4j
public class RoleProjectPermissionServiceImpl extends ServiceImpl<RoleProjectPermissionMapper, RoleProjectPermission> implements IRoleProjectPermissionService {

    @Autowired
    private RoleProjectPermissionMapper roleProjectPermissionMapper;

    @Autowired
    private ProjectPermissionMapper projectPermissionMapper;

    @Autowired
    private MenuTreeUtil menuTreeUtil;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private IRoleProjectService roleProjectService;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public List<String> getPermission(List<Integer> roleIds, Integer projectId) {
        return roleProjectPermissionMapper.getPermission(roleIds,projectId);
    }

    @Override
    public Result getList(Integer roleId, Integer projectId,Long userId) {
        Result result = Result.getDefaultFalse();
        //获取操作员的账户类型
        SysUser sysUser = sysUserMapper.selectById(userId);
        if(ConstantsUtil.ENTERPRISE_ACCOUNT != sysUser.getType()){
            //result.setMsg("越权操作");
            result.setMsg(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            return result;
        }
        //通过项目id查询企业id
        Integer enterpriseId = projectMapper.selectEnterpriseIdById(projectId);
        if(null == enterpriseId || sysUser.getEnterpriseId() != enterpriseId){
            return new Result().Faild(ErrorCodeUtil.SIMPLY_OVERSTEPPED_HIS_BOUNDS);
        }
        Map<String,Object> map = new HashMap<>();
        //查询已经勾选的权限id
        List<Integer> checked = roleProjectPermissionMapper.queryCheckedButton(roleId,projectId);
        map.put("checked",checked);
        //通过用户id获取到
        List<SysPermission> list = sysPermissionMapper.queryListByProjectId(projectId);
        list = menuTreeUtil.menuList(list);
        map.put("list",list);
        result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }

    @Override

    public Result setPermission(PermissionVo permissionVo,Long userId) {
        Result result = setPer(permissionVo, userId);
        log.info("返回结果：{}",result);
        if(result.isResult()){
            Map<String,Object> map = (Map<String, Object>) result.getData();
            log.info("权限校验的map：{}",map);
            if(map != null){
                rabbitTemplate.convertAndSend(QueueConstantUtil.PERMISSION_CHECK,map);
            }
            return Result.getDefaultTrue();
        }else{
            return result;
        }

    }

    @Transactional
    public Result setPer(PermissionVo permissionVo,Long userId){
        //获取当前登录用户信息
        SysUser sysUser = sysUserMapper.selectById(userId);
        //通过企业id贺项目id查询是否具备权限
        Integer count = projectMapper.selectCount(new QueryWrapper<Project>().eq("id", permissionVo.getProjectId()).eq("enterprise_id", sysUser.getEnterpriseId()));
        if(null == count || count == 0){
            log.info("根据项目id与当前用户所属的企业id未查到项目信息");
            return new Result().Faild(ErrorCodeUtil.SIMPLY_OVERSTEPPED_HIS_BOUNDS);
        }
        //获取之前的旧权限
        List<Integer> oldPermission = roleProjectPermissionMapper.selectOldPermissionId(permissionVo.getId(),permissionVo.getProjectId());
        //删除之前的数据
        roleProjectPermissionMapper.delete(new UpdateWrapper<RoleProjectPermission>().eq("project_id",permissionVo.getProjectId())
                .eq("role_id",permissionVo.getId()));
        if(null == permissionVo.getPerimissions() || permissionVo.getPerimissions().size() == 0){
            //删除角色项目表中的信息
            boolean remove = roleProjectService.remove(new UpdateWrapper<RoleProject>().eq("role_id", permissionVo.getId()).eq("project_id", permissionVo.getProjectId()));
            if(!remove){
                throw new MyServiceException(ErrorCodeUtil.DELETE_PROJECT_ERROR);
            }
            return Result.getDefaultTrue();
        }
        //过滤不存在的权限id
        List<Integer> permissions = projectPermissionMapper.getPermissionIds(permissionVo.getProjectId(),permissionVo.getPerimissions());
        if(null == permissions || permissions.size() == 0){
            return new Result().Faild(ErrorCodeUtil.INCLUDE_INVALID_PERMISSION);
        }
        //设置权限
        List<RoleProjectPermission> list = new ArrayList<RoleProjectPermission>();
        for(Integer id: permissions){
            RoleProjectPermission r = new RoleProjectPermission();
            r.setProjectId(permissionVo.getProjectId());
            r.setRoleId(permissionVo.getId());
            r.setPermissionId(id);
            list.add(r);
        }
        boolean flag= this.saveBatch(list);
        if(flag){
            //判断项目是否已经存在
            int num = roleProjectService.count(new QueryWrapper<RoleProject>().eq("role_id", permissionVo.getId()).eq("project_id", permissionVo.getProjectId()));
            if(num == 0 ){
                flag = roleProjectService.saveOrUpdate(RoleProject.builder().roleId(permissionVo.getId()).projectId(permissionVo.getProjectId()).build());
                if(!flag){
                    log.info("删除角色项目是失败");
                    throw new MyServiceException(ErrorCodeUtil.SET_PERMISSION_ERROR);
                }
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("oldPermission",oldPermission);
            map.put("newPermission",permissions);
            map.put("roleId",permissionVo.getId());

            return Result.OK(map);
        }
        throw new MyServiceException(ErrorCodeUtil.SET_PERMISSION_ERROR);
    }

    @Override
    public List<Integer> querySurplusPermission(Integer projectId) {
        return roleProjectPermissionMapper.querySurplusPermission(projectId);
    }
}
