package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalSerialVo {
    //设备号
    private String serial;
    //主账户
    private Long masterId;
    //主开关
    private Long masterSn;
}
