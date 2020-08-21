package cn.meiot.feign;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.feign.hystrix.UserFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * @Package cn.meiot.feign
 * @Description:
 * @author: 武有
 * @date: 2019/9/27 12:28
 * @Copyright: www.spacecg.cn
 */
@FeignClient(value = "meiot-user",fallback = UserFeignHystrix.class)
public interface UserFeign {
    @RequestMapping(value = "/api/msg/getImgConfig",method = RequestMethod.GET)
    ImgConfigVo getImgConfig();


    /**
     * 根据用户id获取主用户id
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/device/getMainUserId", method = RequestMethod.GET)
    Long getMainUserIdByUserId(@RequestParam("userId") Long userId);


    /**
     * 通过用户id获取角色id
     * @param userId
     * @return
     */
    @RequestMapping(value = "/api/getRoleIdByUserId",method = RequestMethod.GET)
    List<Integer> getRoleIdByUserId(@RequestParam("userId") Long userId);


    @RequestMapping(value = "/api/getConfigValueByKey",method = RequestMethod.GET)
    String getConfigValueByKey(@RequestParam(value = "key") String cKey);
}
