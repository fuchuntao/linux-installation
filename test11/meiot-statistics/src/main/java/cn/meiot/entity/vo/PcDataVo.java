package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PcDataVo {

    private Integer type;

    private Integer year;

    private Integer month;

    private Integer oldMonth;

    private Integer oldYear;

    private Integer projectId;



    private BigDecimal meter;

}
