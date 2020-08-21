package cn.meiot.entity.equipment2.upswitch;

import cn.meiot.entity.equipment2.Sid;
import lombok.Data;

import java.io.Serializable;

@Data
public class Switchd extends Sid implements Serializable {

    private Integer index;

    private String type;
}
