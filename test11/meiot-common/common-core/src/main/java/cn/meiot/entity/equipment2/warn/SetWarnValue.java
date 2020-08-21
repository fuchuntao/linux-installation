package cn.meiot.entity.equipment2.warn;

import cn.meiot.entity.bo.Crcuit;
import cn.meiot.entity.equipment2.Sid;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SetWarnValue extends Sid implements Serializable {
    private static final long serialVersionUID = 5899429886748093540L;

    //最大电压
    private Long maxvoltage;
    //最小电压
    private Long minvoltage;
    //最大的电流值
    private Long maxcurrent;
    //最小的电流值
    private Long mincurrent;
    //预警最大电压值
    private Long warnmaxvoltage;
    //预警最小电压值
    private Long warnminvoltage;
    //预警最大的电流值
    private Long warnmaxcurrent;
    //预警最小的电流值
    private Long warnmincurrent;
    //最大的漏电电流
    private Long maxleakage;
    //预警最大的漏电电流
    private Long warnleakage;
    //最大功率
    private Long maxload;
    //最大功率
    private Long warnmaxload;
    //最大的温度值
    private Long maxtemp;
    //最大的温度值
    private Long warnmaxtemp;

    public SetWarnValue(Crcuit crcuit,Long sid){
        //电压
        this.maxvoltage = crcuit.getVoltageWA().longValue();
        this.warnmaxvoltage = crcuit.getVoltage().longValue();
        //电流
        this.maxcurrent = crcuit.getCurrentWA().longValue();
        this.warnmaxcurrent = crcuit.getCurrent().longValue();
        //漏电
        this.maxleakage = crcuit.getLeakageWA().longValue();
        this.warnleakage = crcuit.getLeakage().longValue();
        //温度
        this.maxtemp = crcuit.getTempWA().longValue();
        this.warnmaxtemp = crcuit.getTemp().longValue();
        //欠压
        this.minvoltage =crcuit.getUnderVoltageWA().longValue();
        this.warnminvoltage = crcuit.getUnderVoltage().longValue();
        //功率
        this.maxload = crcuit.getPowerWA().longValue();
        this.warnmaxload = crcuit.getPower().longValue();

        this.sid = sid;
    }

    public SetWarnValue(Long maxload,Long sid){
        this.sid = sid ;
        this.maxload = maxload;
    }
}
