package cn.meiot.utils;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/12/28 10:07
 * @Copyright: www.spacecg.cn
 */
public class FaultMsgUtils {

    public static String getName(String faultTypeId,String faultValue){
        Double d=Double.valueOf(faultValue);
        if ("4".equals(faultTypeId)){
            if (d>=0 && d<500){
                return "电热毯/烧水壶";
            }
            if (d>500 && d<=1000){
                return "电饭煲";
            }
            if (d>1000 && d<=1500){
                return "吹风机";
            }
            if (d>1500 && d<=2000){
                return "热得快";
            }
            if (d>2000 && d<=3000){
                return "电磁炉";
            }
            if (d>3000){
                return "电取暖器";
            }
            return "未知";
        }else{
            return "-";
        }
    }


}
