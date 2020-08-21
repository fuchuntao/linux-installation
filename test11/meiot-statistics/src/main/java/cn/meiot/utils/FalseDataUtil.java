package cn.meiot.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 假数据
 */
@Slf4j
public class FalseDataUtil {

    /**
     * 设备数量
     */
    public static final Integer DEIVCE_SUM = 300;

    /**
     * 设备数量
     */
    public static final Integer ONLINE_DEVICE = 258;



    /**
     * 设备数量
     */
    public static  BigDecimal SUM_DATA_2019 = BigDecimal.ZERO;


    /**
     * 2019年数据
     */
    public static  Map<Integer, BigDecimal> DATA_2019 = new HashMap<Integer, BigDecimal>();


    static {
        DATA_2019.put(1,BigDecimal.valueOf(3098l));
        DATA_2019.put(2,BigDecimal.valueOf(7665l));
        DATA_2019.put(3,BigDecimal.valueOf(8008l));
        DATA_2019.put(4,BigDecimal.valueOf(8512l));
        DATA_2019.put(5,BigDecimal.valueOf(9500l));
        DATA_2019.put(6,BigDecimal.valueOf(12003l));
        DATA_2019.put(7,BigDecimal.valueOf(15265l));
        DATA_2019.put(8,BigDecimal.valueOf(18080l));
        DATA_2019.put(9,BigDecimal.valueOf(15032l));
        DATA_2019.put(10,BigDecimal.valueOf(13421l));
        DATA_2019.put(11,BigDecimal.valueOf(9500l));
        DATA_2019.put(12,BigDecimal.valueOf(5000l));
        Collection<BigDecimal> values=DATA_2019.values();
        Iterator<BigDecimal> keyInterator2=values.iterator();
        while (keyInterator2.hasNext()){
            SUM_DATA_2019.add(keyInterator2.next());
        }
        log.info("数据添加完成，2019电量总和：{}",SUM_DATA_2019);

    }

}
