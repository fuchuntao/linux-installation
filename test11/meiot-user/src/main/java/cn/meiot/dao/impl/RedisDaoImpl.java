package cn.meiot.dao.impl;

import cn.meiot.dao.RedisDao;
import cn.meiot.utils.ConstantUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisDaoImpl implements RedisDao {

    private RedisTemplate redisTemplate;

    public RedisDaoImpl(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }
    @Override
    public void saveValue(String key, Object value) {
        redisTemplate.opsForValue().set(key,value, ConstantUtil.CODE_EXPIRE, TimeUnit.MINUTES);
    }

    @Override
    public String getValueByKey(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

}
