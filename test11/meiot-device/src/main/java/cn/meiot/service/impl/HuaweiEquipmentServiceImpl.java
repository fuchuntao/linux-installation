package cn.meiot.service.impl;

import cn.meiot.dao.HuaweiEquipmentMapper;
import cn.meiot.dto.PasswordDto;
import cn.meiot.entity.db.HuaweiEquipment;
import cn.meiot.service.HuaweiEquipmentService;
import cn.meiot.utils.RedisConstantUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
public class HuaweiEquipmentServiceImpl implements HuaweiEquipmentService {

    @Autowired
    private HuaweiEquipmentMapper huaweiEquipmentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public void addHuaweiEquipment(HuaweiEquipment huaweiEquipment){
        huaweiEquipmentMapper.insert(huaweiEquipment);
        redisTemplate.opsForHash().put(RedisConstantUtil.HUAWEI_PASSWORD_SERIAL, huaweiEquipment.getDeviceId(),JSONObject.toJSONString(huaweiEquipment));
    }

    @Override
    public PasswordDto queryPasswordDtoBySerial(String serialNumber) {
        String json = (String) redisTemplate.opsForHash().get(RedisConstantUtil.HUAWEI_PASSWORD_SERIAL, serialNumber);
        PasswordDto passwordDto = JSONObject.parseObject(json, PasswordDto.class);
        if( passwordDto != null){
            return passwordDto;
        }else{
            passwordDto = new PasswordDto();
        }
        HuaweiEquipment huaweiEquipment = huaweiEquipmentMapper.selectByPrimaryKey(serialNumber);
        if(huaweiEquipment == null){
            return null;
        }
        try {
            BeanUtils.copyProperties(passwordDto,huaweiEquipment);
            redisTemplate.opsForHash().put(RedisConstantUtil.HUAWEI_PASSWORD_SERIAL, serialNumber, JSONObject.toJSONString(passwordDto));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return passwordDto;
    }

    @Override
    public String querySerialNumber(String deviceId) {
        return huaweiEquipmentMapper.querySerialNumber(deviceId);
    }
}
