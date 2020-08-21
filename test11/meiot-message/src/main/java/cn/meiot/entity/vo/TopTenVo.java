package cn.meiot.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/3/9 11:52
 * @Copyright: www.spacecg.cn
 */
@Data
public class TopTenVo implements Serializable {
    private String name;
    private Integer count;
    private String serNumber;

}
