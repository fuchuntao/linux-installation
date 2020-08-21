package cn.meiot.service;

import cn.meiot.entity.vo.*;

public interface ILoginService {

    /**
     * 用户登录
     * @param login
     * @return
     */
    Result login(Login login);

    /**
     * 忘记密码
     * @param forgetPwdVo
     * @return
     */
    Result forgetPwd(ForgetPwdVo forgetPwdVo);

    /**
     * 注册
     * @param registerVo
     * @return
     */
    Result register(RegisterVo registerVo);

    /**
     * 注销
     * @param userId
     * @return
     */
    Result logout(Long userId,String device);

    /**
     * 修改app个人密码
     * @param resetPwd
     * @return
     */
    Result updatePwd(ResetPwd resetPwd);

    /**
     * 平台登录
     * @param login
     * @return
     */
    Result paltLogin(Login login);

    /**
     * 企业账号登录
     * @param login
     * @return
     */
    Result enterpriseLogin(Login login);

    /**
     * 通过短信验证码修改密码
     * @param userId 用户id
     * @param forgetPwdVo
     * @return
     */
    Result updatePwdBySms(Long userId, ForgetPwdVo forgetPwdVo);

    /**
     * 微信二维码登录处理
     * @param userId
     * @return
     */
    Result wxQrCodeLogin(Long userId,String device);
}
