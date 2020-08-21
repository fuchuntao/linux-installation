package cn.meiot.entity.vo;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/4/20 11:34
 * @Copyright: www.spacecg.cn
 */
public class SendStatusVo {

    private Long alarmId;
    private Integer type;

    public SendStatusVo(Long alarmId, Integer type) {
        this.alarmId = alarmId;
        this.type = type;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
