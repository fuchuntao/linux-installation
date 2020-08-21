package cn.meiot.entity.dto.pc;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseBuildingDto implements Serializable {
	protected Long id;
	//名称
	protected String name;
	//父id
	protected Integer pId;
	//当前层级
	protected Integer level;
	
}
