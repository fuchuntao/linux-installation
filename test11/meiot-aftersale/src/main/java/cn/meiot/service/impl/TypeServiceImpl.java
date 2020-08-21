package cn.meiot.service.impl;

import cn.meiot.entity.Maintenance;
import cn.meiot.entity.Type;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.MaintenanceMapper;
import cn.meiot.mapper.TypeMapper;
import cn.meiot.service.TypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @Package cn.meiot.service.impl
 * @Description:
 * @author: 武有
 * @date: 2019/9/17 15:11
 * @Copyright: www.spacecg.cn
 */
@Service
@Slf4j
public class TypeServiceImpl  implements TypeService {

    @Autowired
    private TypeMapper typeMapper;

    @Override
    public List<Type> getTypeList() {
        return typeMapper.selecTypeList();
    }

    @Override
    public Result addType(String name) {
        return typeMapper.addType(name)==1?Result.getDefaultTrue():Result.getDefaultFalse();
    }
}
