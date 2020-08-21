package cn.meiot.entity.vo;

import lombok.Data;

/**
 * @Package cn.meiot.entity.dto
 * @Description:
 * @author: 武有
 * @date: 2019/9/25 16:52
 * @Copyright: www.spacecg.cn
 */
@Data
public class FaultMessageVo {
    private String id;
    /**
     * 故障时间
     */
    private String faultTime;

    /**
     * 故障类型ID
     */

    private String faultTypeId;

    /**
     * 故障类型名称
     */
    private String faultTypeName;

    /**
     * 故障状态
     */
    private String faultStatus;

    /**
     * 开关名称
     */
    private String switchName;

    /**
     * 配电箱名称 Distribution box
     */
    private String distributionBoxName;

    /**
     * 地址
     */
    private String address;

    /**
     * 设备号
     */
    private String serialNumber;

    /**
     * 开关号
     */
    private String switchSn;

    /**
     * 故障值
     */
    private String faultValue;

    /**
     *  新增 电器
     */
    private String householdAppliancesName;
}
