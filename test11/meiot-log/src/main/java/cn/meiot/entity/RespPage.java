package cn.meiot.entity;

import lombok.Data;

/**
 * @author fengshaoyu
 * @title: RespPage
 * @projectName spacepm
 * @description: 分页返回
 * @date 2019-01-08 14:19
 */
@Data
public class RespPage {
    /**
     * 总数
     */
    private Long total;
    /**
     * 返回内容
     */
    private Object data;
}
