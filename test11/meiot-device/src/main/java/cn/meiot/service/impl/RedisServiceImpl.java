package cn.meiot.service.impl;

import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.RedisService;
import cn.meiot.utils.RedisConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void insertSerial(String serialNumber) {
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(RedisConstantUtil.ADD_SERIAL + serialNumber, "1", 5,TimeUnit.SECONDS);
        if(!aBoolean){
            throw new MyServiceException(ResultCodeEnum.REPEAT_ADD.getCode(),ResultCodeEnum.REPEAT_ADD.getMsg());
        }
    }

    @Override
    public void removeInsertSerial(String serialNumber) {
        redisTemplate.delete(RedisConstantUtil.ADD_SERIAL + serialNumber);
    }


}
