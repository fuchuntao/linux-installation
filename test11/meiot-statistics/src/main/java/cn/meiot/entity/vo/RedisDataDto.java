package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: RedisDataDto
 * @Description: 上传电量 me(meter)，漏电电流 l(leakage)， 电压 v, 电流 ca， 温度 t（temp), 负载 po 存缓存
 * @author: 符纯涛
 * @date: 2020/7/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisDataDto implements Serializable {

    private static final long serialVersionUID = 4768029793975811735L;
    /**
     * 电量
     */
    private Long meter;


    /**
     * 电流
     */
    private Long current;

    /**
     * 漏电流
     */
    private Long leakage;


    /**
     * 温度
     */
    private Integer temp;


    /**
     * 负载
     */
    private Long power;


    /**
     * 电压
     */
    private Long voltage;


    /**
     * 时间戳（秒）（每次上传的时间戳）
     */
    private Long lastTime;


    /**
     * 开始时间戳（秒）
     */
    private Long startTime;



}
