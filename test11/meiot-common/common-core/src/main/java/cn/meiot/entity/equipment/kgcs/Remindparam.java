package cn.meiot.entity.equipment.kgcs;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Remindparam implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 漏电预警值
     */
    private BigDecimal rmleakage;
    /**
     * 过流预警值
     */
    private BigDecimal rmcurrent;
    /**
     *过温预警值
     */
    private BigDecimal rmtemp;
    /**
     * 过压预警值
     */
    private BigDecimal rmvoltagehigh;
    /**
     * 欠压预警值
     */
    private BigDecimal rmvoltagelow;
    /**
     * 过载预警值
     */
    private BigDecimal rmpower;
}
