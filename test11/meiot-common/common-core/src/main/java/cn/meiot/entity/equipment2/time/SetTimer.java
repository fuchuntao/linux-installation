package cn.meiot.entity.equipment2.time;

import cn.meiot.entity.equipment.Timeswitch;
import cn.meiot.entity.equipment2.Sid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetTimer extends Sid implements Serializable {
    //1为时间段起止模式，2为星期重复模式，3为时间点开，4为时间点关，0为禁止
    private Integer mode;
    //定时开关组序号，0-4，最多5组
    private int num;
    //1开关  2 功率
    private Integer flag;
    //开始时间
    private Long start;
    //结束时间
    private Long end;
    //开
    private String on;
    //关
    private String off;
    //星期 从周末开始
    private List<Integer> cycle;
    //功率
    private Integer loadmax;

    public static SetTimer closeTime(Long sid,Integer flag,Integer num,Long time){
        SetTimer setTimer = new SetTimer();
        setTimer.setSid(sid);
        setTimer.setNum(num);
        setTimer.setStart(time);
        setTimer.setEnd(time);
        setTimer.setMode(0);
        setTimer.setLoadmax(0);
        return setTimer;
    }

    public SetTimer (Timeswitch timeswitch){
        this.mode = timeswitch.getMode();
        this.num = timeswitch.getNum();
        this.start = timeswitch.getStart();
        this.end = timeswitch.getEnd();
        this.on = timeswitch.getOn();
        this.off = timeswitch.getOff();
        this.cycle = timeswitch.getCycle();
        this.loadmax = timeswitch.getLoadmax();
        this.flag = timeswitch.getFlag();
    }


    public void setStart(Long start) {
        if(start > 9999999999L) {
            this.start = start/1000;
            return ;
        }
        this.start = start;
    }

    public void setEnd(Long end) {
        if(end > 9999999999L) {
            this.end = end/1000;
            return ;
        }
        this.end = end;
    }

    public void setOn(String on) {
        if(StringUtils.isBlank(on)) {
            this.on = null;
            return;
        }
        this.on = on.replaceAll(":", ".");
    }
    public void setOff(String off) {
        if(StringUtils.isBlank(off)) {
            this.off = null;
            return;
        }
        this.off = off.replaceAll(":", ".");
    }
}
