package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterStatisticsDto {

    /**
     * 项目id
     */
    private Integer projectId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 名称
     */
    private String name;

    /**
     * 开始时间
     */
    private Long startTime;


    /**
     * 结束时间
     */
    private Long endTime;


    /**
     *水表编号
     */
    private String meterid;

    /**
     *分页
     */
    private Integer page = 1;
    private Integer pageSize = 10;

    /**
     * 1为导出表格
     */
    private Integer sign;

    /**
     * 0抄表表格， 1为用水记录
     */
    private Integer tab;

    /**
     * 年
     */
    private Integer year;


    /**
     * 月
     */
    private Integer month;

    /**
     * 日
     */
    private Integer day;

    /**
     * 水表编号列表
     */
    private List<String> waterList;

    /**
     *  type  年 月 （0 1）
     */
    private Integer type;


}
