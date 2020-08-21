package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysRolePermissionVo implements Serializable {

    private Integer type;

    private Integer projectId;

    private Long userId;

    private Integer adminId;

    private Integer isAdmin;
}
