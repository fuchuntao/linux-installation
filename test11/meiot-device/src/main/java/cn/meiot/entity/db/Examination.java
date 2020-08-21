package cn.meiot.entity.db;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: Examination.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月31日
 */
@Data
public class Examination {
	private static final long serialVersionUID = 1L;

    /**
    * id
    */
	@Id
	@GeneratedValue(generator = "JDBC")
    private Long id;

    /**
    * 设备系列号
    */
    private String serialNumber;

    /**
    * 开关序列号
    */
    private String switchSn;

    /**
    * 设备开关序号
    */
    private Integer switchIndex;

    /**
    * 漏电状态:0=不漏电:，1=漏电
    */
    private Integer swicthStatus;

    /**
    * 漏电流(单位ma)
    */
    private Integer leakage;

    /**
    * 各相电流(单位ma)
    */
    private Float current;

    /**
    * 各相电压(单位ma)
    */
    private Float voltage;

    /**
    * 漏电自检查询:0=查询中，1=查询成功，2=查询失败
    */
    private Integer type;

    /**
    * 自检时间
    */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
