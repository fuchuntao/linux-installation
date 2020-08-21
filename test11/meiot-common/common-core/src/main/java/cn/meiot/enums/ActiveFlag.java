package cn.meiot.enums;

public enum  ActiveFlag {

    NORMAL("y"),

    DELETED("d");


    private String value = "y";

    private ActiveFlag(String value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
