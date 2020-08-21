package cn.meiot.entity.water;

import lombok.Data;

@Data
public class CustomerImeter {
    /**
     * 水表编号
     */
    private String meterid;
    /**
     * 水表开始使用时间
     */
    private Long starttime;
    /**
     * 结束时间
     */
    private Long endtime;
}
