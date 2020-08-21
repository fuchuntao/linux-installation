package cn.meiot.utils.factory;

import cn.meiot.enums.SmsType;
import cn.meiot.utils.Sms.ForgetPwd;
import cn.meiot.utils.Sms.RegisterUser;
import cn.meiot.utils.Sms.UpdatePwd;
import cn.meiot.utils.abstracts.SmsCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 短信类型工厂
 */
@Component
@Slf4j
public class SmsFactory {

    @Autowired
    private ForgetPwd forgetPwd;

    @Autowired
    private RegisterUser registerUser;

    @Autowired
    private UpdatePwd updatePwd;

    /**
     * 通过短信类型获取对应的实例
     * @param type
     * @return
     */
    public SmsCode getSmsCode(Integer type){
        if(null == type){
            return null;
        }
        if(type.equals(SmsType.FORGETPWD.value())){
            log.info("ForgetPwd===============");
            return forgetPwd;
        }else if (type.equals(SmsType.REGISTER.value())){
            log.info("RegisterUser===============");
            return registerUser;
        }else if(type.equals(SmsType.UPDATEPWD.value())){
            log.info("UpdatePwd===============");
            return updatePwd;
        }
        return null;
    }
}
