package cn.meiot.utils;

import cn.meiot.entity.vo.Result;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

/**
 * 参数校验
 */
public class VaildUtil {

    public static Result checkParam(String serialNumber,Long switchSn){
        Result result = Result.getDefaultFalse();
        if(StringUtils.isEmpty(serialNumber)){
            result.setMsg("设备序列号不能为空");
            return result;
        }
        if(null == switchSn){
            result.setMsg("开关序号不能为空");
            return result;
        }

        return  Result.getDefaultTrue();

    }
}
