package cn.meiot.entity.db;

import java.io.Serializable;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
public class SwitchType implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
    * project_id
    */
    private Integer projectId;

    /**
    * 开关名
    */
    private String name;
    /**
     * 是否默认
     */
    private Integer isDefault;
}
