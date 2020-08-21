package cn.meiot.enums;

public enum MsgType {

	MAINTYPE("1"),SUBTYPE("2");

    private String value = "2";

    private MsgType(String value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
