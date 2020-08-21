package cn.meiot.mq;

import cn.meiot.config.RabbitConfig;
import cn.meiot.entity.Auth;
import cn.meiot.enums.AccountType;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.RedisConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ReceiveMq {


    private RedisTemplate redisTemplate;

    public ReceiveMq(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    /**
     * 刷新token有效时间
     * @param auth
     */
    @RabbitListener(queues = "refreshTokenTime")
    public void refreshTokenTime(Auth auth){
        log.info("刷新token： {}" ,auth);
        String key = RedisConstantUtil.USER_TOKEN;
        //log.info("刷新token的有效时间："+auth.getUserId());
        Long expireTime = 0l;
        String device = "";
        if("pc".equals(auth.getDevice())){
            expireTime = ConstantsUtil.OTHER_TOKEN_EXPIRE_TIME;
            device = "pc";
        }else{
            expireTime = ConstantsUtil.APP_TOKEN_EXPIRE_TIME;
            device = "phone";
        }
        String  userKey = key+device+"_"+auth.getUserId();
        String authKey = key+auth.getAuthentication();
        log.info("userKey:{},authKey:{}",userKey,authKey);
        redisTemplate.expire(userKey,expireTime, ConstantsUtil.EXPIRE_TYPE);
        redisTemplate.expire(authKey,expireTime, ConstantsUtil.EXPIRE_TYPE);
        if(AccountType.ENTERPRISE.value().equals(auth.getType())){
            redisTemplate.expire(RedisConstantUtil.USER_ROLES+auth.getUserId(),expireTime, ConstantsUtil.EXPIRE_TYPE);
        }
       // log.info("刷新token成功");

    }


    /**
     * 删除被挤掉的token
     * @param token
     */
    @RabbitListener(queues = RabbitConfig.DEL_USER_TOKEN)
    public void delUserToken(String token){
        String key = RedisConstantUtil.USER_TOKEN+token;
        redisTemplate.delete(key);
    }
}
