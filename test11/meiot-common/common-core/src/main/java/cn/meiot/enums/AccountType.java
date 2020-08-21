package cn.meiot.enums;

public enum AccountType {

    PLATFORM(1), ENTERPRISE(2),PERSONAGE(5);

    private Integer value = 0;

    private AccountType(Integer value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
