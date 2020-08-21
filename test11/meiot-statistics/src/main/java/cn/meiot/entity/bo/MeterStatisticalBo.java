package cn.meiot.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterStatisticalBo {

    /**
     * 月份
     */
    private String name;
    /**
     *用电量
     */
    private BigDecimal value;

}
