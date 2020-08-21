package cn.meiot.service.impl.api;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.bo.DeviceAccountTypeBo;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.AccountType;
import cn.meiot.mapper.SysUserMapper;
import cn.meiot.service.api.DeviceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements DeviceService {

    @Autowired
    private SysUserMapper sysUserMapper;


    @Override
    public Result checkEnterprise(Long userId) {
        Result result = Result.getDefaultFalse();
        //通过用户id获取用户信息
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("id", userId));

        if(null == sysUser){
            result.setMsg("此用户不存在");
            return result;
        }
        result =Result.getDefaultTrue();
        //判断用户是否属于个人账户
        if(AccountType.PERSONAGE.value() == sysUser.getType()){
            result.setData(DeviceAccountTypeBo.builder().isEnterprise(false).build());
            return result;
        }else if(AccountType.ENTERPRISE.value() == sysUser.getType()){
            //此用户属于企业用户
            DeviceAccountTypeBo deviceAccountTypeBo = DeviceAccountTypeBo.builder()
                    .userId(0l == sysUser.getBelongId() ? sysUser.getId() :sysUser.getBelongId())
                    .isEnterprise(true)
                    .build();
            result.setData(deviceAccountTypeBo);
            return  result;
        }else{
            //此账户既不属于个人账户也不属于企业账户
            result = Result.getDefaultFalse();
            result.setMsg("账号类型有误");
            return result;
        }

    }
}
