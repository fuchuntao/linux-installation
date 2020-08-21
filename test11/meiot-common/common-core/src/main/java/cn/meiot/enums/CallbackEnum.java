package cn.meiot.enums;

import java.io.Serializable;

public enum  CallbackEnum implements Serializable {
    SWITCH_STATUS(0), //开关状态
    LEAKAGE_SELF_TEST(1), //漏电自检
    ALARM_WARNING(2), //故障预警报警
    SWITCH_VARIATION(3);//开关变动

    private Integer value;

    CallbackEnum(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
