package cn.meiot.service.mq;

import cn.meiot.dto.PasswordDto;
import cn.meiot.entity.db.HuaweiEquipment;
import cn.meiot.service.EquipmentService;
import cn.meiot.service.HuaweiEquipmentService;
import cn.meiot.utils.QueueConstantUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class HuaweiServiceImpl {

    @Autowired
    private HuaweiEquipmentService huaweiEquipmentService;

    @Autowired
    private EquipmentService equipmentService;

    @RabbitListener(queues= QueueConstantUtil.REGISTE_RSERIAL)
    public void registerSerialPasswod(PasswordDto passwordDto){
        try {
            HuaweiEquipment huaweiEquipment = new HuaweiEquipment();
            BeanUtils.copyProperties(huaweiEquipment,passwordDto);
            huaweiEquipmentService.addHuaweiEquipment(huaweiEquipment);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RabbitListener(queues= QueueConstantUtil.ACTIVATION_RSERIAL)
    public void activationSerial(LinkedHashMap linkedHashMap){
        try {
            equipmentService.activationSerial(linkedHashMap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
