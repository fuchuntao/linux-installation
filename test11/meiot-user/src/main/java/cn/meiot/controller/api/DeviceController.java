package cn.meiot.controller.api;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.ISysUserService;
import cn.meiot.service.api.DeviceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 供设备服务调用的controller
 */
@RestController
@RequestMapping("/device")
@Slf4j
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ISysUserService sysUserService;


    /**
     * 根据用户id查询此用户是否是企业用户 如果是则返回他的顶级账户
     * @param userId
     * @return
     */
    @RequestMapping(value = "checkEnterprise",method = RequestMethod.GET)
    public Result checkEnterprise(@RequestParam(value = "userId",defaultValue = "") Long userId){

        if(null == userId){
            Result result = Result.getDefaultFalse();
            result.setMsg("请传入用户id");
            return result;
        }

        return deviceService.checkEnterprise(userId);
    }

    /**
     * 通过用户id查询用户的名称和手机号
     * @param userId
     */
    @RequestMapping(value = "findInfoById",method = RequestMethod.GET)
    public Map<String,Object> getInfoById(@RequestParam("userId") Long userId){
        SysUser sysUser = sysUserService.getById(userId);
        if(null == sysUser){
            return null;
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("phone",sysUser.getUserName());
        map.put("userName",sysUser.getNickName());
        return map;
    }

    /**
     * 根据用户id获取主账户id
     * @param userId
     * @return
     */
    @RequestMapping(value = "getMainUserId",method = RequestMethod.GET)
    public Long getMainUserId(@RequestParam("userId") Long userId){
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getId,userId).eq(SysUser::getStatus,1));
        if(null == sysUser){
            log.info("用户id：{} 不存在",userId);
            return null;
        }
         return 0 == sysUser.getBelongId() ? sysUser.getId() : sysUser.getBelongId();

    }

}
