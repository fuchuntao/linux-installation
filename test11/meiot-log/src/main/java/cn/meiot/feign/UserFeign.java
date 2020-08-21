package cn.meiot.feign;

import cn.meiot.feign.hystrix.UserFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Package cn.meiot.feign
 * @Description:
 * @author: 武有
 * @date: 2019/10/12 14:45
 * @Copyright: www.spacecg.cn
 */
@FeignClient(value = "meiot-user",fallback = UserFeignHystrix.class)
public interface UserFeign {
    /**
     * 根据用户id获取主用户id
     * @param userId
     * @return
     */
    @RequestMapping(value = "/device/getMainUserId",method = RequestMethod.GET)
    Long getMainUserIdByUserId(@RequestParam("userId") Long userId);
}
