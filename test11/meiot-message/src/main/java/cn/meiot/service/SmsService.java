package cn.meiot.service;

import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SmsVo;

public interface SmsService {
    /**
     * 发送短信验证码
     * @param smsVo
     */
    void sendSms(SmsVo smsVo) throws Exception;

    /**
     * 查询验证码
     * @param smsVo
     * @return
     */
    Result getSms(SmsVo smsVo);

    /**
     * 删除验证码
     * @param smsVo
     */
    void delCode(SmsVo smsVo);
}
