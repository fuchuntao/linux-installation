package cn.meiot.enums;

public enum FaultMsgContentEnum {

    LEAKAGEPROTECTION(1,"发生漏电报警,请尽快处理！"),
    OVERTEMPERATURETIP(2,"发生过流报警,请尽快处理！"),
    OVERTEMPERATUREPROTECTION(3,"发生过温报警，请尽快处理！"),
    OVERLOADPROTECTION(4,"发生过载报警,请尽快处理！"),
    SHORTOUTPROTECTION(5,"发生短路报警,请尽快处理！"),
    OVERVOLTAGEPROTECTION(6,"发生过压报警,请尽快处理！"),
    UNDERVOLTAGEPROTECTION(7,"发生欠压报警,请尽快处理！"),
    LOUDIAN(10,"发生漏电预警,请尽快处理！"),
    GUOLIU(11,"发生过流预警,请尽快处理！"),
    GUOWEN(12,"发生过温预警,请尽快处理！"),
    GUOZAI(13,"发生过载预警,请尽快处理！"),
    DUANLU(14,"发生短路预警,请尽快处理！"),
    GUOYA(15,"发生过压预警,请尽快处理！"),
    QIANYA(16,"发生欠压预警,请尽快处理！");

    private   String value;
    private  Integer index;

    FaultMsgContentEnum(Integer index, String value){
        this.index = index;
        this.value = value;
    }

    public static String getContent(Integer value) {
        FaultMsgContentEnum[] businessModeEnums = values();
        for (FaultMsgContentEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.index().equals(value)) {
                return businessModeEnum.value();
            }
        }
        return null;
    }



    public Integer index() {
        return index;
    }

    public String value() {
        return value;
    }
}
