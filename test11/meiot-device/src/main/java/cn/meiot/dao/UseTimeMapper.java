package cn.meiot.dao;

import cn.meiot.entity.db.UseTime;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.Map;

public interface UseTimeMapper extends BaseMapper<UseTime> {
    /**
     * 查询开始时间和结束时间
     * @param useTime
     * @return
     */
    Map queryUseTime(UseTime useTime);
}
