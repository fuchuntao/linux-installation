package cn.meiot.entity.equipment2;

import cn.meiot.entity.equipment2.examination.CheckResult;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExaminationEntity extends BaseEntity2 implements Serializable {

    private List<CheckResult> checkresult = new ArrayList<>();
}
