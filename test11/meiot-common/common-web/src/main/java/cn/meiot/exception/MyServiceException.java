package cn.meiot.exception;

public class MyServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MyServiceException(String code,String exceptMsg) {
        this.code = code;
        //this.msg = msg;
        this.exceptMsg = exceptMsg;
    }

    public MyServiceException(String code,Object ... args) {
        this.exceptMsg = code;
        this.args = args;
    }

    public MyServiceException(String code) {
        this.exceptMsg = code;
    }

    private String code;
    private String msg;

    private String exceptMsg;

    private Object[] args;

    public String getExceptMsg() {
        return exceptMsg;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setExceptMsg(String exceptMsg) {
        this.exceptMsg = exceptMsg;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
