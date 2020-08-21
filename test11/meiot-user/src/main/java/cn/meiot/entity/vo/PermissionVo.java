package cn.meiot.entity.vo;

import cn.meiot.utils.ErrorCodeUtil;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PermissionVo {

    /**
     * 类型id
     */
    @NotNull(message = ErrorCodeUtil.TYPE_ID_NOT_BE_NULL)
    private Integer id;

    /**
     * 项目id
     */
    private Integer projectId;

    /**
     * 权限ids
     */
    @NotNull(message = ErrorCodeUtil.PERMISSION_ID_NOT_BE_NULL)
    private List<Integer>  perimissions;
}
