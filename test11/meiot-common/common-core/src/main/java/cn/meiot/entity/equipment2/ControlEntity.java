package cn.meiot.entity.equipment2;

import cn.meiot.config.CmdConstart;
import cn.meiot.entity.equipment2.control.Crlpower;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ControlEntity extends BaseEntity2 implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Crlpower> crlpower = new ArrayList<>();

    public ControlEntity (List<Crlpower> crlpower ){
        this.cmd = CmdConstart.CMD_207;
        this.crlpower = crlpower;
    }

    public ControlEntity(Crlpower crlpower){
        this.cmd = CmdConstart.CMD_207;
        this.crlpower.add(crlpower);
    }
}
