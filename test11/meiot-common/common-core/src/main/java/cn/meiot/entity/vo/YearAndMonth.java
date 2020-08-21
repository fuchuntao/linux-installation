package cn.meiot.entity.vo;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YearAndMonth {

    private Integer year;

    private Integer month;

    private Integer day;
}
