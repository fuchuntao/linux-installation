package cn.meiot.utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package cn.meiot.utils.enums
 * @Description:
 * @author: 武有
 * @date: 2020/6/12 11:06
 * @Copyright: www.spacecg.cn
 */
public class AlarmEnum {
    public static final Map<Integer, Map<String, Integer>> map = new HashMap();
    static{
        map.put(1,getMap(1,1));
        map.put(2,getMap(2,3));
        map.put(3,getMap(1,3));
        map.put(4,getMap(1,4));
        map.put(5,getMap(1,2));
        map.put(6,getMap(1,6));
        map.put(7,getMap(1,7));
//        map.put(8,getMap(2,0));
//        map.put(9,getMap(2,1));
        map.put(10,getMap(2,1));
        map.put(11,getMap(2,6));
        map.put(12,getMap(2,7));
        map.put(13,getMap(2,2));
        map.put(14,getMap(2,4));

    }
    private static Map<String, Integer> getMap (Integer key,Integer value) {
        Map<String, Integer> map=new HashMap<>();
        map.put("type",key);
        map.put("value",value);
        return map;
    }

    public static  Map<String, Integer> getValue(Integer key) {
        Map<String, Integer> map = AlarmEnum.map.get(key);
        if (null == map) {
            return null;
        }
        return map;
    }

}
