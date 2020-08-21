package cn.meiot.enums;


/**
 * 1	漏电保护
        2	过温提醒
        3	过温保护
        4	过载保护
        5	短路保护
        6	过压保护
        7	欠压保护
 */


public enum FaultTitleEnum {

    LEAKAGEPROTECTION(1,"漏电报警"),
    OVERTEMPERATURETIP(2,"过流报警"),
    OVERTEMPERATUREPROTECTION(3,"过温报警"),
    OVERLOADPROTECTION(4,"过载报警"),
    SHORTOUTPROTECTION(5,"短路报警"),
    OVERVOLTAGEPROTECTION(6,"过压报警"),
    UNDERVOLTAGEPROTECTION(7,"欠压报警"),
    MANUAL(8,"手动分闸"),
    LOUDIAN(10,"漏电预警"),
    GUOLIU(11,"过流预警"),
    GUOWEN(12,"过温预警"),
    GUOZAI(13,"过载预警"),
    DUANLU(14,"短路预警"),
    GUOYA(15,"过压预警"),
    QIANYA(16,"欠压预警");
    private   String value;
    private  Integer index;

    FaultTitleEnum(Integer index, String value){
        this.index = index;
        this.value = value;
    }

    public static String getTitle(Integer value) {
        FaultTitleEnum[] businessModeEnums = values();
        for (FaultTitleEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.index().equals(value)) {
                return businessModeEnum.value();
            }
        }
        return null;
    }

    public static String getTitle2(Integer value) {
        FaultTitleEnum[] businessModeEnums = values();
        for (FaultTitleEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.index().equals(value)) {
                return businessModeEnum.value().substring(0,2);
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
