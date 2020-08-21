package cn.meiot.entity.water;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Record {
    /**
     *抄表记录id
     */
    private Long id;
    /**
     *客户编号
     */
    private String ccid;
    /**
     *水表编号
     */
    private String meterid;
    /**
     *设备编号
     */
    private String deviceid;
    /**
     *核对状态
     */
    private String checked;
    /**
     *核对者
     */
    private String checker;
    /**
     *抄表时间
     */
    private Long readtime;
    /**
     *抄表读数
     */
    private BigDecimal readcount;
}
