package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRoleVo {

    /**
     * 账户id
     */
    private Long userId;

    /**
     * 角色id
     */
    private List<Integer> roles;

    /**
     * 类型   1：平台   2：企业
     */
    private Integer type;
}
