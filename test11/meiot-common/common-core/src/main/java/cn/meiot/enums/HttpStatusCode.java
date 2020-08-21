package cn.meiot.enums;

public enum HttpStatusCode {
    /**
     * 成功
     */
    SUCCESS(200),
    /**
     * 路径错误
     */
    NOT_FOUND(404),
    /**
     * 请求不符合要求或者服务器发生系统错误
     */
    BAD_REQUEST(500),
    /**
     * 权限不足
     */
    UNAUTHORIZED(401),

    /**
     * 请求方式不被允许
     */
    METHOD_NOT_ALLOWED(405);

    private Integer value = 200;

    private HttpStatusCode(Integer value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
