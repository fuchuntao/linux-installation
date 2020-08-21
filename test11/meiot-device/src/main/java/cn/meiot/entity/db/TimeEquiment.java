package cn.meiot.entity.db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
public class TimeEquiment {
	 /**
    * id
    */
	@Id
	@GeneratedValue(generator = "JDBC")
    private Long id;

    /**
    * time_id
    */
    private Long timeId;

    /**
    * 设备user号
    */
    private Long equimentId;
}
