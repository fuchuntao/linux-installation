package cn.meiot.utlis;

import cn.meiot.entity.db.Equipment;

import java.math.BigDecimal;

public class EquipmentUtils {

    public static void setModelAndVoltage(Equipment equipment){
        String serialNumber = equipment.getSerialNumber();
        if(serialNumber.contains("M")) {
            equipment.setModel("M系列");
        }else if(serialNumber.contains("P")) {
            equipment.setModel("P系列");
        }
        String str = String.valueOf(serialNumber.toCharArray()[1]);
        if("2".equals(str)) {
            equipment.setVoltage(new BigDecimal(230));
        }else if("3".equals(str)) {
            equipment.setVoltage(new BigDecimal(380));
        }
    }
}
