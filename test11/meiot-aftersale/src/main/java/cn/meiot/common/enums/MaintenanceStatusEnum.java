package cn.meiot.common.enums;

public enum MaintenanceStatusEnum {

    REPAIRS(1),ACCEPT(2),DISPOSE(3);

    private Integer value = 1;

    private MaintenanceStatusEnum(Integer value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
