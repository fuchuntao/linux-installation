package cn.meiot.mq;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.Wss;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.SysRolePermissionVo;
import cn.meiot.enums.AccountType;
import cn.meiot.enums.JpushTypeEnum;
import cn.meiot.enums.PushType;
import cn.meiot.jg.JPushClientExample;
import cn.meiot.mapper.*;
import cn.meiot.service.IRoleProjectPermissionService;
import cn.meiot.service.ISysPermissionService;
import cn.meiot.service.ISysRolePermissionService;
import cn.meiot.service.ISysUserRoleService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.VerifyUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class PermissionReceive {

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IRoleProjectPermissionService roleProjectPermissionService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectPermissionMapper projectPermissionMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    private static final Thread  t1 = new Thread();

    private static final Thread  t2 = new Thread();

    private static final Thread  t3 = new Thread();

    /**
     * 休眠一会
     */
    private void sleep(Thread t){
        synchronized (t){
            try {
                //让程序休眠3秒，保证上游的事务成功提交
                t.wait(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    @RabbitListener(queues = "savePermission")
    public void saveMsg(SysRolePermissionVo sysRolePermissionVo) {
        String key = ConstantsUtil.SYS_PERMISSION + sysRolePermissionVo.getUserId();
        //获取此用户拥有那些角色
        List<Integer> roleIds = sysUserRoleService.getRoleList(sysRolePermissionVo.getUserId());
        if (null == roleIds || roleIds.size() == 0) {
            return;
        }
        List<String> permissions = new ArrayList<String>();
        if (sysRolePermissionVo.getType() == ConstantsUtil.ENTERPRISE_ACCOUNT && sysRolePermissionVo.getIsAdmin() != 1) { //企业账户
            //通过角色项目查询用户拥有多少权限
            permissions = roleProjectPermissionService.getPermission(roleIds, sysRolePermissionVo.getProjectId());
        } else {
            permissions = sysRolePermissionService.getPermission(roleIds);
        }
        redisTemplate.opsForValue().set(key, permissions);
    }

    /**
     * 推送权限修改消息
     * @param map
     */
    private void pushProjectTypePermission(Map<String, Object> map){
        List<Integer> oldPermissions = (List<Integer>) map.get("oldPermission");
        List<Integer> newPermissions = (List<Integer>) map.get("newPermission");
        Integer projectType = (Integer) map.get("projectType");
        if(null == oldPermissions && newPermissions == null){

        }else{
            boolean flag = VerifyUtil.compare(oldPermissions, newPermissions);
            if (!flag) {
                //通过项目id获取用户id
                List<Integer> projectIds = projectMapper.selectIdByType(projectType);
                log.info("项目类型：{} 下的项目：{}",projectType,projectIds);
                if(projectIds == null || projectIds.size() == 0){
                    return ;
                }
                for(Integer id : projectIds){
                    pushUserByProjectId(id);
                }
            }
        }

    }



    /**
     * 删除多余的权限信息
     *
     * @param map
     */
    @RabbitListener(queues = QueueConstantUtil.DELETE_SURPLUS_PROJECT_PERMISSION)
    public void delProjectSurplusPermission(Map<String,Object> map)  {
        //通过项目类型获取所有的项目id
        Integer projectType = (Integer) map.get("projectType");
        List<Integer> projectId = projectMapper.selectIdByType(projectType);
        if (null == projectId || projectId.size() == 0) {
            log.info("该项目类型下没有项目");
            return;
        }
        log.info("需要修改的项目：{}", projectId);
        for (Integer id : projectId) {
            //通过项目id查询权限id
            //对比项目与项目类型权限，找出项目多余的权限
            List<Integer> permissions = projectMapper.querySurplusProjectPermission(id, projectType);
            if (null != permissions && permissions.size() > 0) {
                //删除多余的权限
                projectPermissionMapper.deleteBatchIds(permissions);
            }
            //删除企业账号中多余的权限
            map = new HashMap<String, Object>();
            map.put("projectId",id);
            rabbitTemplate.convertAndSend(QueueConstantUtil.DELETE_SURPLUS_ENTERPRISE_PERMISSION, map);
        }
        pushProjectTypePermission(map);

    }

    /**
     *删除多余的权限
     *
     * @param map
     */
    @RabbitListener(queues = QueueConstantUtil.DELETE_SURPLUS_ENTERPRISE_PERMISSION)
    public void delEnterpriseSurplusPermission(Map<String, Object> map) throws InterruptedException {
        log.info("删除多余权限开始");
        //查询多余的权限
        if(map == null){
            return ;
        }
        Integer projectId = (Integer) map.get("projectId");
        List<Integer> permissions = roleProjectPermissionService.querySurplusPermission(projectId);
        log.info("需要删除多余的权限id：{}", permissions);
        if (null != permissions && permissions.size() > 0) {
            //删除多余的权限
            roleProjectPermissionService.removeByIds(permissions);
        }
        log.info("没有多余的权限");
        queryPushUserIds(map);



    }

    /**
     * 推送权限修改消息
     * @param map
     */
    private void queryPushUserIds(Map<String, Object> map){
        List<Integer> oldPermissions = (List<Integer>) map.get("oldPermission");
        List<Integer> newPermissions = (List<Integer>) map.get("newPermission");
        Integer projectId = (Integer) map.get("projectId");
        if(null == oldPermissions && newPermissions == null){

        }else{
            boolean flag = VerifyUtil.compare(oldPermissions, newPermissions);
            if (!flag) {
                pushUserByProjectId(projectId);
            }
        }

    }


    /**
     * 根据项目id推送
     * @param projectId
     */
    private void  pushUserByProjectId(Integer projectId){
        //通过项目id获取用户id
        List<Long> userIds = sysUserMapper.selectIdByProjectId(projectId);
        log.info("通过项目id：{} 查询到的用户：{}",projectId,userIds);
        push(userIds);
    }




    /**
     * 权限发生改变
     *
     * @param map
     */
    @RabbitListener(queues = QueueConstantUtil.PERMISSION_CHECK)
    public void permissionCheck(Map<String, Object> map){
        log.info("开始做权限对比");
        List<Integer> oldPermissions = (List<Integer>) map.get("oldPermission");
        List<Integer> newPermissions = (List<Integer>) map.get("newPermission");
        if (oldPermissions == null && newPermissions == null) {
            log.info("数据为空，不需要对比");
            return;
        }
        boolean flag = VerifyUtil.compare(oldPermissions, newPermissions);
        log.info("权限对比结果：{}", flag);
        if (!flag) {
            //权限发生了改变，需要推送给用户，让其重新登录
            Integer roleId = (Integer) map.get("roleId");
            List<Long> userIds = sysUserRoleService.getUserIdByRoleId(roleId);
            log.info("需要重新登录的用户：{}", userIds);
            if (userIds == null || userIds.size() == 0) {
                return;
            }
            push(userIds);
        }

    }

    /**
     * 推送
     *
     * @param userIds
     * @return
     */
    private void push(List<Long> userIds) {
        //更新用户的权限信息
        userIds.forEach(userId ->{
            log.info("当前用户：{}",userId);
            Object o = redisTemplate.opsForValue().get(RedisConstantUtil.USER_PERMISSIONS + userId);
            if (null != o) {
                //redisTemplate.delete(RedisConstantUtil.USER_PERMISSIONS + userId);
                //获取当前用户的权限
                SysUser sysUser = sysUserMapper.selectById(userId);
                if(sysUser!= null){
                    //将用户的角色id保存到缓存中
                    List<Integer> list = sysUserRoleMapper.selectRoleByUserId(userId);
                    //保存角色信息
                    //redisTemplate.opsForValue().set(RedisConstantUtil.USER_ROLES+userId,list);
                    if(AccountType.ENTERPRISE.value().equals(sysUser.getType())){
                        //获取当前用户的项目id
                        Object value = redisTemplate.opsForHash().get(RedisConstantUtil.DEFAULT_PROJECT,userId.toString());
                        log.info("token信息：{}",o);
                        if(null != value){
                            Integer  projectId = (Integer) value;
                            sysPermissionService.queryEnPermissionIds(sysUser,list,projectId);
                        }

                    }else if(AccountType.PLATFORM.value().equals(sysUser.getType())){
                        sysPermissionService.setRunPlatformPermission(sysUser,list);
                    }
                }
            }else {
                log.info("当前用户：{} 不在线！",userId);
            }
        });
    }


}
