package cn.meiot.service.impl;

import cn.meiot.dao.EquipmentUserMapper;
import cn.meiot.dao.UseTimeMapper;
import cn.meiot.entity.db.UseTime;
import cn.meiot.service.UseTimeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UseTimeServiceImpl implements UseTimeService {

    @Autowired
    private EquipmentUserMapper equipmentUserMapper;

    @Autowired
    private UseTimeMapper useTimeMapper;

    @Override
    @Transactional
    public void insert(Long userId,Integer projectId) {
        UseTime useTime = new UseTime(userId,projectId);
        UseTime oldUseTime = useTimeMapper.selectOne(useTime);
        if(oldUseTime != null){
            oldUseTime.setEndTime(null);
            useTimeMapper.updateByPrimaryKey(oldUseTime);
            return ;
        }
        useTimeMapper.insertSelective(useTime);
    }



    @Override
    @Transactional
    public void deleteSerial(Long userId,Integer projectId) {
        UseTime useTime = new UseTime(userId,projectId);
        UseTime oldUseTime = useTimeMapper.selectOne(useTime);
        if(oldUseTime == null){
            return ;
        }
        List<String> strings = equipmentUserMapper.querySerialByUserId(userId);
        if(!CollectionUtils.isEmpty(strings)){
            return ;
        }
        oldUseTime.setEndTime(new Date());
        useTimeMapper.updateByPrimaryKeySelective(oldUseTime);
    }

    @Override
    @Transactional
    public void deleteMasterSerial(String serialNumber) {
        List<String> rtuserIdBySerialNumber = equipmentUserMapper.getRtuserIdBySerialNumber(serialNumber);
        rtuserIdBySerialNumber.forEach( userId->{
            Long user = Long.valueOf(userId);
            deleteSerial(user,null);
        });
    }
}
