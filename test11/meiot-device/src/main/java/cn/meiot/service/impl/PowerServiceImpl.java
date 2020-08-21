package cn.meiot.service.impl;

import cn.meiot.constart.TableConstart;
import cn.meiot.dao.EquipmentUserMapper;
import cn.meiot.dao.PowerMapper;
import cn.meiot.entity.db.EquipmentUser;
import cn.meiot.entity.db.Power;
import cn.meiot.entity.db.PowerAppUser;
import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.Status;
import cn.meiot.entity.equipment.sckgzt.Sckgzt;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.PowerService;
import cn.meiot.service.SwitchService;
import cn.meiot.utils.RedisConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PowerServiceImpl implements PowerService {

    @Autowired
    private PowerMapper powerMapper;

    @Autowired
    private EquipmentUserMapper equipmentUserMapper;

    @Autowired
    private SwitchService switchService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public Result insert(Power power) {
        String serialNumber = power.getSerialNumber();
        Set<PowerAppUser> powerAppUserList = power.getPowerAppUserList();
        //获取设备及开关列表
        powerMapper.insertSelective(power);
        Long id = power.getId();
        powerMapper.insertTableUser(id,powerAppUserList,serialNumber,TableConstart.POWER);
        //如果不打开  则返回
        if(!power.getIsSwitch()){
            return Result.getDefaultTrue();
        }
        Integer power1 = power.getPower();
        //设置开关功率
        sendOpenPower(serialNumber,powerAppUserList, power1);
        return Result.getDefaultTrue();
    }

    private void sendOpenPower(String serialNumber, Set<PowerAppUser> powerAppUserList, Integer power1) {
        if(CollectionUtils.isEmpty(powerAppUserList)){
            return;
        }
        List<Sckgzt> list = new ArrayList<>();
        for (PowerAppUser powerAppUser: powerAppUserList) {
            Integer index = powerAppUser.getIndex();
            Device device = new Device();
            device.setIndex(index);
            device.setId(Long.valueOf(powerAppUser.getSwitchSn()));
            Status status = new Status();
            status.setPower(power1);
            list.add(new Sckgzt(device,status));
        }
        switchService.baseEntityJson(serialNumber,list);
    }

    /*private void sendOpenPower(List<PowerSerialNumber> powerSerialNumberList,final Integer power1) {
        if(CollectionUtils.isEmpty(powerSerialNumberList)){
            return;
        }
        //Set<String> serialNumberSet = new HashSet<>();
        powerSerialNumberList.forEach(powerSerialNumber->{
            List<PowerAppUser> powerAppUserList = powerSerialNumber.getPowerAppUserList();
            if(CollectionUtils.isEmpty(powerAppUserList)){
                return;
            }
            String serialNumber = powerSerialNumber.getSerialNumber();
            List<Sckgzt> list = new ArrayList<>();
            for (PowerAppUser powerAppUser: powerAppUserList) {
                Integer index = powerAppUser.getIndex();
                Device device = new Device();
                device.setIndex(index);
                device.setId(Long.valueOf(powerAppUser.getId()));
                Status status = new Status();
                status.setPower(power1);
                list.add(new Sckgzt(device,status));
            }
            switchService.baseEntityJson(serialNumber,list);
        });
    }*/

    @Override
    @Transactional
    public Result update(Power newPower) {
        //失效状态
        Integer status = 1;
        Long id = newPower.getId();
        Set<PowerAppUser> powerAppUserList = newPower.getPowerAppUserList();
        Power oldPower = powerMapper.selectByPrimaryKey(id);
        String serialNumber = oldPower.getSerialNumber();
        //删除数据
        powerMapper.deleteByTableUser(id,TableConstart.POWER,status);
        //重新添加
        powerMapper.insertTableUser(id,powerAppUserList,serialNumber,TableConstart.POWER);
        //之前打开情况
        boolean oldSwitch = oldPower.getIsSwitch();
        //现在打开情况
        boolean newSwitch = newPower.getIsSwitch();
        if((newSwitch) || (!oldSwitch)){
            Set<PowerAppUser> oldSwitchList = powerMapper.queryPowerUser(id);
            //先给零
            sendOpenPower(serialNumber,oldSwitchList, 0);
            //再给值
            sendOpenPower(serialNumber,powerAppUserList, newPower.getPower());
        }
        powerMapper.updateByPrimaryKeySelective(newPower);
        return Result.getDefaultTrue();
    }

    @Override
    @Transactional
    public Result delete(Power newPower) {
        Long id = newPower.getId();
        Power oldPower = powerMapper.selectByPrimaryKey(id);
        boolean oldSwitch = oldPower.getIsSwitch();
        if(oldSwitch){
            Set<PowerAppUser> oldSwitchList = powerMapper.queryPowerUser(id);
            //先给零
            sendOpenPower(oldPower.getSerialNumber(),oldSwitchList, 0);
        }
        powerMapper.deleteByPrimaryKey(id);
        powerMapper.deleteByTableUser(id,TableConstart.POWER,null);
        return Result.getDefaultTrue();
    }

    @Override
    public Result query(String switchSn,Long userId) {
        List<Map> powers = powerMapper.query(switchSn);
        powers.forEach(map->{
            Integer id = Integer.valueOf(map.get("id").toString());
            String serialNumber = (String) map.get("serialNumber");
            List<Map> result = queryById(id,userId,serialNumber, TableConstart.POWER);
            List<Map> selection = result.stream().filter(map1 -> map1.get("selection")!= null && !map1.get("selection").equals(0)).collect(Collectors.toList());
            map.put("powerAppUserList",selection);
        });
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(powers);
        return defaultTrue;
    }

    public void queryAdminUser(String serialNumber,Long userId){
        EquipmentUser equipmentUser = new EquipmentUser();
        equipmentUser.setUserId(userId);
        equipmentUser.setSerialNumber(serialNumber);
        equipmentUser.setUserStatus(1);
        int i = equipmentUserMapper.selectCount(equipmentUser);
        if(i != 1){
            throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
        }
    }

    @Override
    public List<Map> queryById(Integer id, Long userId,String serialNumber,String table) {
        List<Map> listMap = powerMapper.queryBySerialNumberAndId(serialNumber,id,table);
        listMap.forEach(map -> {
            String switchSn = (String) map.get("switchSn");
            String name = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SWITCH, userId+"_"+switchSn);
            map.put("name",name);
            Integer powerUserId = 0;
            if(id != null){
                powerUserId = powerMapper.queryBySwitchSnAndId(switchSn,id, table);
            }
            map.put("selection",powerUserId);
        });
        return listMap;
    }

    @Override
    @Transactional
    public Result isSwitch(Power newPower) {
        powerMapper.updateByPrimaryKeySelective(newPower);
        Long id = newPower.getId();
        Power oldPower = powerMapper.selectByPrimaryKey(id);
        String serialNumber = oldPower.getSerialNumber();
        //现在打开情况
        boolean newSwitch = newPower.getIsSwitch();
        Set<PowerAppUser> oldSwitchList = powerMapper.queryPowerUser(id);
        if(newSwitch){
            sendOpenPower(serialNumber,oldSwitchList, newPower.getPower());
        }else{
            sendOpenPower(serialNumber,oldSwitchList, 0);
        }
        return Result.getDefaultTrue();
    }
}
