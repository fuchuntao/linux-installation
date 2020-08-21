package cn.meiot.utils;

import java.util.Map;

public class SwitchUtil {
    private SwitchUtil(){}

    public static Object getLoadMax(Map map){
        Integer loadMaxStatus = (Integer) map.get("loadMaxStatus");
        //如果状态为关闭 则返回 关闭的值   因为设置了具体的值 PHP会覆盖最新的值
        if(loadMaxStatus != null && loadMaxStatus.equals(0)){
            Object offLoadMax = map.get("offLoadMax");
            return offLoadMax;
        }
        //如果没设置过任何状态则直接使用缓存的值
        Object loadmax = map.get("loadmax");
        if(loadmax == null){
            return 0;
        }
        return loadmax;
    }

    public static Integer getSwitchStatus(Map map){
        return map.get("switch")!=null ?(Integer) map.get("switch"):0;
    }

    public static Integer getLoadMaxStatus(Map map){
        //不为null 则取值
        Integer loadMaxStatus = (Integer) map.get("loadMaxStatus");
        if(loadMaxStatus != null ){
            return loadMaxStatus;
        }
        Integer loadmax = (Integer) map.get("loadmax");
        //如果loadmax > 0 则说明开启了功率具体的值
        if(loadmax != null && !loadmax.equals(0)){
            return 1;
        }
        //否则为关闭
        return 0;
    }
}
