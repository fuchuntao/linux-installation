package cn.meiot.service;

import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.UserWXBindVo;

public interface WxService {

     Result getTicket(Long userId);

    /**
     * 微信二维码登录
     * @param code
     * @return
     */
    Result qrCodeLogin(String code,String device);

    /**
     * 绑定
     * @param userWXBindVo
     * @return
     */
    Result bindingWx(UserWXBindVo userWXBindVo,String device);

    /**
     * 解绑
     * @param userId
     * @return
     */
    Result unBindWx(Long userId);
}
