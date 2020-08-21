package cn.meiot.service.impl;

import cn.meiot.dao.RedisDao;
import cn.meiot.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl  implements RedisService {


    @Autowired
    private RedisDao redisDao;

    @Override
    public void saveValue(String key, Object value) {
        redisDao.saveValue(key,value);
    }

    @Override
    public String getValueByKey(String key) {
        return redisDao.getValueByKey(key);
    }


}
