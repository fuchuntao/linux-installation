package cn.meiot.enums;


public enum  ResultCodeEnum {
    //1014 code  需要app需清除设备缓存

    UPDATE_USERNAME_ERROR("1014","meiot_device_error_00000"),
    NO_AUTHENTICATION("1014","meiot_device_error_00001"),
    MAIN_UNBIND("1014","meiot_device_error_00002"),


    EQUIMENT_IS_NULL("1001","meiot_device_error_00003"),
    EQUIMENT_NO_EXISTENT("1002","meiot_device_error_00004"),
    UPDATE_DEFAULT_ERROR("1003","meiot_device_error_00005"),
    NOT_IS_MAIN_USER("1005","meiot_device_error_00006"),
    COMMUNICATION_ERROR("1006","meiot_device_error_00007"),
    UNBIND_SUB_USER_ERROR("1007","meiot_device_error_00008"),
    ALREADY_APPLY("1009","meiot_device_error_00009"),
    IS_YOUR_EQUIMENT("1010","meiot_device_error_00010"),
    UPDATE_BUDLING_ERROR("1011","meiot_device_error_00011"),
    USER_ERROR("1012","meiot_device_error_00012"),
    ALREADY_SERIAL_BIND("1013","meiot_device_error_00013"),
    WATER_SERIAL_BIND("1013","meiot_device_error_00014"),
    ROLE_SERIAL_BIND("1013","meiot_device_error_00015"),

    SERIAL_BIND("1019","meiot_device_error_00016"),
    ALREADY_SWITCH_BIND("1015","meiot_device_error_00017"),
    METER_INSET_ERROR("1016","meiot_device_error_00018"),
    METER_INSET_PROJECT_ERROR("1017","meiot_device_error_00019"),
    METER_INSET_NUMBER_ERROR("1018","meiot_device_error_00020"),
    INSERT_ERROR("1020","meiot_device_error_00021"),
    UPDATE_ERROR("1021","meiot_device_error_00022"),
    //未联网 1022
    NETWORK_ERROR("1022","meiot_device_error_00023"),
    NETWORK_LONGTIME_ERROR("1022","meiot_device_error_00024"),

    //数据统计服务
    //清除数据
    STATISTICS_MAIN_UNBIND("1014","statistics_error_00001"),

    //统计返回为code为2000
    STATISTICS_DATA_IS_NULL("2000", "statistics_success_00002"),


    NOT_FIND_APP_KEY("10001","api_not_find_app_key_10001"),
    NOT_FIND_APP("10002","api_not_find_app_10002"),
    OPEN_API_NO_AUTHORITY("10003","open_api_10003"),
    OPEN_API_EQUIPMENT_BIND("10004","open_api_10004"),
    OPEN_API_NO_EQUIPMENT("10005","open_api_10005"),
    OPEN_API_PARAMETER_ERROR("10006","open_api_10006"),
    OPEN_API_NO_SWITCH("10007","open_api_10007"),

    //统计返回为code为2000
    REPEAT_ADD("10000", "meiot_device_error_10000");


    private String code;

    private String msg;

    private ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
