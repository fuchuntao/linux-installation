package cn.meiot.controller;


import cn.meiot.common.ErrorCode;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.exception.MyServiceException;
import cn.meiot.exception.MyTokenExcption;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.EncryptUtil;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.UserAgentUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

@Controller
@Slf4j
public class BaseController {


    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    public HttpServletRequest request;


    /**
     * 获取用户id
     *
     * @return
     */
    public Long getUserId() {
        String userId = request.getHeader(ConstantsUtil.USER_ID);

        if(StringUtils.isEmpty(userId)){
            //通过token获取userId
            String key = RedisConstantUtil.USER_TOKEN;
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            String secretKey = (String )redisTemplate.opsForValue().get(key+ token);
            //log.info("用户token：{}，用户密钥：{}",auth.getAuthentication(),secretKey);
            if(null == secretKey){
                throw new MyTokenExcption(ErrorCode.USER_ID_NOT_NULL,"为获取到用户id");
            }
            //解密 ，拿到用户id信息
            try {
                userId = EncryptUtil.decrypt(token,secretKey);
                return Long.valueOf(userId);
                //log.info("用户id：{}",userId);
            }catch (Exception e){
                throw new MyTokenExcption(ErrorCode.USER_ID_NOT_NULL,"为获取到用户id");
            }
        }
        log.info("当前操作用户id：{}",userId);
        return Long.valueOf(userId);
    }



    /**
     * 获取项目id(前端传值)
     *
     * @return
     */
    public Integer getProjectId() {
        String project = ConstantsUtil.PROJECT;
        String projectId = request.getHeader(project);
        String device = getDevice();
        log.info("获取到的项目id:{}",projectId);
        if (StringUtils.isEmpty(projectId)) {
            throw  new MyTokenExcption("未获取到项目id","未获取到项目id");
        }
        Object object = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN+device+"_"+getUserId());
        if(null == object){
            throw  new MyTokenExcption("未获取到用户信息","未获取到用户信息");
        }
        AuthUserBo authUserBo = new Gson().fromJson(object.toString(),AuthUserBo.class);
        List<Integer>  projectIds = authUserBo.getProjectIds();
        if(null == projectIds || projectIds.size() == 0){
            throw  new MyTokenExcption("当前用户没有该项目,请尝试重新登录","当前用户没有项目,请尝试重新登录");
        }
        Integer id = Integer.valueOf(projectId);
        if(projectIds.contains(id))
            return id;
        log.info("当前用户操作他人项目");
        throw  new MyTokenExcption("当前用户没有该项目,请尝试重新登录","当前用户没有项目,请尝试重新登录");

    }

    /**
     * 获取登录设备
     * @return
     */
    public String getDevice(){
        String  device = UserAgentUtils.getDeviceName(request.getHeader("User-Agent"));
        if("pc".equals(device)){
            device = "pc";
        }else{
            device = "phone";
        }
        return device;
    }


    public  Long getUserIdByToken(){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        String key = RedisConstantUtil.USER_TOKEN;
        String secretKey = (String )redisTemplate.opsForValue().get(key+token);
        if(secretKey == null ){
            log.info("密钥为空");
            throw  new MyTokenExcption("未获取到用户信息","未获取到用户信息");
        }
        String userId = "";
        try {
            userId = EncryptUtil.decrypt(token,secretKey );
            //log.info("用户id：{}",userId);
        }catch (Exception e){
            throw  new MyTokenExcption("token非法","token非法");
        }

        Object object = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN+"pc"+"_"+userId);
        if(object == null ){
            object = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN+"phone"+"_"+userId);
        }

        if(object  ==  null ){
            throw  new MyTokenExcption("未获取到用户信息","未获取到用户信息");
        }
        AuthUserBo authUserBo = new Gson().fromJson(object.toString(),AuthUserBo.class);
        return authUserBo.getUser().getId();
    }



    /**
     *
     * @Title: getUserType
     * @Description: 根据当前登录用户id获取用户类型
     * @param userId
     * @return: java.lang.Integer
     */
    public Integer getUserType(Long userId) {
        AuthUserBo authUserBo = getAuthUserBo(userId);
        return authUserBo.getUser().getType();
    }



    private  AuthUserBo getAuthUserBo(Long userId) {
        Object object = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN+"pc"+"_"+userId);
        if(object == null ){
            object = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN+"phone"+"_"+userId);
        }

        if(object  ==  null ){
            throw  new MyTokenExcption("未获取到用户信息","未获取到用户信息");
        }
        AuthUserBo authUserBo = new Gson().fromJson(object.toString(),AuthUserBo.class);
        return authUserBo;
    }



}
