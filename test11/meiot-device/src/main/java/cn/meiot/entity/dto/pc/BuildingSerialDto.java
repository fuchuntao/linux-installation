package cn.meiot.entity.dto.pc;

import lombok.Data;

@Data
public class BuildingSerialDto {
    private Long id;
    private String name;
    private Long parentId;
    private Integer serialTotal;
}
