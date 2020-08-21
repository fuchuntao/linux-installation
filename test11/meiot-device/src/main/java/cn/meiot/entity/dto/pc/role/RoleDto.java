package cn.meiot.entity.dto.pc.role;

import java.util.List;

import lombok.Data;

@Data
public class RoleDto {
	private Integer roleId;
	private Integer projectId;
	/**
	 * 选中状态
	 */
	private List<Long> listBuilding;
	/**
	 * 半选中状态
	 */
	private List<Long> listSelection;
}
