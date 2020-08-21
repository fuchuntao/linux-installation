package cn.meiot.service.impl.pc;

import cn.meiot.entity.*;
import cn.meiot.entity.vo.PermissionVo;
import cn.meiot.entity.vo.QueryProjectVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.mapper.*;
import cn.meiot.service.pc.IProjectPermissionService;
import cn.meiot.service.pc.IProjectService;
import cn.meiot.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-19
 */
@Slf4j
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private ProjectTypeMapper projectTypeMapper;

    @Autowired
    private ProjectPermissionMapper projectPermissionMapper;

    @Autowired
    private ProjectTypePermissionMapper projectTypePermissionMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private IProjectPermissionService projectPermissionService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MenuTreeUtil menuTreeUtil;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RoleProjectMapper roleProjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DeviceFeign deviceFeign;


    @Override
    public Result getList(QueryProjectVo projectVo) {
        Result result = Result.getDefaultTrue();
        Map<String, Object> map = new HashMap<String, Object>();
        Integer count = projectMapper.getListCount(projectVo);
        if (null == count || count == 0) {
            map.put("list", 0);
            map.put("count", null);
            return result;
        }
        List<Project> list = projectMapper.getList(projectVo);
        map.put("list", list);
        map.put("count", count);
        result.setData(map);
        return result;
    }

    @Override
    @Transactional
    public Result addProject(Project project) {
        check(project);
        Integer count = projectMapper.insert(project);
        if (null == count || count == 0) {
            return new Result().Faild(ErrorCodeUtil.ADD_PROJECT_ERROR);
        }
        setUserProhect(project);
        return Result.getDefaultTrue();
    }

    @Override
    public Result updateProject(Project project) {
        check(project);
        project.setEnterpriseId(null);
        project.setUpdateTime(ConstantsUtil.DF.format(new Date()));
        redisUtil.deleteHashKey(RedisConstantUtil.PROJECT_NAMES,project.getId().toString());
        Integer count = projectMapper.updateById(project);
        if (null == count || count == 0) {
            return new Result().Faild(ErrorCodeUtil.UPDATE_PROJECT_ERROR);
        }
        //setUserProhect(project);

        return Result.getDefaultTrue();
    }

    /**
     * 设置账号项目关系
     *
     * @param project
     */
    private void setUserProhect(Project project) {
        //通过企业id获取到用户主账号id
        Long userId = sysUserMapper.queryMainIdByEnterpriseId(project.getEnterpriseId());
        if (null == userId) {
            throw new MyServiceException(ErrorCodeUtil.NOT_FOUND_ENT_ACCOUNT);
        }
        //删除当前项目的所有用户
        //userProjectMapper.delete(new UpdateWrapper<UserProject>().lambda().eq(UserProject::getProjectId,project.getId()));
        UserProject userProject = UserProject.builder().userId(userId).projectId(project.getId()).build();
        Integer count = userProjectMapper.insert(userProject);
        if (null == count || count == 0) {
            throw new MyServiceException(ErrorCodeUtil.ADD_USER_PEOJECT_ERROR);
        }
    }

    @Override
    public Result projectPermissionlist(Integer id) {
        Result result = Result.getDefaultTrue();
        Project project = projectMapper.selectById(id);
        if (null == project) {
            log.info("项目id：{}不存在");
            return new Result().Faild(ErrorCodeUtil.PROJECT_IS_NOT_EXIST);
        }
        Map<String, Object> map = new Hashtable<String, Object>();
        List<SysPermission> list = new ArrayList<SysPermission>();
        //获取选中的按钮权限id
        List<Long> checked = projectTypePermissionMapper.queryCheckedButton(id);
        //通过项目类型获取到所有的权限
        List<Integer> perIds = projectTypePermissionMapper.selectIdsBypTypeId(project.getProjectType());
        if (null != perIds && perIds.size() > 0) {
            list = projectPermissionMapper.projectPermissionlist(id, perIds);
        }
        list = menuTreeUtil.menuList(list);
        map.put("list", list);
        map.put("checked", checked);
        result.setData(map);
        return result;

    }

    @Override

    public Result setPermission(PermissionVo permissionVo) {
        Result result = setPer(permissionVo);
        if(result.isResult()){
            //删除企业账号中多余的权限
            Map<String,Object> map = (Map<String, Object>) result.getData();
            rabbitTemplate.convertAndSend(QueueConstantUtil.DELETE_SURPLUS_ENTERPRISE_PERMISSION, map);
            return Result.getDefaultTrue();
        }
        return  result;
    }

    @Transactional
    public Result setPer(PermissionVo permissionVo){
        //校验项目类型是否存在
        Integer count = projectMapper.selectCount(new QueryWrapper<Project>().eq("id", permissionVo.getId()));
        if (0 == count) {
            log.info("设置权限的项目id：{}", permissionVo.getId());
            return new Result().Faild(ErrorCodeUtil.PROJECT_IS_NOT_EXIST);
        }
        //获取之前的旧权限
        List<Integer> oldPermission = projectPermissionMapper.findpermissionIds(permissionVo.getId());
        //删除之前的旧数据
        projectPermissionMapper.delete(new QueryWrapper<ProjectPermission>().eq("project_id", permissionVo.getId()));
        Map<String,Object> map = new HashMap<String, Object>();
        if (null != permissionVo.getPerimissions() && permissionVo.getPerimissions().size() > 0) {
            //校验权限id是否合法
            List<Integer> permission = sysPermissionMapper.selectIdsByIds(permissionVo.getPerimissions());
            if (null == permission || permission.size() == 0) {
                throw new MyServiceException(ErrorCodeUtil.NOT_LEGITIMATE_PERMISSION);
            }
            List<ProjectPermission> list = new ArrayList<ProjectPermission>();
            for (Integer id : permission) {
                ProjectPermission projectPermission = new ProjectPermission();
                projectPermission.setProjectId(permissionVo.getId());
                projectPermission.setSysPermissionId(id);
                list.add(projectPermission);
            }
            boolean flag = projectPermissionService.saveBatch(list);
            map.put("newPermission",permission);
            if (!flag) {
                return new Result().Faild(ErrorCodeUtil.SAVE_ERROR);
            }
        }
        map.put("oldPermission",oldPermission);
        map.put("projectId",permissionVo.getId());
        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }


    @Override
    public Result getListById(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (null == sysUser) {
            return new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        List<Map<String, Object>> list = null;
        // TODO 目前获取主账户的所有项目，
        //list = projectMapper.selectListByEnId(sysUser.getEnterpriseId());
        if (sysUser.getIsAdmin() == 1) {
            //获取所有项目
            list = projectMapper.selectListByEnId(sysUser.getEnterpriseId());
        } else {
            //获取当前用户的角色列表
            List<Integer> roles = sysUserRoleMapper.selectRoleByUserId(sysUser.getId());
            if (null == roles || roles.size() == 0) {
                return new Result().Faild(ErrorCodeUtil.USER_NOT_ROLE);
            }
            //通过角色获取到项目信息
            list = projectMapper.getListByRoles(roles);
        }
        Result result = Result.getDefaultTrue();
        result.setData(list);
        return result;
    }

    @Override
    public Result exportProject(QueryProjectVo projectVo, HttpServletResponse response) {
        //需要导出的内容
        List<Project> list = projectMapper.selectListByCondition(projectVo);
        if (null == list || list.size() == 0) {
            return new Result().Faild(ErrorCodeUtil.EXPROT_IS_NOT_NULL);
        }
        list.forEach(project -> {
            // TODO 数据是写死的，待优化
            Integer total = deviceFeign.queryDeviceTotal(project.getId());
            project.setDeviceNum(total);
        });
        ExcelUtils.export(list, "项目列表", response, Project.class);
        return null;
    }

    @Override
    public Result queryList(Long userId) {
        Result result = Result.getDefaultTrue();

        SysUser sysUser = sysUserMapper.selectById(userId);
        if (null == sysUser) {
            return new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        //获取所有的项目列表
        List<Map<String, Object>> projects = projectMapper.queryProjectByEnterpriseId(sysUser.getEnterpriseId());
        result.setData(projects);

        return result;
    }

    @Override
    public Result cutProjectId(Long userId, Integer projectId) {
        SysUser user = sysUserMapper.selectById(userId);
        List<String> permissions = null;
        List<Integer> ids = null;
        Result result = Result.getDefaultFalse();
        result.setCode("-2");
        if (user.getIsAdmin() == 1) {
            ids = projectMapper.selectIdsByEnterpriseId(user.getEnterpriseId());
            boolean flag = checkProjectId(ids, projectId);
            if(!flag){
                result.setMsg(ErrorCodeUtil.NOT_HAVE_PROJECT_PERMISSION);
                return result;
            }
            permissions = sysPermissionMapper.queryIdsByEnterpriseId(projectId);
        } else {
            //获取当前用户的所有角色
            List<Integer> roleIds = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES + userId);
            if (null == roleIds || roleIds.size() == 0) {
                return new Result().Faild("用户没有角色");
            }
            ids = roleProjectMapper.selectprojectIdsByRoles(roleIds);
            boolean flag = checkProjectId(ids, projectId);
            if(!flag){
                result.setMsg(ErrorCodeUtil.NOT_HAVE_PROJECT_PERMISSION);
                return result;
            }
            permissions = sysPermissionMapper.selectUrlByRoleAndProjectId(roleIds, projectId);
        }
        if (null != permissions) {
            redisTemplate.opsForValue().set(RedisConstantUtil.USER_PERMISSIONS + user.getId(), permissions);
            redisTemplate.opsForHash().put(RedisConstantUtil.DEFAULT_PROJECT,user.getId().toString(),projectId);
        }
        return Result.getDefaultTrue();
    }

    @Override
    public String queryProjectNameById(Integer projectId) {
        //加入读锁
        Lock readLock = ReadWriteLockUtil.readLock(ReadWriteLockUtil.projectNameLock);
        try {
            readLock.lock();
            String projectName = getvalueByRedis(projectId);
            if (!StringUtils.isEmpty(projectName)) {
                return projectName;
            }
        } finally {
            //释放读锁
            readLock.unlock();
        }
        //从数据库中获取项目名称
        //获取写锁
        Lock writeLock = ReadWriteLockUtil.writeLock(ReadWriteLockUtil.projectNameLock);
        try {
            writeLock.lock();
            return selectNameById(projectId);
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    public Map<String, String> queryProNameByProjectId(Integer projectId) {
        //根据项目id获取项目名称
        String projectName = queryProjectNameById(projectId);
        //获取企业名称
        String enterpriseName = queryEnterpriseName(projectId);

        Map<String,String> map = new HashMap<String,String>();
        map.put("projectName",projectName);
        map.put("enterpriseName",enterpriseName);
        return map;
    }

    /**
     * 通过项目id查询企业id
     * @param projectId
     * @return
     */
    private String queryEnterpriseName(Integer projectId){
        return  projectMapper.queryEnterpriseName(projectId);
    }



    /**
     * 从数据库中获取项目名称
     *
     * @param projectId
     * @return
     */
    private String selectNameById(Integer projectId) {
        //从缓存中获取数据
        String projectName = getvalueByRedis(projectId);
        if (StringUtils.isEmpty(projectName)) {
            projectName = projectMapper.queryProjectNameById(projectId);
        }
        //将数据存入redis
        redisUtil.saveHashValue(RedisConstantUtil.PROJECT_NAMES,projectId.toString(),projectName);
        return projectName;
    }

    /**
     * 在缓存中获取数据
     *
     * @param projectId
     * @return
     */
    private String getvalueByRedis(Integer projectId) {
        String projectName = redisUtil.stringValue(RedisConstantUtil.PROJECT_NAMES, projectId.toString());
        if (null != projectName) {
            return projectName;
        }
        return null;
    }


    private boolean checkProjectId(List<Integer> ids, Integer projectId) {
        if (!ids.contains(projectId)) {
//            throw new MyServiceException("您没有当前项目项目权限", "您没有当前项目项目权限");
            return false;
        }
        return true;
    }


    /**
     * 校验
     *
     * @param project
     * @return
     */
    private void check(Project project) {
        //判断项目是否已经存在
        Integer count = 0;
        if (null == project.getId()) {
            count = projectMapper.selectCount(new QueryWrapper<Project>().eq("project_name", project.getProjectName()));
            if (null != count && count > 0) {
                throw new MyServiceException(ErrorCodeUtil.PROJECT_IS_EXIST);
            }
        } else {
            //判断项目是否存在
            Project byId = projectMapper.selectById(project.getId());
            if (null == byId) {
                throw new MyServiceException(ErrorCodeUtil.PROJECT_IS_NOT_EXIST);
            }
            //判断项目是否重名
            if (!byId.getProjectName().equals(project.getProjectName())) {
                //从数据库中获取
                Integer id = projectMapper.selectIdByProjectName(project.getProjectName());
                if (null != id && id != project.getId()) {
                    throw new MyServiceException(ErrorCodeUtil.PROJECT_IS_NOT_EXIST);
                }
            }
            //判断项目类型是否发生了改变
            if (byId.getProjectType() != project.getProjectType()) {
                if (projectPermissionMapper.selectCount(new QueryWrapper<ProjectPermission>().lambda().eq(ProjectPermission::getProjectId, project.getId())) > 0) {
                    //清空当前项目的所有权限
                    boolean flag = projectPermissionService.remove(new UpdateWrapper<ProjectPermission>().lambda().eq(ProjectPermission::getProjectId, byId.getId()));
                    if (!flag) {
                        throw new MyServiceException(ErrorCodeUtil.DELETE_PROJECT_ROLE_ERROR);
                    }
                }
            }
        }
        //判断所属企业是否存在
        count = enterpriseMapper.selectCount(new QueryWrapper<Enterprise>().eq("id", project.getEnterpriseId()));
        if (null == count || count == 0) {
            throw new MyServiceException(ErrorCodeUtil.NOT_FOUND_ENT_ACCOUNT);
        }
        //判断项目类型是否存在
        count = projectTypeMapper.selectCount(new QueryWrapper<ProjectType>().eq("id", project.getProjectType()));
        if (null == count || count == 0) {
            throw new MyServiceException(ErrorCodeUtil.PROJECT_TYPE_IS_NOT_EXIST);
        }
    }
}
