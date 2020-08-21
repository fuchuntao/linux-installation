package cn.meiot.entity.device;

import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.Meter;
import cn.meiot.entity.equipment.Status;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SckData implements Serializable {

    private Device device;

    private Status status;

    private List<Meter> meter;
}
