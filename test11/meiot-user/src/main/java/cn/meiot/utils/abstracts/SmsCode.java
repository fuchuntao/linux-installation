package cn.meiot.utils.abstracts;

import cn.meiot.entity.vo.SmsVo;
import cn.meiot.entity.vo.Result;
import org.springframework.stereotype.Component;

/**
 * 发送短信验证码的基类
 */
public  interface SmsCode {

    /**
     * 发送验证码
     * @return
     */
    Result check(SmsVo smsVo);
}
