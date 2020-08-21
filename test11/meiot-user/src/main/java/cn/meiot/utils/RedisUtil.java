package cn.meiot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {


    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 获取hash
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object getHashValue(String key, String hashKey) {
        Object o = redisTemplate.opsForHash().get(key, hashKey);
        return o;
    }

    /**
     * 返回String类型的数据（hash类型）
     * @param key
     * @param hashKey
     * @return
     */
    public String stringValue(String key, String hashKey) {
        //在缓存中获取数据
        Object hashValue = getHashValue(key, hashKey);
        if (null == hashValue) {
            return null;
        }
        return hashValue.toString();
    }


    /**
     * 保存hash类型的值
     *
     * @param key
     * @param hashKey
     * @param time
     */
    public void saveHashValue(String key, String hashKey, Object time) {
        redisTemplate.opsForHash().put(key, hashKey, time);
    }


    /**
     * 删除hashkey
     * @param key
     * @param hashKey
     */
    public void deleteHashKey(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key,hashKey);
    }

    /**
     * 保存value值
     * @param key
     * @param value
     * @param expireTime
     */
    public void saveStringValue(String key, String value, long expireTime) {
        redisTemplate.opsForValue().set(key,value, expireTime,TimeUnit.SECONDS);
    }

    /**
     * 通过key获取value
     * @param key
     * @return
     */
    public String getValueByKey(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除String类型
     * @param key
     */
    public void deleteString(String key) {

        redisTemplate.delete(key);
    }
}
