package cn.meiot.service.impl.apiservice;

import cn.meiot.dao.EquipmentApiMapper;
import cn.meiot.entity.dto.apiservice.SerialDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.EquipmentUserService;
import cn.meiot.service.apiservice.EquipmentApiService;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EquipmentApiServiceImpl implements EquipmentApiService {

    @Autowired
    private EquipmentUserService equipmentUserService;

    @Autowired
    private EquipmentApiMapper equipmentApiMapper;

    @Override
    public Result insertSerial(SerialDto serialDto) {
        int row = equipmentApiMapper.insertSerial(serialDto);
        return row>0?Result.OK():Result.faild(ResultCodeEnum.INSERT_ERROR.getCode());
    }

    @Override
    public Result deleteSerial(SerialDto serialDto) {
        equipmentApiMapper.deleteSerial(serialDto);
        return Result.OK();
    }

    @Override
    public Result listSerial(Integer page, Integer pageSize, Long appId) {
        Integer total = equipmentApiMapper.totalSerial(appId);
        List<Map> list = equipmentApiMapper.listSerial(page,pageSize,appId);
        Map map = new HashMap();
        map.put("total",total);
        map.put("list",list);
        return Result.OK(map);
    }


    @Override
    public void authentication(String serialNumber, Long appId) {
        Long aLong = selectIdBySerialNumber(serialNumber);
        if(!appId.equals(aLong)){
            throw new MyServiceException("");
        }
    }

    @Override
    public Long selectIdBySerialNumber(String serialNumber) {
        Long appId = equipmentApiMapper.selectIdBySerialNuber(serialNumber);
        return appId;
    }
}
