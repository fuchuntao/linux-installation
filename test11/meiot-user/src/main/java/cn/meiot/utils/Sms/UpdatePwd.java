package cn.meiot.utils.Sms;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SmsVo;
import cn.meiot.service.ISysUserService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.abstracts.SmsCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdatePwd  implements SmsCode {

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public Result check(SmsVo smsVo) {

        SysUser sysUser = sysUserService.getById(smsVo.getUserId());
        if(null == sysUser  || !sysUser.getType().equals(smsVo.getType())){
            return  new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        smsVo.setAccount(sysUser.getUserName());
        return Result.getDefaultTrue();
    }
}
