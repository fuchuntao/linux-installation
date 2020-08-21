package cn.meiot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 通过key查询value
     * @return
     */
    public Object getValueByKey(String key){
        return redisTemplate.opsForValue().get(key);
    }
    /**
     * 通过key设置Value
     */
    public void setValueByKey(String key, Object o, Long expiration, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key,o,expiration,timeUnit);
    }

    /**
     * 查询hash
     * @return
     */
    public Object getHashValueByKey(String hash,String key){
        Object o = redisTemplate.opsForHash().get(key,hash);
        log.info("HASH值：{}，key:{}",hash,key);
        log.info("查询用户id的结果：{}",o);
        return o;
    }

    /**
     * 保存hash值
     * @param hashKey hash值
     * @param key
     * @param value
     */
    public void saveHashValue(String hashKey, String key, Object value) {
        redisTemplate.opsForHash().put(hashKey,key,value);
    }


    /**
     * 通过key删除字符串
     * @param key
     */
    public void deleteValueByKey(String key) {
        redisTemplate.delete(key);
    }
}
