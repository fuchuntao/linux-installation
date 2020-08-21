package cn.meiot.feign;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.hystrix.UserFeignHystrix;
import org.apache.ibatis.annotations.Lang;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "meiot-user", fallback = UserFeignHystrix.class)
public interface UserFeign {

    /**
     * 获取所有用户的id
     *
     * @return
     */
    @RequestMapping(value = "/api/msg/findAllUserId", method = RequestMethod.GET)
    Result findAllUserId(@RequestParam("type") Integer type);

    /**
     * 获取图片配置信息
     *
     * @return
     */
    @RequestMapping(value = "/api/msg/getImgConfig", method = RequestMethod.GET)
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
     * 根据主账户id所有子账户
     *
     * @param mainUserId
     * @return
     */
    @RequestMapping(value = "/api/msg/getAllUserIdByMainUser", method = RequestMethod.GET)
    List<Long> getAllUserIdByMainUser(@RequestParam("mainUserId") Long mainUserId);

    /**
     * 通过角色查询用户
     *
     * @param roleIds
     * @return
     */
    @PostMapping("/api/msg/getUserIdsByRoleId")
    List<Long> getUserIdsByRoleId(Map map); //roleIds

    /**
     * 通过项目id查询项目名称
     *
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/api/queryProjectNameById", method = RequestMethod.GET)
    String queryProjectNameById(@RequestParam("projectId") Integer projectId);


    /**
     * 根据类型获取用户id列表
     *
     * @param type 2:企业用户    5：个人用户     0/空：企业+个人
     * @return
     */
    @RequestMapping(value = "/api/getUserByType", method = RequestMethod.GET)
    List<Long> getUserByType(@RequestParam("type") Integer type);


    @RequestMapping(value = "/api/accessToken", method = RequestMethod.GET)
    String getAccessToken();

    /**
     * 获取用户openid
     *
     * @return
     */
    @RequestMapping(value = "/api/openid", method = RequestMethod.GET)
    String getOpenid(@RequestParam(value = "userId", required = true) Long userId);

    @RequestMapping(value = "api/getTypeByUserId",method = RequestMethod.GET)
    Integer getUserTypeByUserId(Long userId);

    @RequestMapping(value = "/api/getConfigValueByKey",method = RequestMethod.GET)
    String getConfigValueByKey(@RequestParam(value = "key") String cKey);


    /**
     * 通过用户id和权限唯一标识校验用户是否存在此权限
     * @param userId  用户id
     * @param permission  权限唯一标识
     * @param projectId  项目id
     * @return
     */
    @RequestMapping(value = "/api/checkPermission",method = RequestMethod.GET)
    boolean checkPermission(@RequestParam("userId") Long userId,@RequestParam("permission") String permission,
                                   @RequestParam("projectId") Integer projectId);

//    /**
//     * 通过用户id查询用户昵称
//     * @param userId
//     * @return
//     */
//    @RequestMapping(value = "/api/getRoleIdByUserId",method = RequestMethod.GET)
//     String getNiknameByUserId(@RequestParam("userId") Long userId);



    /**
     * 通过用户id查询用户昵称
     * @param userId
     * @return
     */
    @RequestMapping(value = "/api/getNiknameByUserId",method = RequestMethod.GET)
    public String getNiknameByUserId(@RequestParam("userId") Long userId);

    /**
     * 根据用户ID查询他的角色名称
     * @param userId
     * @return
     */
    @RequestMapping(value = "api/getRoleNameByUserId",method = RequestMethod.GET)
    List<String> getRoleNameByUserId(@RequestParam("userId") Long userId);
}
