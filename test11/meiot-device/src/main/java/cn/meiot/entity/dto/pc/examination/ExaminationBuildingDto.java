package cn.meiot.entity.dto.pc.examination;

import java.util.ArrayList;
import java.util.List;

import cn.meiot.entity.db.Building;
import cn.meiot.entity.dto.pc.BaseBuildingDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationBuildingDto extends BaseBuildingDto{
	private Integer status = 0;
	private Integer serialTotal;
	private List<SerialDto> listSerial;
	private List<ExaminationBuildingDto> listData = new ArrayList<>();
	public ExaminationBuildingDto (Building building){
		this.id = building.getId();
		this.level = building.getLevel();
		this.name = building.getName();
		this.pId = building.getParentId().intValue();
		this.serialTotal = building.getSerialTotal();
	}
}
