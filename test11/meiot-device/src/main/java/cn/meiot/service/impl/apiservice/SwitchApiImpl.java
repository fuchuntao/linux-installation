package cn.meiot.service.impl.apiservice;

import cn.meiot.dao.ApISwitchMapper;
import cn.meiot.entity.dto.apiservice.SerialDto;
import cn.meiot.entity.dto.apiservice.SwitchApi;
import cn.meiot.entity.equipment2.BaseEntity2;
import cn.meiot.entity.equipment2.ControlEntity;
import cn.meiot.entity.equipment2.control.Crlpower;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.SwitchService;
import cn.meiot.service.apiservice.SwitchApIService;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SwitchApiImpl implements SwitchApIService {

    @Autowired
    private ApISwitchMapper apISwitchMapper;

    @Autowired
    private SwitchService switchService;

    @Override
    public List listSwitchSn(String serialNumber) {
        List<Map> mapList = apISwitchMapper.listSwitchSn(serialNumber);
        return mapList;
    }

    @Override
    public Result sendSwitch(SerialDto serialDto) {
        String serialNumber = serialDto.getSerialNumber();
        List<SwitchApi> switchApiList = serialDto.getSwitchApiList();
        List<Crlpower> crlpowers = new ArrayList<>();
        Crlpower crlpower = null;
        for (SwitchApi switchApi: switchApiList) {
            crlpower = new Crlpower();
            crlpower.setSid(new Long(switchApi.getSwitchSn()));
            crlpower.setOtype(switchApi.getType());
            crlpowers.add(crlpower);
        }
        ControlEntity controlEntity = new ControlEntity(crlpowers);
        Result result = switchService.baseEntityJson2(controlEntity, serialNumber);
        return result;
    }
}
