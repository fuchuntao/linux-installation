package cn.meiot.entity.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Scene {

    /**
     * 场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
     */
    private String scene_str;
}
