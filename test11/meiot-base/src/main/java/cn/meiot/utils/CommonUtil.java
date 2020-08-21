package cn.meiot.utils;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.feign.UserFeign;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/11/22 8:40
 * @Copyright: www.spacecg.cn
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class CommonUtil {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserFeign userFeign;
    public ImgConfigVo getImgConfig() {
        Object object = redisTemplate.opsForValue().get(RedisConstantUtil.IMG_CONFIG);
        if (null == object) {
            ImgConfigVo imgConfigVo = userFeign.getImgConfig();
            if (null == imgConfigVo) {
                log.info("未获取到图片配置信息");
                return null;
            }
            log.info("接收结果：{}", imgConfigVo);
            return imgConfigVo;
        }
        ImgConfigVo imgConfigVo = new Gson().fromJson(object.toString(), ImgConfigVo.class);
        return imgConfigVo;

    }
}
