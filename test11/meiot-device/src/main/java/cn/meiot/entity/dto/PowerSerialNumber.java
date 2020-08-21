package cn.meiot.entity.dto;

import cn.meiot.entity.db.PowerAppUser;
import lombok.Data;

import java.util.List;

@Data
public class PowerSerialNumber {
    private String serialNumber;
    private String name;
    private List<PowerAppUser> powerAppUserList;
}
