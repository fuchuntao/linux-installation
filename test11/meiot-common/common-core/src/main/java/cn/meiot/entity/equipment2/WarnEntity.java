package cn.meiot.entity.equipment2;

import cn.meiot.config.CmdConstart;
import cn.meiot.entity.equipment2.warn.SetWarnValue;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置报警及预警值
 */
@Data
public class WarnEntity extends BaseEntity2 implements Serializable {

    private static final long serialVersionUID = 9155802037566900000L;

    private List<SetWarnValue> setWarnValue = new ArrayList<>();

    public WarnEntity(List<SetWarnValue> setWarnValueList){
        this.cmd = CmdConstart.CMD_205;
        this.setWarnValue = setWarnValueList;
    }
    public WarnEntity(SetWarnValue setWarnValueList){
        this.cmd = CmdConstart.CMD_205;
        this.setWarnValue.add(setWarnValueList);
    }

}
