package cn.meiot.entity.db;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: Equipment.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月16日
 */
@Data
public class Equipment {
	//最大功率
	private Integer loadmax;
	//额定电压
	private BigDecimal voltage;
	//自检状态
	private Integer examinationStatus;
	//自检时间
	private String examinationTime;
	 //设备序号
	@Id
	@GeneratedValue(generator = "JDBC")
    private  String serialNumber;
   
	 //状态:0-未激活 1-已激活 2-停用
    private  Integer equipmentStatus;
   
	 //型号
    private  String model;
   
	 //信息来源: 1-设备上报 2-人工添加
    private  Integer source;
   
	 //创建时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private  Date createTime;
   
	 //更新时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private  Date updateTime;
   
	 //激活时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private  Date activationTime;
   
	 //开关数量
    private  Integer switchCount;
   
	 //固件版本号
    private  String firmwareVersion;
   
	 //组织架构
    private  Long buildingId;
    
    //版本号
    private String version;

    //所属公司
	private Integer company;

	//协议版本号
	private Integer agreementVersion;
}
