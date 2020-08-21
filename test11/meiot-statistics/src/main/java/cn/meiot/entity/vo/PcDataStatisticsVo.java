package cn.meiot.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: PcDataStatisticsVo
 * @Description: 首页管理平台返回数据实体类
 * @author: 符纯涛
 * @date: 2019/9/20
 */
@Data
public class PcDataStatisticsVo {

    /**
     * 个人用户
     */
    private Integer userSum;


    /**
     * 企业用户
     */
    private Integer companyUserSum;


    /**
     * 设备数量
     */
    private Integer deviceSum;


    /**
     * 项目数量
     */
    private Integer projectSum;


    /**
     * 设备报修
     */
    private List<StatisticsVo> StatisticsVoList;


//    /**
//     * 设备近一年的增长
//     */
//    private List<PcDeviceStatisticsVo> deviceStatisticsVoList;

    /**
     * 用户近一年的增长
     */
    private List<PcUserStatistics> pcUserStatisticsList;

}
