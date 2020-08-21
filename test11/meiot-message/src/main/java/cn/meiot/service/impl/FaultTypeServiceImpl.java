package cn.meiot.service.impl;

import cn.meiot.entity.FaultType;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.FaultTypeMapper;
import cn.meiot.service.IFaultTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-24
 */
@Service
public class FaultTypeServiceImpl extends ServiceImpl<FaultTypeMapper, FaultType> implements IFaultTypeService {

@Autowired
private FaultTypeMapper faultTypeMapper;
    @Override
    public Result getFaultTypeList() {
        Result result = Result.getDefaultTrue();
        List<FaultType> faultTypes = faultTypeMapper.selectTypeList();
        List<FaultType> types=new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            types.add(faultTypes.get(i));
        }
        result.setData(types);
        return result;
    }
}
