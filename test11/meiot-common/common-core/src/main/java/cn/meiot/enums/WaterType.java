package cn.meiot.enums;

public enum WaterType {

    /**
     * 客户列表
     */
    CUSTOMER(0),
    /**
     * 水箱列表
     */
    IMETER(1),
    /**
     * 抄表列表
     */
    RECORD(2);

    private Integer value = 0;

    private WaterType(Integer value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
