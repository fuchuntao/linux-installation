package cn.meiot.entity.vo;

import cn.meiot.entity.bo.UserInfoBo;
import lombok.Data;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/9/23 18:02
 * @Copyright: www.spacecg.cn
 */
@Data
public class AuthUserVo {
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
    private Integer projectId;

}
