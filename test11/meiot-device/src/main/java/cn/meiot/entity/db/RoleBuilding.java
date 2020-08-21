package cn.meiot.entity.db;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
public class RoleBuilding implements Serializable  {
	private static final long serialVersionUID = 1L;
	/**
    * id
    */
	@Id
	@GeneratedValue(generator = "JDBC")
    private Long id;

    /**
    * role_id
    */
    private Integer roleId;

    /**
    * building_id
    */
    private Long buildingId;
    /**
     * 
     */
    private Integer projectId;
}
