package cn.meiot.enums;

public enum SmsType {

    FORGETPWD(1),REGISTER(2),UPDATEPWD(3);

    private Integer value = 0;

    private SmsType(Integer value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }

}
