package cn.meiot.utils;

import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.feign.UserFeign;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/9/18 14:38
 * @Copyright: www.spacecg.cn
 */
@Component
@Slf4j
public class UserImgUrl {

    @Autowired
    private UserFeign userFeign;

//    @Autowired
//    public ImgConfigVo imgConfigVo;

    @Autowired
    private FileConfigVo fileConfigVo;



    public String getImgUrl(RedisTemplate redisTemplate){
        Object imgUrl = redisTemplate.opsForValue().get("IMG_URL");
        if (null == imgUrl || imgUrl.equals("null")) {
            synchronized (UserImgUrl.class){
                if (null == imgUrl || imgUrl.equals("null")) {
                    imgUrl=userFeign.getImgConfig();
                    redisTemplate.opsForValue().set("IMG_URL",JSONObject.toJSONString(imgUrl),10, TimeUnit.MINUTES);
                    imgUrl = redisTemplate.opsForValue().get("IMG_URL");
                }
            }
        }
        String jsonString= (String) imgUrl;
        ImgConfigVo imgConfig= JSONObject.parseObject(jsonString,ImgConfigVo.class);
        if (null == imgConfig) {
            return "";
        }
        return imgConfig.getServername()+imgConfig.getMap()+imgConfig.getImg()+imgConfig.getThumbnail();
    }


    /**
     * 获取绝对路径
     * @param key
     * @return
     */
    public String getImgUrl(String key){
//        return imgConfigVo.getServername()+imgConfigVo.getMap()+imgConfigVo.getImg()+key;
        return FileConfigVo.getMPath(key);
    }


    /**
     * 获取配置文件的值
     * @param key
     * @param redisTemplate
     * @return
     */
    public String getRel(String key,RedisTemplate redisTemplate){
        String o = (String) redisTemplate.opsForHash().get(RedisConstantUtil.ConfigItem.CONFIG_KEYS, key);
        if (null != o) {
            return o;
        }
        return getConfig(key);
    }

    /**
     * 网络请求获取配置文件
     * @param key
     * @return
     */
    public String getConfig(String key){
        return userFeign.getConfigValueByKey(key);
    }
}
