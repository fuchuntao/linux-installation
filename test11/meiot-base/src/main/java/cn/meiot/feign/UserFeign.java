package cn.meiot.feign;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.impl.UserFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "meiot-user",fallback = UserFeignHystrix.class)
public interface UserFeign {

    /**
     * 获取所有用户的id
     * @return
     */
    @RequestMapping(value = "/api/msg/findAllUserId",method = RequestMethod.GET)
    Result findAllUserId(@RequestParam("type") Integer type);

    /**
     * 获取图片配置信息
     * @return
     */
    @RequestMapping(value = "/api/msg/getImgConfig",method = RequestMethod.GET)
    ImgConfigVo getImgConfig();

    /**
     * 根据用户id获取主用户id
     * @param userId
     * @return
     */
    @RequestMapping(value = "/device/getMainUserId",method = RequestMethod.GET)
    Long getMainUserIdByUserId(@RequestParam("userId") Long userId);

    /**
     * 根据主账户id所有子账户
     * @param mainUserId
     * @return
     */
    @RequestMapping(value = "/api/msg/getAllUserIdByMainUser",method = RequestMethod.GET)
    List<Long> getAllUserIdByMainUser(@RequestParam("mainUserId") Long mainUserId);

    /**
     * 通过角色查询用户
     * @param roleIds
     * @return
     */
    @PostMapping("/api/msg/getUserIdsByRoleId")
    List<Long> getUserIdsByRoleId(Map map); //roleIds

    /**
     * 通过项目id查询项目名称
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/api/queryProjectNameById",method = RequestMethod.GET)
    String  queryProjectNameById(@RequestParam("projectId") Integer projectId);


}
