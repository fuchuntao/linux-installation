package cn.meiot.feign.hystrix;

import cn.meiot.entity.bo.UserNumBo;
import cn.meiot.feign.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserFeignHystrix  implements UserFeign {


    @Override
    public Integer queryProjectTotal() {
        log.info("获取项目总数量超时，获取失败！！！！！！！！！");
        return 0;
    }

    /**
     *
     * @Title: getProjectDateByProjectId
     * @Description: 通过项目id查询项目的开始时间
     * @param projectId
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Long getProjectDateByProjectId(Integer projectId) {
        log.info("通过项目id查询项目的开始时间，获取失败！！！！！");
        return null;
    }

    @Override
    public UserNumBo getUserNum() {
        log.info("获取用户数量超时！！！！！！！！");
        UserNumBo userNumBo = new UserNumBo();
        userNumBo.setUserSum(0);
        userNumBo.setCompanyUserSum(0);
        return userNumBo;
    }

    @Override
    public Long getMainUserId(Long userId) {

        log.info("获取用户主账号失败，超时，用户id：{}",userId);
        return null;
    }
}
