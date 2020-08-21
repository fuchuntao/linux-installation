package cn.meiot.entity.equipment2.upwarn;

import cn.meiot.entity.equipment2.Sid;
import lombok.Data;

import java.io.Serializable;
@Data
public class WarnInfo extends Sid implements Serializable {
    /**
     * 警报类型：1、漏电保护
     *  2、过温提醒
     * 3、过温保护
     * 4、过载保护
     * 5、过流保护
     * 6、过压保护
     * 7、欠压保护
     * 8、手动分闸
     * 9、手动合闸
     * 10、漏电提醒
     * 11、过压提醒
     * 12、欠压提醒
     * 13、过流提醒
     * 14、过载提醒
     */
    private Integer eventtype;
    /**
     * 报警值
     */
    private Long eventvalue;
    /**
     *开关状态（0：正常合闸,1:手动合闸2:正常分闸，3：手动分闸，4、过压分闸，5、欠压分闸、
     * 6过流分闸，7、过功率自动分闸，8过温分闸、9、漏电产生自动分闸、10、手动漏电测试分闸
     */
    private Integer switchstatus;

    private Integer faultStatus = 0;

    public Integer getFaultStatus() {
        if(switchstatus == null){
            return 0;
        }
        if(switchstatus.equals(3))
            return 8;
        if(switchstatus <3)
            return 0;
        return switchstatus - 3;
    }
}
