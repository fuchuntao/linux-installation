package cn.meiot.entity.vo;

import lombok.Data;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/12/12 10:05
 * @Copyright: www.spacecg.cn
 */

@Data
public class WhiteVo {
    /**
     * 设备号
     */
    private String serialNumber;
    /**
     * 版本
     */
    private String version;
    /**
     * 描述
     */
    private String description;
}
