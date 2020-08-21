package cn.meiot.entity.db;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
public class EquipmentApi implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
    * 设备号
    */
    private String serialNumber;

    /**
    * appid
    */
    private Long applicationId;

    /**
    * update_time
    */
    private Date updateTime;

    /**
    * create_time
    */
    private Date createTime;

    /**
    * 0正常 1删除
    */
    private Integer deleted;

    public EquipmentApi() {
    }

}