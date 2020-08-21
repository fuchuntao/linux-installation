package cn.meiot.utils;

import cn.meiot.controller.BaseController;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.AlarmVo;
import cn.meiot.entity.vo.MqVo;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package cn.meiot.util
 * @Description:
 * @author: 武有
 * @date: 2019/10/14 17:17
 * @Copyright: www.spacecg.cn
 */
@Component
@Slf4j
public class UserInfoUtil {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private DeviceFeign deviceFeign;


    /**
     *
     * @param userId
     * @return
     */
    public AuthUserBo getAuthUserBo(Long userId) {
        String auth = (String) redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN +"phone_"+ userId);
        if (StringUtils.isEmpty(auth)) {
            auth=(String) redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN +"pc_"+ userId);
            if (StringUtils.isEmpty(auth)) {
                return null;
            }
        }
        return new Gson().fromJson(auth, AuthUserBo.class);
    }

    public synchronized Map<String, String> getParamMap(MqVo mqVo, String userId) {
        String mainUserId =String.valueOf(userFeign.getMainUserIdByUserId(Long.valueOf(userId)));
        //项目Id
        Integer projectId = deviceFeign.getProjectIdBySerialNumber(mqVo.getSerialNumber());
        log.info("获取到的项目ID为{}", projectId);
        String deviceAlias = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER, mainUserId + "_" + mqVo.getSerialNumber());//redisUtil.getHashValueByKey(RedisConstantUtil.NIKNAME_SERIALNUMBER, userId+"_"+faultMessage.getSerialNumber());
        log.info("获取到的设备名称为：{}", deviceAlias);
        String switchAlias = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SWITCH, mainUserId + "_" + mqVo.getMqDeviceVos().get(0).getId());//redisUtil.getHashValueByKey(RedisConstantUtil.NIKNAME_SWITCH, userId+"_"+faultMessage.getSwitchSn());
        log.info("获取到的开关别名为{}", switchAlias);
        log.info("用户ID为：{}", userId);
        Map<String, String> map = new HashMap<>();
        map.put("deviceAlias", deviceAlias);
        map.put("projectId", String.valueOf(projectId));
        map.put("switchAlias", switchAlias);
        map.put("userId", userId);
        return map;
    }

    public synchronized Map<String, String> getParamMap(AlarmVo alarmVo, Long userId) {
        String deviceAlias = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER, userId + "_" + alarmVo.getDeviceid());//redisUtil.getHashValueByKey(RedisConstantUtil.NIKNAME_SERIALNUMBER, userId+"_"+faultMessage.getSerialNumber());
        log.info("获取到的设备名称为：{}", deviceAlias);
        String switchAlias = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SWITCH, userId + "_" +alarmVo.getSwitchInfo().getId());//redisUtil.getHashValueByKey(RedisConstantUtil.NIKNAME_SWITCH, userId+"_"+faultMessage.getSwitchSn());
        Map<String, String> map = new HashMap<>();
        map.put("deviceAlias", deviceAlias);
        map.put("switchAlias", switchAlias);
        return map;
    }


}
