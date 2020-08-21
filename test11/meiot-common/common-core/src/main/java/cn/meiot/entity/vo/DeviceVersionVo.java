package cn.meiot.entity.vo;

import lombok.Data;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/11/27 16:52
 * @Copyright: www.spacecg.cn
 */
@Data
public class DeviceVersionVo {
    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 设备版本
     */
    private String version;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态 0 带升级 1升级中 2升级成功 3升级失败
     */
    private Integer status;


    /**
     * 总长度
     */
    private Long length;


    /**
     * 当前长度
     */
    private Long currentLength;
}
