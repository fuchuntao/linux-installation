package cn.meiot.entity.equipment2;

import cn.meiot.config.CmdConstart;
import cn.meiot.entity.equipment2.time.SetTimer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TimeEntity extends BaseEntity2 implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SetTimer> settimer = new ArrayList<>();

    public TimeEntity(SetTimer settimer){
        this.settimer.add(settimer);
        this.cmd = CmdConstart.CMD_206;
    }

    public TimeEntity(List<SetTimer> settimer){
        this.settimer = settimer;
        this.cmd = CmdConstart.CMD_206;
    }
}
