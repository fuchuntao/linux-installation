package cn.meiot.dao;

import cn.meiot.entity.db.HuaweiEquipment;
import tk.mybatis.mapper.common.BaseMapper;

public interface HuaweiEquipmentMapper extends BaseMapper<HuaweiEquipment> {
    String querySerialNumber(String deviceId);
}
