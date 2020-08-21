package cn.meiot.entity.db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
public class TimeBuilding {
	/**
    * id
    */
	@Id
	@GeneratedValue(generator = "JDBC")
    private Long id;

    /**
    * building_id
    */
    private Long buildingId;

    /**
    * time_id
    */
    private Long timeId;
}
