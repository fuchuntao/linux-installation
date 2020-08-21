package cn.meiot.feign.hystrix;

import cn.meiot.entity.ApplicationFeignVo;
import cn.meiot.feign.ApplicationFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApplicationFeignHystrix  implements ApplicationFeign {
    @Override
    public ApplicationFeignVo getApplicationInfoByKey(String key) {
        return null;
    }
}
