package cn.meiot.entity.equipment2.upstatus;

import cn.meiot.entity.equipment2.Sid;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Sl extends Sid implements Serializable {
    private static final long serialVersionUID = 1L;
    //开关状态 0：正常合闸,1:手动合闸2:正常分闸，3：手动分闸，4、过压分闸，5、欠压分闸、6过流分闸，7、过功率自动分闸，8过温分闸、9、漏电产生自动分闸、10、手动漏电测试分闸
    private Integer s;
    //电压
    private Long v;
    //a相电流
    private Long ca;
    //b相电流
    private Long cb;
    //c相电流
    private Long cc;
    //漏电电流
    private Long l;
    //板载温度
    private Integer t;
    //A接线端温度
    private Integer ta;
    //B接线端温度
    private Integer tb;
    //c接线端温度
    private Integer tc;
    //当前功率
    private Long po;
    //当前电量
    private Long me;
    //Map
    private Map map = new HashMap<>();
    //开关状态  0、无 1、过压分闸，2、欠压分闸、3过流分闸，4、过功率自动分闸，5过温分闸、6、漏电产生自动分闸、7、手动漏电测试分闸  8、手动分闸
    private Integer faultStatus = 0;

    public Map getMap() {
        if(s.equals(0) || s.equals(1)){
            map.put("switch",1);
        }else{
            map.put("switch",0);
        }

        return map;
    }

    public Integer getFaultStatus() {
        if(s.equals(3)){
            faultStatus = 8;
        }else if(s <3){
            faultStatus = 0;
        }else {
            faultStatus = s - 3;
        }
        return faultStatus;
    }

    public void setS(Integer s) {
        this.s = s;
    }

    public void setV(Long v) {
        if(v != null){
            List<Long> list = new ArrayList<>();
            list.add(v);
            map.put("voltage",list);
        }
        this.v = v;
    }

    public void setCa(Long ca) {
        this.ca = ca;
    }

    public void setCb(Long cb) {
        this.cb = cb;
    }

    public void setCc(Long cc) {
        this.cc = cc;
    }

    public void setL(Long l) {
        if(l != null){
            map.put("leakage",l);
        }
        this.l = l;
    }

    public void setT(Integer t) {
        if(t != null){
            map.put("temp",t);
        }
        this.t = t;
    }

    public void setTa(Integer ta) {
        this.ta = ta;
    }

    public void setTb(Integer tb) {
        this.tb = tb;
    }

    public void setTc(Integer tc) {
        this.tc = tc;
    }

    public void setPo(Long po) {
        if(po != null){
            map.put("power",po);
        }
        this.po = po;
    }

    public void setMe(Long me) {
        if(me != null){
            map.put("meter",me);
        }
        this.me = me;
    }
}
