package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: WaterStatisticsVo
 * @Description: redis存储对象
 * @author: 符纯涛
 * @date: 2020/2/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisDateVo implements Serializable {


    private static final long serialVersionUID = 6651191929426429217L;
    private Integer type;

    /**
     * 地址
     */
    private Long updateTime;


}
