package cn.meiot.entity.vo;

import lombok.Data;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/10/21 11:00
 * @Copyright: www.spacecg.cn
 */
@Data
public class StatisticsEventTimeVo {
    private String time;
    private Integer count;
    public StatisticsEventTimeVo(){}

    public StatisticsEventTimeVo(String time, Integer count) {
        this.time = time;
        this.count = count;
    }
}
