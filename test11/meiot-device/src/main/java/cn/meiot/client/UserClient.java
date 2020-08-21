package cn.meiot.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.meiot.client.hystrix.UserHystrix;

@FeignClient(value = "meiot-user",fallback = UserHystrix.class)
public interface UserClient {

	 /**
     * 通过用户id查询用户的名称和手机号
     * @param userId
     */
    @RequestMapping(value = "/device/findInfoById",method = RequestMethod.GET)
    public Map<String,Object> getInfoById(@RequestParam("userId") Long userId);
    
    /**
     * 通过用户id查询主用户id
     * @param userId
     */
    @RequestMapping(value = "/device/getMainUserId",method = RequestMethod.GET)
    public Long getMainById(@RequestParam("userId") Long userId);
    
    /**
     * 通过项目id查询企业名和项目名
     * @param projectId
     * projectName 项目名
     * enterpriseName 企业名
     */
    @RequestMapping(value = "/api/queryProNameByProjectId",method = RequestMethod.GET)
    public Map<String,String> queryProNameByProjectId(@RequestParam("projectId") Integer projectId);

    /**
     * 根据key获取配置信息value
     * @param cKey
     * @return
     */
    @RequestMapping(value = "/api/getConfigValueByKey",method = RequestMethod.GET)
    public String getConfigValueByKey(@RequestParam(value = "key") String cKey);
}
