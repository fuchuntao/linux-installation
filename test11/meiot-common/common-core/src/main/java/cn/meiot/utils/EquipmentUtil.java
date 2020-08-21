package cn.meiot.utils;

import org.springframework.util.StringUtils;

import java.util.Map;

public class EquipmentUtil {
    private EquipmentUtil(){}


    public static String getImageKey(Map<String, Map<String,String>> maps,String serialNumber,String key){
        if(StringUtils.isEmpty(serialNumber)){
            return null;
        }
        String substring = serialNumber.substring(0,1);
        Map<String, String> obj = maps.get(substring);
        if(obj == null){
            obj = maps.get("default");
        }
        return obj.get(key);
    }
}
