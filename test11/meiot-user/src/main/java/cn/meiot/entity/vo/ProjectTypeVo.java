package cn.meiot.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ProjectTypeVo {


    private Integer id;

    @NotEmpty(message = "名称不能为空")
    private String name;

    private List<Integer> permissions;
}
