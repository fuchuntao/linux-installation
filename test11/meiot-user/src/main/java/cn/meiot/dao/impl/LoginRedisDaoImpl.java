package cn.meiot.dao.impl;

import cn.meiot.dao.LoginRedisDao;
import cn.meiot.entity.SysMenu;
import cn.meiot.entity.SysPermission;
import cn.meiot.entity.SysUser;
import cn.meiot.entity.UserProject;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.enums.AccountType;
import cn.meiot.utils.ConstantUtil;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.google.gson.Gson;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class LoginRedisDaoImpl implements LoginRedisDao {


    public static final String PREFIX = "_";

    private RedisTemplate<Object, Object> redisTemplate;

    public LoginRedisDaoImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveToken( AuthUserBo authUserBo,String secretKey) {
        //存储格式：  key：token    value：secretKey
        String key = RedisConstantUtil.USER_TOKEN + authUserBo.getToken();
        long expireTime = 0l;
        String device = "";
        if("pc".equals(authUserBo.getDevice())){
            expireTime = ConstantsUtil.OTHER_TOKEN_EXPIRE_TIME;
            device = "pc";
        }else{
            expireTime = ConstantsUtil.APP_TOKEN_EXPIRE_TIME;
            device = "phone";
        }
        redisTemplate.opsForValue().set(key, secretKey , expireTime,ConstantsUtil.EXPIRE_TYPE);
        //存储基本信息
        key = RedisConstantUtil.USER_TOKEN +device+PREFIX+ authUserBo.getUser().getId();
        String user = new Gson().toJson(authUserBo);
        redisTemplate.opsForValue().set(key, user, expireTime, ConstantsUtil.EXPIRE_TYPE);
    }

    @Override
    public SysUser getUserInfoByToken(String redisKey, String device) {
        String key = ConstantUtil.TOKEN + ":" + device + ":" + redisKey;
        Object object = redisTemplate.opsForValue().get(key);
        return (SysUser) object;
    }

    @Override
    public void saveMenu(List<SysPermission> sysMenus, Long userId) {
        String key = ConstantsUtil.SYS_PERMISSION + userId;
        redisTemplate.opsForValue().set(key, sysMenus);
    }

    @Override
    public void saveProject(List<UserProject> projects, Long userId) {
        String key = ConstantsUtil.USER_PROJECTS + userId;
        for (UserProject p : projects) {
            String projectId = p.getProjectId().toString();
            redisTemplate.opsForHash().put(key, projectId, p.getProjectId());
        }
    }

    @Override
    public void clearLoginInfo(Long userId) {
        String key = RedisConstantUtil.USER_TOKEN+ userId;
        Object object =  redisTemplate.opsForValue().get(key);
        if(null == object){
            return;
        }
        AuthUserBo authUserBo = new Gson().fromJson(object.toString(),AuthUserBo.class);
        //判断此用户是否是企业用户且不是超级管理员
        if (authUserBo.getUser().getType() == AccountType.ENTERPRISE.value() && authUserBo.getUser().getIsAdmin() != 1){
            //删除项目
            key = ConstantsUtil.USER_PROJECTS + userId;
            redisTemplate.delete(key);
        }

        //删除此用户的基础信息
        redisTemplate.delete(key);

        //删除key：token   value：密钥
        key =RedisConstantUtil.USER_TOKEN + authUserBo.getToken();
        redisTemplate.delete(key);

        //删除此用户的权限信息
        key = ConstantsUtil.SYS_PERMISSION + userId;
        redisTemplate.delete(key);

    }

    @Override
    public void savePersission(List<String> list, Long id) {
        String key = ConstantsUtil.SYS_PERMISSION + id;
        redisTemplate.opsForValue().set(key, list);
    }
}
