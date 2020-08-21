package cn.meiot.service.impl.electrician;

import cn.meiot.dao.EquipmentMapper;
import cn.meiot.dao.UserChangeSwitchMapper;
import cn.meiot.entity.db.UserChangeSwitch;
import cn.meiot.service.BuildingService;
import cn.meiot.service.electrician.UserChangeSwitchService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserChangeSwitchServiceImpl implements UserChangeSwitchService {

    @Autowired
    private UserChangeSwitchMapper userChangeSwitchMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private BuildingService buildingService;

    @Override
    public PageInfo queryLog(Long userId,Integer page,Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Map> mapList = userChangeSwitchMapper.queryLog(userId);
        PageInfo pageinfo = new PageInfo<>(mapList);
        return pageinfo;
    }

    @Override
    public UserChangeSwitch changeSwitch(UserChangeSwitch userChangeSwitch) {
        Long oldSwitchSn = userChangeSwitch.getOldSwitchSn();
        Map map = equipmentMapper.queryEquipmentBySwitch(oldSwitchSn);
        if(map == null){
            return null;
        }
        Long buildingId = (Long) map.get("buildingId");
        String name = (String) map.get("name");
        String serialNumber = (String)map.get("serialNumber");
        String address = buildingService.queryAddress(buildingId);
        userChangeSwitch.setCreateTime(new Date());
        userChangeSwitch.setAddress(address);
        userChangeSwitch.setName(name);
        userChangeSwitch.setSerialNumber(serialNumber);
        userChangeSwitchMapper.insertSelective(userChangeSwitch);
        return userChangeSwitch;
    }
}
