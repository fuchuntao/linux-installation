package cn.meiot.aop;

import cn.meiot.common.ErrorCode;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.enums.AccountType;
import cn.meiot.exception.MyServiceException;
import cn.meiot.exception.MyTokenExcption;
import cn.meiot.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Package cn.meiot.aop
 * @Description:
 * @author: 武有
 * @date: 2019/12/11 14:22
 * @Copyright: www.spacecg.cn
 */
@Slf4j
@Aspect
@Component
@SuppressWarnings("all")
public class UpgradeDetectionAspect extends BaseController {

    @Autowired
    @Setter
    private RedisTemplate redisTemplate;
    public static final String UPGRADE_PROGRESS_BAR = "UpgradeProgressBar_";

    @Override
    public Integer getProjectId() {
        try {
            return super.getProjectId();
        } catch (Exception e) {
            String project = ConstantsUtil.PROJECT;
            String projectId = request.getHeader(project);
            String  device = UserAgentUtils.getDeviceName(request.getHeader("User-Agent"));
            if("pc".equals(device)){
                device = "pc";
            }else{
                device = "phone";
            }
            log.info("获取到的项目id:{}", projectId);
            if (StringUtils.isEmpty(projectId)) {
                throw new MyTokenExcption("未获取到项目id", "未获取到项目id");
            }
            Object object = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN+device+"_" + getUserId());
            if (null == object) {
                throw new MyTokenExcption("为获取到用户信息", "为获取到用户信息");
            }
            AuthUserBo authUserBo = new Gson().fromJson((String) JSONObject.parse(object.toString()), AuthUserBo.class);
            List<Integer> projectIds = authUserBo.getProjectIds();
            if (null == projectIds || projectIds.size() == 0) {
                throw new MyTokenExcption("当前用户没有该项目,请尝试重新登录", "当前用户没有项目,请尝试重新登录");
            }
            Integer id = Integer.valueOf(projectId);
            if (projectIds.contains(id))
                return id;
            log.info("当前用户操作他人项目");
            throw new MyTokenExcption("当前用户没有该项目,请尝试重新登录", "当前用户没有项目,请尝试重新登录");
        }
    }

    @Pointcut("@annotation(cn.meiot.aop.UpgradeDetection)")
    public void upgradeDetection() {
    }

    @Before(value = "upgradeDetection()")
    public void upgradeDetectionIng(JoinPoint point) {
        String value = getUpgradeDetection(point).value();
        String  device = UserAgentUtils.getDeviceName(request.getHeader("User-Agent"));
        if("pc".equals(device)){
            device = "pc";
        }else{
            device = "phone";
        }
        AuthUserBo authUserBo = getAuthUserBo(device);
        if (authUserBo.getUser().getType().equals(AccountType.PERSONAGE.value())) {
            if (upgradeDetectionIng(getUserId(), 0)) throw new MyServiceException(value);
        }
        if (authUserBo.getUser().getType().equals(AccountType.ENTERPRISE.value())) {
            if (upgradeDetectionIng(getUserId(), getProjectId())) throw new MyServiceException(value);
        }
    }

    private UpgradeDetection getUpgradeDetection(JoinPoint point) {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        UpgradeDetection annotation = method.getAnnotation(UpgradeDetection.class);
        return annotation;
    }

    public boolean upgradeDetectionIng(Long userId, Integer projectId) {
        List<Object> values = redisTemplate.opsForHash().values(UPGRADE_PROGRESS_BAR + userId + "_" + projectId);
        return !(values == null||values.size()==0);
    }

    public AuthUserBo getAuthUserBo(String device) {
        AuthUserBo authUserBo = null;
        Object object = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN +device +"_"+ getUserId());
        try {
            authUserBo = new Gson().fromJson(object.toString(), AuthUserBo.class);
        } catch (Exception e) {
            authUserBo = new Gson().fromJson((String) JSONObject.parse(object.toString()), AuthUserBo.class);
        }
        return authUserBo;
    }
}
