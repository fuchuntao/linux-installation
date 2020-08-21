package cn.meiot.entity.dto.pc;

import lombok.Data;

@Data
public class BuildingRespDto {
	//主用户id
	private long userId;
	private Long pId = 0L;
	private Integer projectId;
	private Long roleId;
	private Integer type;
	//当前登录用户id
	private Long currentUserId;
}
