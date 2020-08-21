package cn.meiot.entity.equipment2;

import cn.meiot.entity.equipment2.upwarn.WarnInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UpWarnEntity extends BaseEntity2 implements Serializable {
    private List<WarnInfo> warnInfo;
}
