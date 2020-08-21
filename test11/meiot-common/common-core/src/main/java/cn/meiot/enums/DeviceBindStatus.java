package cn.meiot.enums;

public enum  DeviceBindStatus {

    /**
     * 已拒绝
     */
    REFUSE(0),
    /**
     * 待处理
     */
    PENDING(1),

    /**
     * 已同意
     */
    AGREE(2);

    private Integer value = 0;

    private DeviceBindStatus(Integer value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
