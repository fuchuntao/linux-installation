package cn.meiot.entity.equipment2.examination;

import cn.meiot.entity.equipment2.Sid;
import lombok.Data;

import java.io.Serializable;

@Data
public class CheckResult extends Sid implements Serializable {
    /**
     * 漏电自检返回值
     */
    private Integer leakage;
    /**
     *0表示线路检查异常，1表示线路检查正常
     */
    private Integer result;
}
