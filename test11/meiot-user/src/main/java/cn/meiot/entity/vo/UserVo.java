package cn.meiot.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserVo {

    /**
     * 昵称
     */
    private String nikName;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * id列表
     */
    private List<Long> ids;
}
