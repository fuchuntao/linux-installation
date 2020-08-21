package cn.meiot.entity.vo;

import lombok.Data;

@Data
public class PageVo {

    /**
     * 当前页
     */
    private Integer current;

    /**
     * 每页展示多少行
     */
    private Integer pageSize;

    /**
     * 便宜起始量
     */
    private Integer offset;

    public PageVo(Integer current,Integer pageSize){
        this.current = current;
        this.pageSize = pageSize;
        if(null == current || current < 1 ){
            this.offset = 0;
        }else {
            this.offset = (current-1)*pageSize;
        }

    }
}
