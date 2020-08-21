package cn.meiot.utlis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;

import cn.meiot.entity.dto.pc.BuildingRecursionDto;
import cn.meiot.entity.dto.pc.examination.ExaminationBuildingDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuildingUtlis {
	// 根据角色
	public static void removeId(List<BuildingRecursionDto> listData, List<Long> listBuilding) {
		if (CollectionUtils.isEmpty(listData)) {
			return;
		}
		for (int i = listData.size()-1; i >= 0; i--) {
			BuildingRecursionDto buildingRecursionDto = listData.get(i);
			if (!listBuilding.contains(buildingRecursionDto.getId())) {
				listData.remove(i);
				continue;
			}
			removeId(buildingRecursionDto.getListBuildingRecursionDto(), listBuilding);
		}
	}

	public static void removeId2(List<ExaminationBuildingDto> listData, List<Long> listBuilding) {
		if (CollectionUtils.isEmpty(listData)) {
			return;
		}
		for (int i = listData.size() -1; i >= 0; i--) {
			ExaminationBuildingDto examinationBuildingDto = listData.get(i);
			if (!listBuilding.contains(examinationBuildingDto.getId())) {
				listData.remove(i);
				continue;
			}
			removeId2(examinationBuildingDto.getListData(), listBuilding);
		}
	}

	//public static void

}
