package cn.meiot.enums;

/**
 * 消息推送的类型
 */
public enum  PushType {

    /**
     * 账号在其他地方登录
     */
    MAINTYPE(101),

    /**
     * 权限发生改变
     */
    SUBTYPE(102);

    private Integer value = 101;

    PushType(Integer value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}