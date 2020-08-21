package cn.meiot.dao;

import cn.meiot.entity.db.UserChangeSwitch;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;

public interface UserChangeSwitchMapper extends BaseMapper<UserChangeSwitch> {
    List<Map> queryLog(Long userId);
}
