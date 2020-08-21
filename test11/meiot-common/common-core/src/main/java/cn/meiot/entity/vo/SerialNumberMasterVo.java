package cn.meiot.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SerialNumberMasterVo  implements Serializable {

    /**
     * 设别号
     */
    private String serialNumber;


    /**
     * 主开关编号
     */
    private Integer masterIndex;
    
    /**
     * 主开关编号
     */
    private Long masterSn;
}
