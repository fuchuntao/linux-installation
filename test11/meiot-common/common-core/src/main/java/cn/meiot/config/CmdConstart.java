package cn.meiot.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 开关协议
 */
public class CmdConstart {
    //设置报警及预警值
    public static final String CMD_205 = "CMD-205";
    //设置定时信息
    public static final String CMD_206 ="CMD-206";
    //控制开关
    public static final String CMD_207 ="CMD-207";
    //查询硬件版本信息
    public static final String CMD_208 ="CMD-208";
    //查询网络信息
    public static final String CMD_209 ="CMD-209";
    //查询开关列表
    public static final String CMD_210 ="CMD-210";
    //查询开关数据
    public static final String CMD_211 ="CMD-211";
    //查询定时信息(返回信息直接与设置时间类匹配)
    public static final String CMD_212 ="CMD-212";
    //简易数据开关主动上报，上报的时间间隔参考CMD-203，由于此协议频次比较高，因此都用简写代替
    public static final String CMD_213 ="CMD-213";
    //功率及电量数据定时上传
    public static final String CMD_214 ="CMD-214";
    //报警类信息上传
    public static final String CMD_215 ="CMD-215";
    //开关执行信息上传
    public static final String CMD_216 ="CMD-216";
    //漏电自检
    public static final String CMD_221 ="CMD-221";

    private static Map<String,String> map;

    public static String findValue(String key){
        if(map == null){
            synchronized (CmdConstart.class){
                if(map == null){
                    map = new HashMap<>();
                    map.put(CMD_210,"switchlist");
                    map.put(CMD_213,"sl");
                    map.put(CMD_215,"warninfo");
                    map.put(CMD_221,"checkresult");
                }
            }
        }
        return map.get(key);
    }
}
