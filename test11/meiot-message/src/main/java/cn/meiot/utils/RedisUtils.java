package cn.meiot.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2020/5/7 11:26
 * @Copyright: www.spacecg.cn
 */
@Component
public class RedisUtils {
    private final RedisTemplate redisTemplate;

    public RedisUtils(RedisTemplate redisTemplate) {
       this.redisTemplate=redisTemplate;
    }

    /**
     * 获取设备箱名称
     * @param userId
     * @param serialNumber
     * @return
     */
    public String getDeviceName(Long userId,String serialNumber){
        String deviceAlias = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER, userId + "_" + serialNumber);
        if (StringUtils.isEmpty(deviceAlias)) {
            deviceAlias=serialNumber;
        }
        return deviceAlias;
    }

    public String getSwitchAlias(Long userId,String switchSn) {
        String switchAlias = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SWITCH, userId + "_" +switchSn);
        if (StringUtils.isEmpty(switchAlias)) {
            switchAlias=switchSn;
        }
        return switchAlias;
    }

}
