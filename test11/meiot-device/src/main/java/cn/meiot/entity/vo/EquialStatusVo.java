package cn.meiot.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EquialStatusVo {
    //设备名称
    private String serialName;
    //设备号
    private String serialNumber;
    //组织架构id
    private Long buildingId;
    //地址
    private String address;
    //线路状态  0正常  1报警  2预警
    private Integer switchStatus = 0;
    //联网状态
    private Integer isOnline = 1;
    //打开数量
    private Integer openNum = 0;
    //关闭数量
    private Integer closeNum = 0;
}
