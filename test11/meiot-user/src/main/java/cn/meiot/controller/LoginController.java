package cn.meiot.controller;

import cn.meiot.aop.Log;
import cn.meiot.entity.vo.*;
import cn.meiot.service.ILoginService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.UserAgentUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录管理
 */
@RestController
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    private ILoginService loginService;

    /**
     * 登录（app端）
     * @return
     */
    @RequestMapping(value = "/nofilter/login",method = RequestMethod.POST)
    public Result login(@RequestBody @Valid Login login, HttpServletRequest request){
        Result result = Result.getDefaultFalse();
        if(StringUtils.isEmpty(login.getAccount()) || StringUtils.isEmpty(login.getPassword())){
            //result.setMsg("用户名或者密码不能为空");
            result.setCode(ErrorCodeUtil.USER_NAME_OR_PWD_NOT_BE_NULL);
            return result;
        }
        String  agent = request.getHeader("User-Agent");
        String  device = UserAgentUtils.getDeviceName(agent);
        log.info("设备型号：{},=========>:{}",agent,device);
        login.setDevice(device);
        return loginService.login(login);
    }

    /**
     *平台登录
     * @param login
     * @param request
     * @return
     */
    @RequestMapping(value = "/nofilter/paltLogin",method = RequestMethod.POST)
    public Result paltLogin(@RequestBody @Valid Login login/*,BindingResult bindingResult*/, HttpServletRequest request){
//        if(bindingResult.hasErrors()){
//            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
//        }
        String  agent = request.getHeader("User-Agent");
        String  device = UserAgentUtils.getDeviceName(agent);
        log.info("设备型号：{}",device);
        login.setDevice(device);
        return loginService.paltLogin(login);
    }

    /**
     * 企业账户登录
     * @param login
     * @param request
     * @return
     */
    @PostMapping(value = "/nofilter/enterpriseLogin")
    public Result enterpriseLogin(@RequestBody @Valid Login login ,HttpServletRequest request){
        String  agent = request.getHeader("User-Agent");
        log.info("User-Agent：{}",agent);
        String  device = UserAgentUtils.getDeviceName(agent);
        log.info("登录的设备型号：{}",device);
        login.setDevice(device);

        return loginService.enterpriseLogin(login);
    }



    /**
     * 注册账号
     * @param registerVo
     * @return
     */
    @RequestMapping(value = "/nofilter/register",method = RequestMethod.POST)
    public Result register(@RequestBody RegisterVo registerVo){
        log.info("注册用户开始====================>");
        log.info("注册参数：{}",registerVo);
        Result result = Result.getDefaultFalse();
        if(null == registerVo){
            //result.setMsg("参数不可为空");
            result.setCode(ErrorCodeUtil.PARMA_NOT_BE_NULL);
            return result;
        }
        if(StringUtils.isEmpty(registerVo.getAccount())){
            //result.setMsg("手机号不可为空");
            result.setCode(ErrorCodeUtil.PHONE_NOT_BE_NULL);
            return result;
        }
        if(StringUtils.isEmpty(registerVo.getCode())){
            //result.setMsg("手验证码不可为空");
            result.setCode(ErrorCodeUtil.SMS_CODE_NOT_BE_NULL);
            return result;
        }
        if(StringUtils.isEmpty(registerVo.getNewPwd())){
            //result.setMsg("登陆密码不可为空");
            result.setCode(ErrorCodeUtil.LOGIN_PWD_NOT_BE_NULL);
            return result;
        }
        return loginService.register(registerVo);
    }


    /**
     * 注销
     * @return
     */
    @RequestMapping(value = "logout",method = RequestMethod.GET)
    @Log(operateContent = "注销")
    public Result logout(HttpServletRequest request){
        String  agent = request.getHeader("User-Agent");
        String  device = UserAgentUtils.getDeviceName(agent);
        return loginService.logout(getUserId(),device) ;
    }

    /**
     * 忘记密码
     * @return
     */
    @RequestMapping(value = "/nofilter/forgetPwd",method = RequestMethod.POST)
    @Log(operateContent = "忘记密码")
    public Result forgetPwd(@RequestBody @Valid ForgetPwdVo forgetPwdVo){
        if(forgetPwdVo.getType() == null ){
            return Result.faild(ErrorCodeUtil.TYPE_NOT_BE_NULL);
        }
        return loginService.forgetPwd(forgetPwdVo);
    }

    @PostMapping(value = "/updatePwd")
    public Result updatePwd(@RequestBody @Valid ResetPwd resetPwd, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        resetPwd.setUserId(getUserId());
        return loginService.updatePwd(resetPwd);
    }


    @RequestMapping(value = "/nofilter/updatePwdBySms",method = RequestMethod.POST)
    public Result updatePwdBySms(@RequestBody @Valid ForgetPwdVo forgetPwdVo){
        return loginService.updatePwdBySms(getUserId(),forgetPwdVo);
    }



    /**
     * 查询用户id
     * @return
     */
    @GetMapping(value = "/verify")
    public Result queryUserId(){
        Long userId = getUserIdByToken();
        Result result = Result.getDefaultTrue();
        result.setData(userId);
        return result;
    }

}
