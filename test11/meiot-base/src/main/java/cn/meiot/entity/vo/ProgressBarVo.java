package cn.meiot.entity.vo;

import lombok.Data;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/11/30 10:52
 * @Copyright: www.spacecg.cn
 */
@Data
public class ProgressBarVo {
    //状态 0带升级 1升级中 2升级完成 3升级失败
    private Integer status;

    //设备序列号
    private String serialNumber;

    //当前长度
    private Long currentLength;

    //总长度
    private Long length;

    public ProgressBarVo(Integer status, String serialNumber, Long currentLength, Long length) {
        this.status = status;
        this.serialNumber = serialNumber;
        this.currentLength = currentLength;
        this.length = length;
    }
}
