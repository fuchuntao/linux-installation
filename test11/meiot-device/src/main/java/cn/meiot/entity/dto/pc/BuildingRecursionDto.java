package cn.meiot.entity.dto.pc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.meiot.entity.db.Building;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingRecursionDto extends BaseBuildingDto implements Serializable {

	//状态
	private BuildingEquipment buildingEquipment = new BuildingEquipment();
	//下层
	private List<BuildingRecursionDto> listBuildingRecursionDto = new ArrayList<>();

	public BuildingRecursionDto(Building building){
		this.id = building.getId();
		this.level = building.getLevel();
		this.name = building.getName();
		this.pId = building.getParentId().intValue();
	}
}
