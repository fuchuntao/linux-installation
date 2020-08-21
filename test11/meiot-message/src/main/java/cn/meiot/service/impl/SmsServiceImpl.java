package cn.meiot.service.impl;

import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SmsVo;
import cn.meiot.service.SmsService;
import cn.meiot.utils.ConstantUtil;
import cn.meiot.utils.RandomUtil;
import cn.meiot.utils.SmsTool;
import cn.meiot.utils.VerifyUtil;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SmsTool smsTool;


    @Override
    public void sendSms(SmsVo smsVo) throws Exception {
        //校验手机号码是否合法
        log.info("发送短线验证码的参数：{}",smsVo);
        if (!VerifyUtil.verifyPhone(smsVo.getAccount())) {
            return;
        }

        //判断一分钟之内是否发送过
        String key = ConstantUtil.SMS_CODE_PREFIX + ":" + smsVo.getAccount() + ":" + smsVo.getSmsType();
        Long expire = redisTemplate.getExpire(key);
        if(expire > 0 ){
            long l = ConstantUtil.SMS_CODE_EXPIRE * 60l;
            if((l -expire) <= 60 ){
                log.info("一分钟类已经发过，此次发送作废！");
                return ;
            }
        }


        //生成随机验证码
        String code = RandomUtil.getNum(ConstantUtil.SMS_CODE_LENGTH);
       // String code = "123456";
        log.info("短信验证码为：{}", code);

        //发送
        SendSmsResponse sendSmsResponse =  smsTool.sendSms(smsVo.getAccount(),code);
        if(!sendSmsResponse.getCode().equals("OK")){
            log.info("短信验证码发送失败,原因：{}",sendSmsResponse.getMessage());
            return ;
        }

        //前缀
        String prefix = ConstantUtil.SMS_CODE_PREFIX;
        redisTemplate.opsForValue().set(prefix + ":" + smsVo.getAccount() + ":" + smsVo.getSmsType(),
                code, ConstantUtil.SMS_CODE_EXPIRE, TimeUnit.MINUTES);

    }

    @Override
    public Result getSms(SmsVo smsVo) {
        Result result = Result.getDefaultTrue();
        String key = ConstantUtil.SMS_CODE_PREFIX + ":" + smsVo.getAccount() + ":" + smsVo.getSmsType();
        log.info("key:{}", key);
        String code = (String) redisTemplate.opsForValue().get(key);
        result.setData(code);
        log.info("查询结果：{}", code);
        return result;
    }

    @Override
    public void delCode(SmsVo smsVo) {
        String key = ConstantUtil.SMS_CODE_PREFIX + ":" + smsVo.getAccount() + ":" + smsVo.getSmsType();
        redisTemplate.delete(key);

    }
}
