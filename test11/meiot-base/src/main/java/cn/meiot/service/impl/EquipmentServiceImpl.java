package cn.meiot.service.impl;

import cn.meiot.entity.Equipment;
import cn.meiot.mapper.EquipmentMapper;
import cn.meiot.service.IEquipmentService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备信息 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2019-12-10
 */
@Service
@DS("db_2")
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements IEquipmentService {

}
