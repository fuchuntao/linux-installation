package cn.meiot.entity.enums;

/**
 * 作用范围
 */
public enum ConfigTypeEnum {

    ALL(0,"所有"),

    SYS(1,"系统"),
    USER(2,"用户");

    private final int code;

    private final String descp;


    ConfigTypeEnum(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    public static String getContent(Integer value) {
        ConfigTypeEnum[] businessModeEnums = values();
        for (ConfigTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.code().equals(value)) {
                return businessModeEnum.descp();
            }
        }
        return null;
    }

    public Integer code() {
        return code;
    }

    public String descp() {
        return descp;
    }


    @Override
    public String toString() {
        return this.descp;
    }
}
