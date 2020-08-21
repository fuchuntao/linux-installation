package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: RedisMeterDto
 * @Description: 上传电量 me(meter)
 * @author: 符纯涛
 * @date: 2020/7/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisMeterDto implements Serializable {

    private static final long serialVersionUID = 4768029793975811735L;
    /**
     * 电量
     */
    private Long meter;

//
//    /**
//     * 电流
//     */
//    private Long current;
//
//    /**
//     * 漏电流
//     */
//    private Long leakage;
//
//
//    /**
//     * 温度
//     */
//    private Integer temp;
//
//
//    /**
//     * 负载
//     */
//    private Long power;


    /**
     * 时间戳（秒）
     */
    private Long time;


//    /**
//     * 开始时间戳（秒）
//     */
//    private Long startTime;



}
