package cn.meiot.service.impl.api;

import cn.meiot.entity.*;
import cn.meiot.entity.bo.UserNumBo;
import cn.meiot.mapper.*;
import cn.meiot.service.api.ApiService;
import cn.meiot.service.pc.IProjectService;
import cn.meiot.utils.DateUtil;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ApiServiceImpl implements ApiService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private RedisUtil  redisUtil;

    @Autowired
    private IProjectService projectService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UserOpenidMapper userOpenidMapper;

    @Autowired
    private UserUnionidMapper userUnionidMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    SysPermissionMapper sysPermissionMapper;

    @Autowired
    private RoleProjectPermissionMapper roleProjectPermissionMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private ProjectPermissionMapper projectPermissionMapper;

    @Override
    public Long getProjectDateByProjectId(Integer projectId) {

        //判断缓存中是否存在
        Long time = getCreateTime(projectId);
        if(null == time){
            String createTime = projectMapper.selectcreateTimeById(projectId);
            if(createTime == null ){
                log.info("在数据库中未获取到项目信息");
                return null;
            }
            log.info("项目id：{}在数据库中获取到了创建时间",projectId);
            //将字符串日期转成时间戳类型
            time = DateUtil.StringToTimestamp(createTime);
            //将时间存入缓存
            redisUtil.saveHashValue( RedisConstantUtil.PROJECT_CREATE_TIME,projectId.toString(),time);
        }
        return time;
    }

    @Override
    public String queryProjectNameById(Integer projectId) {


        return projectService.queryProjectNameById(projectId);
    }

    @Override
    public Map<String, String> queryProNameByProjectId(Integer projectId) {


        return projectService.queryProNameByProjectId(projectId);
    }

    @Override
    public UserNumBo getUserNum() {

        UserNumBo userNumBo = sysUserMapper.getUserNum();

        return userNumBo;
    }

    @Override
    public List<Long> getUserIdByType(Integer type) {

        return sysUserMapper.getUserByType(type);
    }

    @Override
    public String getOpenid(Long userId) {
        String openid = (String) redisUtil.getHashValue(RedisConstantUtil.USER_OPENID, userId.toString());
        if(null == openid){
            UserUnionid one = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUId, userId).eq(UserUnionid::getDeleted,0));
            if(one == null ){
                log.info("未找到用户openid，用户id：{}",userId);
                return null;
            }
            openid = one.getOpenid();
            redisUtil.saveHashValue(RedisConstantUtil.USER_OPENID, userId.toString(),openid);
        }
        return openid;
    }

    @Override
    public Integer getTypeByUserId(Long userId) {
        Integer type = sysUserMapper.selectTypeById(userId);
        if(null == type){
            log.info("用户不存在");
            return null;
        }
        redisUtil.saveHashValue(RedisConstantUtil.USER_TYPE,userId.toString(),type);
        return type;
    }


    @Override
    public List<Integer> getRoleIdByUserId(Long userId) {

        return sysUserRoleMapper.getRoleList(userId);
    }

    @Override
    public boolean checkPermission(Long userId, String permission,Integer projectId) {
        //通过用户id获取拥有的角色列表
        List<Integer> roleList = sysUserRoleMapper.getRoleList(userId);
        if(roleList == null || roleList.size() == 0){
            return false;
        }
        //通过权限唯一标识查询权限的id
        Long id = sysPermissionMapper.selectIdByPermission(permission);

        //通过权限id+角色+项目判断是否存在这个id
        LambdaQueryWrapper<RoleProjectPermission> lambda = new QueryWrapper<RoleProjectPermission>().lambda()
                .eq(RoleProjectPermission::getPermissionId, id)
                .eq(RoleProjectPermission::getProjectId, projectId);
        if(roleList.size() == 1){
            lambda.eq(RoleProjectPermission::getRoleId, roleList.get(0));
        }else{
            lambda.in(RoleProjectPermission::getRoleId,roleList);
        }
        Integer count = roleProjectPermissionMapper.selectCount(lambda);
        log.info("用户id：{}，权限唯一标识：{}，项目id：{}，角色id：{}，查询的结果：{}",userId,permission,projectId,roleList,count);
        if(count == null || count== 0 ){
            return false;
        }
        return true;
    }

    @Override
    public String getNiknameByUserId(Long userId) {
        String  value = (String) redisUtil.getHashValue(RedisConstantUtil.USER_NIKNAMES, userId.toString());
        log.info("从缓存中获取用户昵称，用户id：{},昵称：{}",userId,value);
        if(value != null ){
            return value;
        }
        //从数据库中获取昵称
        String nikname = sysUserMapper.selectNiknameById(userId);
        if(nikname != null ){
            //讲昵称存入缓存
            redisUtil.saveHashValue(RedisConstantUtil.USER_NIKNAMES,userId.toString(),nikname);
        }
        return nikname;
    }

    @Override
    public List<String> getRoleNameByUserId(Long userId) {
        //查询当前用户的角色id
        List<Integer> roleList = sysUserRoleMapper.getRoleList(userId);
        log.info("roleList:{}",roleList);
        if(roleList == null || roleList.size() == 0 ){
            return null;
        }
        //通过角色id查询用户昵称
        List<String> list = sysRoleMapper.queryNamesById(roleList);
        return list;
    }

    @Override
    public List<Long> listUserIdByPermission(String permission, Integer projectId) {
        SysPermission sysPermission = sysPermissionMapper.selectOne(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getPermission, permission));
        if(sysPermission == null){
            return null;
        }
        Integer id = sysPermission.getId();
        //判断该项目是否有权限电工
        ProjectPermission projectPermission = projectPermissionMapper.selectOne(new QueryWrapper<ProjectPermission>().lambda().eq(ProjectPermission::getProjectId, projectId).eq(ProjectPermission::getSysPermissionId, id));
        if(projectPermission == null){
            return null;
        }
        List<Integer> roleIds = roleProjectPermissionMapper.listRoleIds(id,projectId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return null;
        }
        List<Long> longs = sysUserRoleMapper.selectUserIdByRoles(roleIds);
        return longs;
    }


    /**
     * 在缓存中获取创建时间
     * @param projectId
     * @return
     */
    private Long getCreateTime(Integer projectId){
        String key = RedisConstantUtil.PROJECT_CREATE_TIME;
        Object value = redisUtil.getHashValue(key, projectId.toString());
        if(null != value){
            log.info("项目id：{}在缓存中获取到了创建时间",projectId);
            return Long.valueOf(value.toString());
        }
        return null;

    }
}
