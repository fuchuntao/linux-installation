package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IExceptionLogService;
import cn.meiot.service.ILoginLogService;
import cn.meiot.util.UserInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wuyou
 * @since 2019-10-14
 */
@RestController
@RequestMapping("/login-log")
@SuppressWarnings("all")
public class LoginLogController extends BaseController{

    @Autowired
    private UserFeign userFeign;
    @Autowired
    private ILoginLogService ILoginLogService;

    @Autowired
    private UserInfoUtil userInfoUtil;
    @GetMapping("/getLoginLogList")
    @Log(operateContent = "查询登录日志列表",operateModule = "日志服务")
    public Result<Map> getLoginLogList(@RequestParam("currentPage") Integer currentPage,
                                           @RequestParam("pageSize") Integer pageSize,
                                           @RequestParam(value = "startTime",required = false) String startTime,
                                           @RequestParam(value = "endTime",required = false) String endTime,
                                           @RequestParam(value = "account",required = false) String account){
        Long userId=userFeign.getMainUserIdByUserId(getUserId());
        if (userInfoUtil.getAuthUserBo(getUserId()).getUser().getType()==1){
            return ILoginLogService.getLoginLogListAdmin((currentPage-1)*pageSize,pageSize,startTime,endTime,account,1);
        }
        Result<Map> listResult= ILoginLogService.getLoginLogList((currentPage-1)*pageSize,pageSize,startTime,endTime,account,userId);
        return listResult;
    }
}
