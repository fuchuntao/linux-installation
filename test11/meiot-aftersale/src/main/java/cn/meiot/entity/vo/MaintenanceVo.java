package cn.meiot.entity.vo;

import cn.meiot.entity.Maintenance;
import cn.meiot.service.DTOConvert;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class MaintenanceVo {

    /**
     * 故障的设备序列号
     */
    private String serialNumber;

    /**
     * 故障类型
     */
    private Integer mType;

    /**
     * 申报原因
     */
    private String reason;



    public Maintenance convertToMaintenanceDTO(){
        MaintenanceDTOConvert maintenanceDTOConvert = new MaintenanceDTOConvert();
        Maintenance convert = maintenanceDTOConvert.convert(this);
        return convert;
    }

    private static class MaintenanceDTOConvert implements DTOConvert<MaintenanceVo, Maintenance> {
        @Override
        public Maintenance convert(MaintenanceVo maintenanceVo){
            Maintenance maintenance = new Maintenance();
            BeanUtils.copyProperties(maintenanceVo,maintenance);
            return maintenance;
        }
    }
}
