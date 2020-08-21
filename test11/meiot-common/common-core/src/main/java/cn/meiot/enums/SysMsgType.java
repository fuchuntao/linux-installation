package cn.meiot.enums;

public enum  SysMsgType {


    /**
     * 系统公告
     */
    SYS_NOTICE(0),
    /**
     * 绑定请求
     */
    PEND_REQ(1),

    /**
     * 解绑通知
     */
    UNBIND(2);

    private Integer value = 0;

    private SysMsgType(Integer value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
