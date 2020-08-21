package cn.meiot.entity.water;

import lombok.Data;

@Data
public class Imeter {
    /**
     * id
     */
    private Long id;
    /**
     * 水表编号
     */
    private String meterid;
    /**
     * 单位系数
     */
    private Double unit;
    /**
     *水表口径
     */
    private String caliber;
    /**
     *水表表底
     */
    private Double basecount;
    /**
     *水表型号
     */
    private String product;
    /**
     * 智能水表当前绑定设备信息
     */
    private DeviceDto device;
}
