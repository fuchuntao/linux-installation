package cn.meiot.utils.enums;

public enum JpushTypeEnum {

    /**
     * 通知栏展示
     */
    NOTIFICATION(1),
    /**
     * app内展示
     */
    PASSTHROUGH(2);

    private Integer value = 0;

    private JpushTypeEnum(Integer value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
