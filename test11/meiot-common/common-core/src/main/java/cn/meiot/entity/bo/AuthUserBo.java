package cn.meiot.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserBo implements Serializable {

    /**
     * 凭证
     */
    private String token;
    /**
     * 用户
     */
    private UserInfoBo user;

    /**
     * 项目id
     */
    private List<Integer> projectIds;

    /**
     * 设备型号
     */

    private String device;

    /**
     *默认id
     */
    private Integer defaultProjectId;

}
