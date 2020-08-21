package cn.meiot.entity.db;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
public class WaterUser {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * user_id
     */
    private Long userId;

    /**
     * project_id
     */
    private Integer projectId;

    /**
     * 名称
     */
    private String name;

    /**
     * 水表id
     */
    private String meterId;

    /**
     * 审核状态:0-待审核 1-正常 2-禁用
     */
    private Integer status;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * 组织架构id
     */
    private Long buildingId;
}
