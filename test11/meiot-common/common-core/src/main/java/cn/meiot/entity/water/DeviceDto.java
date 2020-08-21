package cn.meiot.entity.water;

import lombok.Data;

@Data
public class DeviceDto {
    /**
     * 编号
     */
    private String deviceid;
    /**
     * 抄表模式
     */
    private String sendmode;
    /**
     * 设备电量
     */
    private Integer battery;
    /**
     * 设备是否启动状态 [已启动,未启动]
     */
    private String openstatus;
    /**
     * [0,1,2,3] 设备运行状态
     */
    private Integer status;
    /**
     * 设备对应的SIM号
     */
    private String SIM;
}
