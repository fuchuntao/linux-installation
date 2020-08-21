package cn.meiot.exception;

public class MyTokenExcption extends RuntimeException{


    private static final long serialVersionUID = 1L;

    public MyTokenExcption(String code,String exceptMsg) {
        this.code = code;
        //this.msg = msg;
        this.exceptMsg = exceptMsg;
    }

    private String code;
    private String msg;

    private String exceptMsg;

    public String getExceptMsg() {
        return exceptMsg;
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
