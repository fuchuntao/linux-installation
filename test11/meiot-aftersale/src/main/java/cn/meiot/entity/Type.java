package cn.meiot.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Package cn.meiot.entity
 * @Description:
 * @author: 武有
 * @date: 2019/9/17 15:07
 * @Copyright: www.spacecg.cn
 */
@Data
public class Type {
    private Long id;
    private String name;

    @JSONField(serialize = false)
    private String description;
}
