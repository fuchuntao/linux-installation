package cn.meiot.entity.equipment2.control;

import cn.meiot.entity.equipment2.Sid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Crlpower extends Sid implements Serializable {
    //0 关 1开 2置空  3漏电自检
    private Integer otype;

    public Crlpower (Long sid ,Integer otype){
        this.sid = sid;
        this.otype = otype;
    }

    public static Crlpower leakageTest(Long sid){
        return new Crlpower(sid,2);
    }
}
