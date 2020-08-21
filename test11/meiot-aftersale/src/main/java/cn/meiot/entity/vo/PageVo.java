package cn.meiot.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/4/17 17:42
 * @Copyright: www.spacecg.cn
 */

@Data
public class PageVo<T> implements Serializable {

    private List<T> dataList;
    private Integer total;

    public PageVo(List<T> dataList, Integer total) {
        this.dataList = dataList;
        this.total = total;
    }
}
