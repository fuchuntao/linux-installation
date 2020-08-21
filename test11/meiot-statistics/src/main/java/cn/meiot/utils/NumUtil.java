package cn.meiot.utils;

import cn.meiot.entity.bo.MeterStatisticalBo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

@Slf4j
public class NumUtil {


    /**
     * 计算百分比
     * @param num1 需要计算所占比例的数
     * @param num2 总数
     * @return
     */
    public static BigDecimal percent(BigDecimal num1, BigDecimal num2) {
        if(num1.compareTo(BigDecimal.ZERO) ==  0 || num2.compareTo(BigDecimal.ZERO) ==  0){
            return BigDecimal.ZERO;
        }
        BigDecimal multiply = (num1.divide(num2,2,BigDecimal.ROUND_HALF_UP));
        log.info("比例：{}",multiply);
        return multiply;
    }



    /**
     * 计算百分比
     * @return
     */
    public static BigDecimal percent2(List<MeterStatisticalBo> list,Integer month) {
        BigDecimal sumMeter = BigDecimal.ZERO;
        if(null == list || list.size() == 0){
            return sumMeter;
        }
        BigDecimal nowMonth = BigDecimal.ZERO;
        for(MeterStatisticalBo m: list){
            sumMeter = sumMeter.add(m.getValue());
            if( (month.toString()).equals(m.getName())){
                nowMonth = m.getValue();
            }
        }
        log.info("当年电量总计：{}",sumMeter);

        if(nowMonth.compareTo(BigDecimal.ZERO) ==  0 || sumMeter.compareTo(BigDecimal.ZERO) ==  0){
            return BigDecimal.ZERO;
        }
        BigDecimal multiply = nowMonth.divide(sumMeter,2,BigDecimal.ROUND_HALF_UP);
        return multiply;
    }
}
