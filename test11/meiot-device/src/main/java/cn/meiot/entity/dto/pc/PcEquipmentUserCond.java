package cn.meiot.entity.dto.pc;

import java.util.List;

import lombok.Data;

@Data
public class PcEquipmentUserCond {
	private String name;
	private String serialNumber;
	private String phone;
	private Integer page = 1;
	private Integer pageSize = 10;
	//从第几个开始
	private Integer pageNumber;
	//组织架构集合
	private List<Long> buildingList;
	private Long userId;
	private Integer projectId;
	private String switchSn;
	private Integer status;
	private Long startTime;
	private Long endTime;
	private Long buildingId;
	private String table;
	private Long id;
	private Integer flag = 1;
	//角色id
	private List<Integer> listRole;

	public Integer getPageNumber() {
		return (this.page-1)*this.pageSize;
	}
}
