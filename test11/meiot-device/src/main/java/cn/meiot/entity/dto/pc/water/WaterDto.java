package cn.meiot.entity.dto.pc.water;

import lombok.Data;

@Data
public class WaterDto {
    private Long mainUserId;
    private Long userId;
    private Integer projectId;
    private String name;
    private String id;
}
