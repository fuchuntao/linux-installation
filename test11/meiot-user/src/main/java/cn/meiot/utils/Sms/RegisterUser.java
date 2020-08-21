package cn.meiot.utils.Sms;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.SmsVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.AccountType;
import cn.meiot.service.ISysUserService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.abstracts.SmsCode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RegisterUser implements SmsCode {

    @Autowired
    private ISysUserService sysUserService;


    @Override
    public Result check(SmsVo smsVo) {
        Integer count = sysUserService.count(new QueryWrapper<SysUser>().eq("user_name", smsVo.getAccount()).eq("type",AccountType.PERSONAGE.value()));
        if(null != count && count > 0){
            return new Result().Faild(ErrorCodeUtil.ACCOUNT_EXIST);
        }
        return Result.getDefaultTrue();
    }
}
