package cn.meiot.entity.db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name ="time_switch")
public class TimerSwitch {
	 /**
    * id
    */
	@Id
	@GeneratedValue(generator = "JDBC")
    private Long id;

    /**
    * 开关号
    */
    private String switchSn;

    /**
    * time_id
    */
    private Long timeId;
}
