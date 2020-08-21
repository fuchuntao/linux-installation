package cn.meiot.service.api;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DeviceService extends IService<SysUser> {

    /**
     * 通过用户id判断是否属于企业用户
     * @param userId
     * @return
     */
    Result checkEnterprise(Long userId);
}
