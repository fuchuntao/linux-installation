package cn.meiot.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/10/16 10:20
 * @Copyright: www.spacecg.cn
 */
@Data
public class MqStatusVo {
    private Integer[] event;
    private BigDecimal power;
    private BigDecimal loadmax;
    private BigDecimal temp;
    private BigDecimal tempmax;
    private BigDecimal meterd;
    private BigDecimal meterm;
    private Integer switchs;
    private BigDecimal auto;
    private BigDecimal leakage;
    private  BigDecimal[] current;
    private BigDecimal[] voltage;
}
