package cn.meiot.utils;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountTypeUtil {

    /**
     * 校验此操作是否合法
     *
     * @param adminType  操作员的账号类型
     * @param userType  被操作员的账号类型
     * @return Result
     */
    public static Result check(Integer adminType, Integer userType) {
        Result result = Result.getDefaultFalse();
        //获取操作用户的账户类型
        if (null == adminType || null == userType) {
            log.info("操作员或用户的类型有误,操作员类型：{}，被操作员类型{}", adminType, userType);
            result.setMsg("操作员或用户的类型有误");
            return result;
        }
        if (adminType != userType && adminType != ConstantsUtil.ACCOUNT_TYPE) {
            result.setMsg("您未有此接口权限");
            return result;
        }
        return Result.getDefaultTrue();
    }


    public static Long getMainUserId(SysUser sysUser){
        Long mainId = sysUser.getBelongId() == 0 ? sysUser.getId() : sysUser.getBelongId();
        return  mainId;
    }

}
