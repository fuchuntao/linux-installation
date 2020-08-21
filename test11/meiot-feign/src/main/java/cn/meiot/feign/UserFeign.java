package cn.meiot.feign;

import cn.meiot.entity.bo.UserNumBo;
import cn.meiot.feign.hystrix.DeviceFeignHystrix;
import cn.meiot.feign.hystrix.UserFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "meiot-user",fallback = UserFeignHystrix.class)
public interface UserFeign {

    /**
     * 计算总项目数量
     * @return
     */
    @RequestMapping(value = "/api/queryProjectTotal",method = RequestMethod.GET)
    Integer queryProjectTotal();


    /**
     * 通过项目id查询项目的开始时间
     *
     * @param projectId
     * @return Result
     */
    @RequestMapping(value = "/api/getProjectDateByProjectId",method = RequestMethod.GET)
    Long getProjectDateByProjectId(@RequestParam("projectId") Integer projectId);


    @RequestMapping(value = "/api/getUserNum",method = RequestMethod.GET)
    UserNumBo getUserNum();


    /**
     * 根据用户id获取主账户id
     * @param userId
     * @return
     */
    @RequestMapping(value = "/device/getMainUserId",method = RequestMethod.GET)
    Long getMainUserId(@RequestParam("userId") Long userId);
}
