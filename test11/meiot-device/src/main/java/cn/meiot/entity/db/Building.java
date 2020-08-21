package cn.meiot.entity.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Building implements Serializable {
	/**
    * id
    */
	@Id
	@GeneratedValue(generator = "JDBC")
    private Long id;

    /**
    * 名称
    */
    private String name;

    /**
    * 编号
    */
    private String serial;

    /**
    * 一键开关状态:1=关，2=开
    */
    private Integer switchStatus;

    /**
    * 层级数
    */
    private Integer level;

    /**
    * 排序权重（值越大越靠前）
    */
    private Integer weigh;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 更新时间
    */
    private Date updateTime;

    /**
    * 上级建筑
    */
    private Long parentId;

    /**
    * 所属企业主账户id
    */
    private Long userId;
    
    /**
     * 项目id
     */
    private Integer projectId;

    /**
     * 设备数量
     */
    private Integer serialTotal;
}
