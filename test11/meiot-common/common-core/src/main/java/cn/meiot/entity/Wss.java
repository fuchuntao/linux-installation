package cn.meiot.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/10/25 15:47
 * @Copyright: www.spacecg.cn
 */
@Data
public class Wss implements Serializable {
    private Integer type;
    private String group;
    private Object data;

    public Wss(Integer type, String group, Object data) {
        this.type = type;
        this.group = group;
        this.data = data;
    }
    public Wss(){}
}
