package cn.meiot.feign.hystrix;


import cn.meiot.feign.UserFeign;
import org.springframework.stereotype.Service;

@Service
public class UserFeignHystrix implements UserFeign {


    @Override
    public Long getMainUserIdByUserId(Long userId) {
        return -1L;
    }

}
