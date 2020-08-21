package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.feign.UserFeign;
import cn.meiot.util.UserInfoUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IActionLogService;

/**
 * <p>
 * 日志表 前端控制器
 * </p>
 *
 * @author 贺志辉
 * @since 2019-08-15
 */
@RestController
@SuppressWarnings("all")
public class ActionLogController extends BaseController {
    @Autowired
    IActionLogService actionLogService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private UserInfoUtil userInfoUtil;

    @GetMapping("/getLogList")
    @Log(operateContent = "查询操作日志列表", operateModule = "日志服务")
    public Result getLogList(@RequestParam("currentPage") Integer currentPage,
                             @RequestParam("pageSize") Integer pageSize,
                             @RequestParam(value = "startTime", required = false) String startTime,
                             @RequestParam(value = "endTime", required = false) String endTime,
                             @RequestParam(value = "account", required = false) String account) {
        Long userId=userFeign.getMainUserIdByUserId(getUserId());
        if (userInfoUtil.getAuthUserBo(getUserId()).getUser().getType()==1){
            return actionLogService.getLogListAdmin((currentPage-1)*pageSize,pageSize,startTime,endTime,account,1);
        }

        return actionLogService.getLogList((currentPage - 1) * pageSize, pageSize, startTime, endTime, account, userId);
    }

    public AuthUserBo getAuthUserBo(Long userId) {
        String auth = (String) redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN + userId);
        return new Gson().fromJson(auth, AuthUserBo.class);
    }
}
