package cn.meiot.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName: WaterStatisticsVo
 * @Description: 抄表信息类
 * @author: 符纯涛
 * @date: 2020/2/26
 */
@Data
public class WaterStatisticsVo {


    private Long recordId;

    /**
     * 地址
     */
    private String address;

    /**
     *抄表记录id
     */
    private Long id;
    /**
     *客户编号
     */
    private String ccid;
    /**
     *水表编号
     */
    private String meterid;
    /**
     *设备编号
     */
    private String deviceid;

    /**
     *抄表时间
     */
    private Long readtime;
    /**
     *抄表读数
     */
    private BigDecimal readcount;

    /**
     * 单位
     */
    private Double unit;

    /**
     *核对状态
     */
    private String checked;
    /**
     *核对者
     */
    private String checker;

}
