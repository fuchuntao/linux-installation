package cn.meiot.entity.dto.pc.examination;

import java.util.List;

import lombok.Data;

@Data
public class BatchExamination {
	private Integer status;
	private String examinationTime;
	private List<String> serialNumber;
}
