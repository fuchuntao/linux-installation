package cn.meiot.entity.equipment.kgcs;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 报警参数
 */
@Data
public class Maxparam implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 漏电报警值
     */
    private BigDecimal maxleakage;
    /**
     *过流报警值
     */
    private BigDecimal maxcurrent;
    /**
     *过压报警值
     */
    private BigDecimal maxvoltagehigh;
    /**
     *欠压报警值
     */
    private BigDecimal maxvoltagelow;

}
