package cn.meiot.enums;

public enum EquipmentStatus {
    /**
     * 全部
     */
    ALL(0),
    /**
     * 报警
     */
    ALARM(1),

    /**
     * 预警
     */
    WARNING(2),

    /**
     * 断网
     */
    DISCONNECTION(3);
    private Integer status;

    private EquipmentStatus(Integer status) {    //    必须是private的，否则编译错误
        this.status = status;
    }

    public Integer status() {
        return this.status;
    }

}
