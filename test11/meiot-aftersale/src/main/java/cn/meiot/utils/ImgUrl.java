package cn.meiot.utils;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.feign.UserFeign;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/9/18 14:38
 * @Copyright: www.spacecg.cn
 */
@Component
@Slf4j
public class ImgUrl {

    @Autowired
    private UserFeign userFeign;

    public String getImgUrl(){
        ImgConfigVo imgConfig = userFeign.getImgConfig();
        return imgConfig.getServername()+imgConfig.getMap()+imgConfig.getImg();
    }
    public String getImgUrl(String mark){
        ImgConfigVo imgConfig = userFeign.getImgConfig();
        return imgConfig.getServername()+imgConfig.getMap()+imgConfig.getImg()+imgConfig.getThumbnail();
    }
}
