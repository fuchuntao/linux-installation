package cn.meiot.entity.db;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: EquipmentUser.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月16日
 */
@Data
public class EquipmentUser {
	//项目id
	private Integer projectId;
	//创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	//子用户名称
	private String userName;
	//开关状态
	private Integer isSwitch;
	@Id
	@GeneratedValue(generator = "JDBC")
	private Long id;
	 //设备序列号
    private  String serialNumber;
   
	 //用户id
    private  Long userId;
   
	 //备注名称
    private  String name;
   
	 //状态:0-待审核 1-正常 2-禁用
    private  Integer userStatus;
   
	 //是否主账户: 0-否 1-是
    private  Integer isPrimary;
   
	 //默认设备: 0-否 1-是
    private  Integer isDefault;
    
    /**
     * 组织架构
     */
    @Transient
    private Long building;
}
