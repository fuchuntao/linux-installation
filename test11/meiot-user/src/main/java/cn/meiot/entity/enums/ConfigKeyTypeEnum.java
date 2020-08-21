package cn.meiot.entity.enums;

import cn.meiot.enums.FaultMsgContentEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * key值类型
 */
public enum ConfigKeyTypeEnum  {

    TEXT(1,"文本"),

    IMAGES(2,"图片"),

    RICH_TEXT(3,"富文本");

    private final int code;

    private final String descp;


    ConfigKeyTypeEnum(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    public static String getContent(Integer value) {
        ConfigKeyTypeEnum[] businessModeEnums = values();
        for (ConfigKeyTypeEnum businessModeEnum : businessModeEnums) {
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
