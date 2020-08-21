package cn.meiot.entity.vo;


import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.LocaleMessageSourceService;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fengshaoyu
 * @title: Result
 * @projectName meiot
 * @description: 统一返回类
 * @date 2019-05-21 11:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 安卓强烈要求返回一个空对象 : {}
     */
    //private static final Map j = new HashMap();

    @Deprecated
    private static final SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



    /**
     * 成功状态码
     */
    public static final String SUCCESSFUL_CODE = "1";
    /**
     * 成功说明
     */
    public static final String SUCCESSFUL_MESG = "处理成功";


    public static final String TIMEOUT = "1";

    /**
     * 处理结果code
     */
    private  String code;
    /**
     * 处理结果描述信息
     */
    private String msg;

    private T data;


    private boolean result;

    private String createTime;



    /**
     * code+提示消息
     * @param code
     * @param msg
     */
    private  Result(String code,String msg,boolean flag,Object ... args){
        this.code = code;
        setMsg(msg,args);
        this.result = flag;
        this.createTime = ConstantsUtil.getSimpleDateFormat().format(new Date());
    }


    /**
     * code+返回值
     * @param code
     * @param data
     */
    private  Result(String code,T data,boolean flag){
        this.code = code;
        this.data = data;
        this.result = flag;
        this.createTime = ConstantsUtil.getSimpleDateFormat().format(new Date());
    }

    /**
     * code
     * @param code
     */
    public Result(String code) {
        this.code = code;
        this.createTime = ConstantsUtil.getSimpleDateFormat().format(new Date());
    }


    public Result Faild(String msg){
        this.code = "-1";
        setMsg(msg);
        this.result = false;
        this.createTime = ConstantsUtil.getSimpleDateFormat().format(new Date());
        return this;
    }

    public  Result Success(){
        this.code = "0";
        this.msg = "success";
        this.result = false;
        this.createTime = ConstantsUtil.getSimpleDateFormat().format(new Date());
        return this;
    }

    /**
     * 失败返回，只返回提示消息
     * @param msg
     * @return
     */
    public static  Result faild(String msg,Object ... args){
        return new Result("-1",msg,false,args);
    }


    /**
     * 失败返回，带code与提示消息
     * @param code
     * @param msg
     * @return
     */
    public static  Result faild(String code,String msg,Object... args){
        return new Result(code,msg,false,args);
    }

    /**
     * 失败返回，带code与提示消息
     * @param code
     * @param msg
     * @return
     */
    public static  Result faild(ResultCodeEnum resultCodeEnum){
        return new Result(resultCodeEnum.getCode(),resultCodeEnum.getMsg(),false,null);
    }

    /**
     * 默认成功返回
     * @return
     */
    public static Result OK(Object data){
        return new Result("0",data,true);
    }


    /**
     * 默认成功返回
     * @return
     */
    public static Result OK(){
        return new Result("0",null,true);
    }




    /**
     * 成功返回，只返回提示消息
     * @param msg
     * @return
     */
    public static  Result success(String msg,Object ... args){
        return new Result("0",msg,true,args);
    }


    /**
     * 成功返回，带code与提示消息
     * @param code
     * @param msg
     * @return
     */
    public static  Result success(String code,String msg,Object... args){
        return new Result(code,msg,true,args);
    }



    /**
     * 成功返回，只返回提示消息
     * @param msg
     * @return
     */
    public static  Result OK(String msg,Object ... args){
        return new Result("0",msg,true,args);
    }


    /**
     * 成功返回，带code与提示消息
     * @param code
     * @param msg
     * @return
     */
    public static  Result OK(String code,String msg,Object... args){
        return new Result(code,msg,true,args);
    }







    /**
     * 返回成功，带code+数据
     * @return
     */
    public static Result OK(String code ,Object data){
        return new Result(code,data,true);
    }


    /**
     * 成功返回
     * @return
     */
    @Deprecated
    public static  Result getDefaultTrue(){
        return Result.builder().code("0").msg("success").result(true).createTime(sd.format(new Date())).build();
    }



    /**
     * 失败返回
     * @return
     */
    @Deprecated
    public static  Result getDefaultFalse(){
        return Result.builder().code("-1").msg("fail").result(false).createTime(sd.format(new Date())).build();
    }


    /*public T getData() {
        if(data == null){
            return (T) j;
        }
        return data;
    }*/

    public void setMsg(String code, Object ... args){
        try{
            String msg = "";
            if(null != args){
                Object[] obj = new Object[args.length];
                obj = args;
                msg =LocaleMessageSourceService.getMessage(code,obj);
            }else{
                msg = LocaleMessageSourceService.getMessage(code);
            }
            this.msg =msg;
        }catch (Exception e){
            this.msg = code;
        }

    }



}
