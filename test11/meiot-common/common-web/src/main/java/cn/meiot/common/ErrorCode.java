package cn.meiot.common;

public class ErrorCode {

    /**
     * 系统错误
     */

    public static final String SYSTEM_ERROR = "meiot-error-00000";


    /**
     * 缺少请求参数
     */

    public static final String MISSING_REQUEST_PARAMETERS = "meiot-error-00001";


    /**
     * 参数解析失败
     */

    public static final String PARAMETER_RESOLUTION_FAILED = "meiot-error-00002";

    /**
     * 参数验证失败
     */

    public static final String PARAMETER_VALIDATION_FAILED = "meiot-error-00003";

    /**
     * 参数绑定失败
     */

    public static final String PARAMETER_BINDING_FAILED = "meiot-error-00004";

    /**
     * 参数验证失败
     */

    public static final String PARAMETER_VAILD_FAILED = "meiot-error-00005";

    /**
     * Not Found
     */

    public static final String NOT_FOUND = "meiot-error-00006";

    /**
     * 不支持当前请求方法
     */

    public static final String THE_CURRENT_REQUEST_METHOD_IS_NOT_SUPPORTED = "meiot-error-00007";

    /**
     * 不支持当前参数类型
     */

    public static final String CURRENT_PARAMETER_TYPES_ARE_NOT_SUPPORTED = "meiot-error-00008";

    /**
     * 违反数据库约束
     */

    public static final String VIOLATION_OF_DATABASE_CONSTRAINTS = "meiot-error-00009";

    /**
     * 用户id不可为空
     */

    public static final String USER_ID_NOT_NULL = "meiot-error-00010";
    /**
     * 参数不可为空
     */

    // static final String PARMA_NOT_BE_NULL = "meiot-error-00000";
}
