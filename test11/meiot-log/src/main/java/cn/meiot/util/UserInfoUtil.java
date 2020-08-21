package cn.meiot.util;

import cn.meiot.controller.BaseController;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.UserAgentUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Package cn.meiot.util
 * @Description:
 * @author: 武有
 * @date: 2019/10/14 17:17
 * @Copyright: www.spacecg.cn
 */
@Component
public class UserInfoUtil extends BaseController {
    @Autowired
    private RedisTemplate redisTemplate;
    public AuthUserBo getAuthUserBo(Long userId) {
        String  device = UserAgentUtils.getDeviceName(request.getHeader("User-Agent"));
        if("pc".equals(device)){
            device = "pc";
        }else{
            device = "phone";
        }
        String auth = (String) redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN + device+"_" + userId);
        return new Gson().fromJson(auth, AuthUserBo.class);
    }
}
