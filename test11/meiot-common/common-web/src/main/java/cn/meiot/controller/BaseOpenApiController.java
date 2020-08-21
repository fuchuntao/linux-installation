package cn.meiot.controller;

import cn.meiot.entity.ApplicationFeignVo;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.RedisUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class BaseOpenApiController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private HttpServletRequest request;

    public ApplicationFeignVo getApplicationInfo() {
        return new Gson().fromJson(redisUtil.getValueByKey(ConstantsUtil.ApplicationConstants.APPLICATION_KYE+getToken()).toString(),ApplicationFeignVo.class);
    }



    private String getToken(){
        return this.request.getHeader(ConstantsUtil.Authorization);
    }
}
