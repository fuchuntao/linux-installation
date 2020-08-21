package cn.meiot.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/9/25 17:48
 * @Copyright: www.spacecg.cn
 */
@Data
public class StatisticsVo implements Serializable {
//
//    /**
//     * 主键
//     */
//    private String id;

    /**
     * 总数
     */
    private String sum;

    /**
     * 状态  1：保修 2：受理 3：维修
     */
    private Integer status;


    private String name;
}
