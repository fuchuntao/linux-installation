package cn.meiot.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fengshaoyu
 * @title: RespData
 * @projectName spacepm
 * @description: 统一返回类
 * @date 2018-12-24 10:14
 */
@Data
@ApiModel()
public class RespData {
    /**
     * 失败状态码
     */
    private static final int ERROR_STATUS = 1;
    /**
     * 成功状态码
     */
    private static final int SUCCESS_STATUS = 0;
    /**
     * 失败说明
     */
    private static final String ERROR_MSG = "error";
    /**
     * 成功说明
     */
    private static final String SUCCESS_MSG ="success";
    /**
     * 返回状态
     */
    @ApiModelProperty("返回状态码")
    private Integer status;
    /**
     * 返回状态说明
     */
    @ApiModelProperty("返回状态说明")
    private String msg;
    /**
     * 返回的数据
     */
    @ApiModelProperty("返回的数据")
    private Object data;

    private static RespData respData = null;

    /**
     * 成功返回
     * @param data 返回的集合
     * @return
     */
    public static RespData success(Object data){
    	if(respData != null){
            synchronized(RespData.class){
                if(respData != null){
                	respData = new RespData();
                	respData.setStatus(SUCCESS_STATUS);
                    respData.setMsg(SUCCESS_MSG);
                    respData.setData(data);
                    return respData;
                }
            }
        }
    	respData = new RespData();
    	respData.setStatus(SUCCESS_STATUS);
        respData.setMsg(SUCCESS_MSG);
        respData.setData(data);
        return respData;
    }

    /**
     * 失败返回
     * @param data 返回的集合
     * @return
     */
    public static RespData error(Object data){
    	if(respData != null){
            synchronized(RespData.class){
                if(respData != null){
                	respData = new RespData();
                	respData.setStatus(ERROR_STATUS);
                    respData.setMsg(ERROR_MSG);
                    respData.setData(data);
                    return respData;
                }
            }
        }
    	respData = new RespData();
    	respData.setStatus(ERROR_STATUS);
        respData.setMsg(ERROR_MSG);
        respData.setData(data);
        return respData;
    }

    /**
     * 返回的集合
     * @param status
     * @param data
     * @return
     */
    public static RespData error(Integer status,Object data){
    	if(respData != null){
            synchronized(RespData.class){
                if(respData != null){
                	respData = new RespData();
                	respData.setStatus(status);
                    respData.setMsg(ERROR_MSG);
                    respData.setData(data);
                    return respData;
                }
            }
        }
    	respData = new RespData();
    	respData.setStatus(status);
        respData.setMsg(ERROR_MSG);
        respData.setData(data);
        return respData;
    }

}
