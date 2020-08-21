package cn.meiot.utils.Sms;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SmsVo;
import cn.meiot.service.ISysUserService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.abstracts.SmsCode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ForgetPwd implements SmsCode {

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public Result check(SmsVo smsVo) {
        Result result = Result.getDefaultFalse();
        //判断账号是否存在
        Integer count = sysUserService.count(new QueryWrapper<SysUser>().eq("user_name", smsVo.getAccount()).eq("type",smsVo.getType()));
        if(null == count || count == 0 ){
            result.setMsg(ErrorCodeUtil.USER_NOT_EXIST);
            return  result;
        }
        return Result.getDefaultTrue();
    }
}
