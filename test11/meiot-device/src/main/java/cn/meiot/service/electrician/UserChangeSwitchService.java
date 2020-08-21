package cn.meiot.service.electrician;

import cn.meiot.entity.db.UserChangeSwitch;
import com.github.pagehelper.PageInfo;

public interface UserChangeSwitchService {
    /**
     * 查询电工更换开关日志
     * @param userId
     * @return
     */
    PageInfo queryLog(Long userId,Integer page,Integer pageSize);

    /**
     * 更换开关
     * @param userChangeSwitch
     */
    UserChangeSwitch changeSwitch(UserChangeSwitch userChangeSwitch);
}
