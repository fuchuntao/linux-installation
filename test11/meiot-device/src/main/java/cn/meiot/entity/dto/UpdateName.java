package cn.meiot.entity.dto;

import javax.validation.constraints.Size;

import cn.meiot.utils.VerifyUtil;
import lombok.Data;

/**
 * @author lingzhiying
 * @title: UpdateName.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月3日
 */
@Data
public class UpdateName {
	private Long switchType = 0L;
	private String switchSn;
	private String serialNumber;
	@Size(min=1, max=10,message ="超长")
	private String name;
	private Long userId;
	private Long id;
	private Integer parentIndex;
	public void setName(String name) {
		this.name = VerifyUtil.filterEmoji(name, "");
	};
}
