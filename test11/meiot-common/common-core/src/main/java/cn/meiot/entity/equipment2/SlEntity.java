package cn.meiot.entity.equipment2;

import cn.meiot.entity.equipment2.upstatus.Sl;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SlEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String mid;
    private String cmd;
    private String did;
    private List<Sl> sl;
}
