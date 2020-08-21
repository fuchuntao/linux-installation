package cn.meiot.entity.equipment2;

import cn.meiot.entity.equipment2.upswitch.Switchd;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 开关上传数据
 */
@Data
public class Switch2Entity extends BaseEntity2 implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Switchd> switchlist = new ArrayList<>();

    /*public SwitchEntity (List<Switchd> switchlist){
        this.switchlist = switchlist;
    }

    public SwitchEntity (Switchd switchd){
        this.switchlist.add(switchd);
    }*/
}
