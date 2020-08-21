package cn.meiot.service.impl;

import cn.meiot.entity.Config;
import cn.meiot.entity.bo.ConfigUserBo;
import cn.meiot.entity.vo.ConfigVo;
import cn.meiot.mapper.ConfigMapper;
import cn.meiot.service.IConfigService;
import cn.meiot.utils.ReadWriteLockUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2020-02-28
 */
@Service
@Slf4j
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements IConfigService {

    private RedisTemplate redisTemplate;

    private ConfigMapper configMapper;

    ConfigServiceImpl(RedisTemplate redisTemplate,ConfigMapper configMapper){
        this.redisTemplate = redisTemplate;
        this.configMapper = configMapper;
    }

    @Override
    public String getConfigValueByKey(String cKey) {
        //获取读锁
        Lock readLock = ReadWriteLockUtil.readLock(ReadWriteLockUtil.CONFIG_VALUE);
        try{
            //加锁
            readLock.lock();
            //缓存中获取配置项
            String cValue = (String) redisTemplate.opsForHash().get(RedisConstantUtil.ConfigItem.CONFIG_KEYS, cKey);
            if(null != cValue ){
                return cValue;
            }
        }finally {
            //释放读锁
            readLock.unlock();
        }
        //获取写锁
        Lock writeLock = ReadWriteLockUtil.writeLock(ReadWriteLockUtil.CONFIG_VALUE);
        try{
            //加锁
            writeLock.lock();
            //数据库中获取数据
            Config config = configMapper.selectOne(new QueryWrapper<Config>().lambda().eq(Config::getCKey, cKey));
            if(config == null ){
                log.info("数据库中不存在，直接返回空！");
                return null;
            }
            //将查询出来的数据存入缓存
            redisTemplate.opsForHash().put(RedisConstantUtil.ConfigItem.CONFIG_KEYS, config.getCKey(),config.getValue());
            return config.getValue();

        }finally {
            writeLock.unlock();
        }
    }

    @Override
    public Integer updateConfigById(ConfigVo configVo) {
        return configMapper.updateConfigById(configVo);
    }

    @Override
    public List<ConfigUserBo> getListByType(int type) {
        return configMapper.getListByType(type);
    }

    @Override
    public String getValueByKey(String key) {
        return configMapper.getValueByKey(key);
    }
}
