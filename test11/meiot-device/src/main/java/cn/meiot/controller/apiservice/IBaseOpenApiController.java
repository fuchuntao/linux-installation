package cn.meiot.controller.apiservice;

import cn.meiot.controller.BaseOpenApiController;
import cn.meiot.entity.ApplicationFeignVo;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.ApplicationFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class IBaseOpenApiController extends BaseOpenApiController {

    @Autowired
    private ApplicationFeign applicationFeign;

    public Long getAppId(){
        return 1L;/*
        ApplicationFeignVo applicationInfo = getApplicationInfo();
        if(applicationInfo != null){
            return applicationInfo.getAppId();
        }
        String token = getToken();
        applicationInfo = applicationFeign.getApplicationInfoByKey(token);
        if(applicationInfo != null){
            return applicationInfo.getAppId();
        }
        throw new MyServiceException("");*/
    }

}
