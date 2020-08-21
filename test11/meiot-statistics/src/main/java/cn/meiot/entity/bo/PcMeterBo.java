package cn.meiot.entity.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PcMeterBo {

    /**
     * 最大电量
     */
    private BigDecimal maxMeter;

    /**
     * 最小电量
     */
    private BigDecimal minMeter;

    /**
     * 平均电量
     */
    private BigDecimal avgMeter;

    /**
     *今日电量
     */
    private BigDecimal todyMeter;


    private BigDecimal sumMeter;




    //判断是否为0
    public static BigDecimal updateMeter(BigDecimal meter) {
        if(meter != null) {
            BigDecimal bigDecimal = meter.setScale(1, BigDecimal.ROUND_HALF_UP);
            return bigDecimal;
        }
        return null;
    }

    public void setMaxMeter(BigDecimal maxMeter) {
        this.maxMeter = updateMeter(maxMeter);
    }

    public void setMinMeter(BigDecimal minMeter) {
        this.minMeter = updateMeter(minMeter);
    }

    public void setAvgMeter(BigDecimal avgMeter) {
        this.avgMeter = updateMeter(avgMeter);
    }

    public void setTodyMeter(BigDecimal todyMeter) {
        this.todyMeter = updateMeter(todyMeter);
    }
    public void setSumMeter(BigDecimal sumMeter) {
        this.sumMeter = updateMeter(sumMeter);
    }
}
