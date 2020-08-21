package cn.meiot.entity.dto.pc.water;

import lombok.Data;

@Data
public class WaterConditionDto {

    /**
     * 项目id
     */
    private Integer projectId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 名称
     */
    private String name;
    /**
     *分页
     */
    private Integer page = 1;
    private Integer pageSize = 10;

}
