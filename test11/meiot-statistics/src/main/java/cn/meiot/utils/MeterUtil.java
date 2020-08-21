package cn.meiot.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeterUtil {

    /**
     * 获取总电量
     *
     * @param list
     * @return
     */
    public static BigDecimal addMeter(List<Map<String, Object>> list) {

        BigDecimal totalMeter = BigDecimal.ZERO;
        if (null != list && list.size() > 0) {
            //获取当天总电量
            for (Map m : list) {
                totalMeter = totalMeter.add((BigDecimal) m.get("meter"));
            }
        }
        return totalMeter;
    }


    /**
     *
     * @Title: meterMaxMin
     * @Description: 获取电量的最大值，最小值
     * @param monthlyMeter
     * @param type 0:表示时间点，1为时间段
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    public static Map<String, Object> meterMaxMin(List<Map<String, Object>> monthlyMeter,Integer type) {

        Map<String, Object> map = new HashMap<>();
        BigDecimal maxMeter = BigDecimal.ZERO;
        BigDecimal minMeter = null;
        BigDecimal totalMeter = BigDecimal.ZERO;


        //最大电量(maxMeter)
        for (Map<String, Object> objectMap : monthlyMeter) {
            Object value = objectMap.get("value");
            if(type == 1) {
                value = objectMap.get("meter");
            }
            if (value == null) {
                value = BigDecimal.ZERO;
            }
            if (maxMeter == null) {
                maxMeter = (BigDecimal) value;
            } else {
                maxMeter = maxMeter.max((BigDecimal) value);
            }
            if (minMeter == null) {
                minMeter = (BigDecimal) value;
            } else {
                minMeter = minMeter.min((BigDecimal) value);
            }
            totalMeter = totalMeter.add((BigDecimal) value);

        }
        map.put("totalMeter", totalMeter.setScale(1, BigDecimal.ROUND_HALF_UP));
        map.put("maxMeter", maxMeter.setScale(1, BigDecimal.ROUND_HALF_UP));
        map.put("minMeter", minMeter == null ? BigDecimal.ZERO : minMeter.setScale(1, BigDecimal.ROUND_HALF_UP));
        return map;
    }
}
