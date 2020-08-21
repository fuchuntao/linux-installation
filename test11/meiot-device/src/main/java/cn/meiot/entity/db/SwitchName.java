package cn.meiot.entity.db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: SwitchName.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月2日
 */
@Data
public class SwitchName {
	@Id
	@GeneratedValue(generator = "JDBC")
	private Long id;
	private String name;
	private Long userId;
	private String switchSn;
	private Long switchType;
}
