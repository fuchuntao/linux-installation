package cn.meiot.controller;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SmsVo;
import cn.meiot.enums.SmsType;
import cn.meiot.feign.SmsFeign;
import cn.meiot.service.ISysUserService;
import cn.meiot.utils.*;
import cn.meiot.utils.Sms.ForgetPwd;
import cn.meiot.utils.Sms.RegisterUser;
import cn.meiot.utils.Sms.UpdatePwd;
import cn.meiot.utils.abstracts.SmsCode;
import cn.meiot.utils.factory.SmsFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.rmi.registry.Registry;

@RestController
@RequestMapping("/sms/nofilter")
@Slf4j
public class SmsController extends  BaseController{

    private final String signContent = "account=%s&smsType=%d";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SmsFeign smsFeign;

    @Autowired
    private SmsFactory smsFactory;

    @Autowired
    private ISysUserService sysUserService;


    /**
     * 发送短信消息
     * @return
     */
    @RequestMapping(value = "/sendSms",method = RequestMethod.POST)
    public Result sendSms(@RequestBody SmsVo smsVo){
        log.info("获取短信验证码开始====================");
        log.info("发送短信验证码的参数：{}",smsVo);
        Result result = Result.getDefaultFalse();
        if(!VerifyUtil.verifyPhone(smsVo.getAccount())){
            //result.setMsg("手机号码格式不正确");
            result.setMsg(ErrorCodeUtil.PHONE_FORMAT_ERROR);
            return result;
        }
        String sign = String.format(signContent, smsVo.getAccount(), smsVo.getSmsType());
        sign = Md5.md5To32x(sign);
        sign = Md5.md5To32x(sign+ ConstantsUtil.SIGN_SECRET);
        log.info("加密后的sign：{}",sign);
        if(!sign.equals(smsVo.getSign())){
            result.setMsg(ErrorCodeUtil.ILLEGAL_REQUEST);
            return result;
        }
        SmsCode smsCode = smsFactory.getSmsCode(smsVo.getSmsType());
        if(smsVo.getSmsType().equals(SmsType.UPDATEPWD.value())){
            smsVo.setUserId(getUserId());
        }
        result = smsCode.check(smsVo);
        if(!result.isResult()){
            return result;
        }
        //校验通过，发送验证码
        rabbitTemplate.convertAndSend(QueueConstantUtil.SEND_SMS_MSG,smsVo);
        log.info("获取短信验证码结束=====================");
        return Result.getDefaultTrue();
    }


    /**
     * 获取短信信息
     * @return
     */
    @RequestMapping(value = "getSms",method = RequestMethod.POST)
    public Result getSms(@RequestBody SmsVo smsVo){
        log.info("进入查询验证码步骤");
        return smsFeign.getSms(smsVo);
    }

    /**
     * 校验验证码是否正确
     * @param smsVo
     * @return
     */
    @PostMapping(value = "/checkCode")
    public Result checkCode(@RequestBody SmsVo smsVo){
        if(SmsType.UPDATEPWD.value().equals(smsVo.getSmsType())){
            SysUser sysUser = sysUserService.getById(getUserId());
            smsVo.setAccount(sysUser.getUserName());
        }
        Result result = smsFeign.getSms(smsVo);
        log.info("查询验证码返回结果：{}",result);
        if(!result.isResult() || null == result.getData()){
            return new Result().Faild(ErrorCodeUtil.QUERY_SMS_PLEASE);
        }
        Object code = result.getData();
        if(!code.toString().equals(smsVo.getCode())){
            result = Result.getDefaultFalse();
            result.setMsg(ErrorCodeUtil.SMS_CODE_ERROR);
            return result;
        }
        return Result.getDefaultTrue();
    }

}
