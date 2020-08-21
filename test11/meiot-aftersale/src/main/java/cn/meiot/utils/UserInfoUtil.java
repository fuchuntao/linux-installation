package cn.meiot.utils;

import cn.meiot.controller.BaseController;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.AuthUserVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/9/18 11:40
 * @Copyright: www.spacecg.cn
 */
@Slf4j
@Component
public class UserInfoUtil extends BaseController {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    public  AuthUserBo getUserInfo() {
        String  device = UserAgentUtils.getDeviceName(request.getHeader("User-Agent"));
        if("pc".equals(device)){
            device = "pc";
        }else{
            device = "phone";
        }
        Object userString=JSONObject.parse(redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN+ device+"_" + getUserId()));
        AuthUserBo userInfo= JSONObject.parseObject((String) userString,AuthUserBo.class);
        return userInfo;
    }
//    @Autowired
//    private RedisUtil redisUtil;
//
//    public  AuthUserBo getUserInfo(Long userId) {
//        //通过用户id获取用户信息
//        Object object = redisUtil.getValueByKey(RedisConstantUtil.USER_TOKEN + userId);
//        log.info("查询的用户id：{}，结果：{}", userId, object);
//        if (null == object) {
//            log.info("为查询到用户信息");
//            return null;
//        }
//        AuthUserBo sysUserBo = JSONObject.parseObject((String) object, AuthUserBo.class);
//        log.info("此用户的账号：{}", sysUserBo.getUser().getUserName());
//        return sysUserBo;
//    }


}
