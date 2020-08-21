package cn.meiot.feign.hystrix;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @Package cn.meiot.feign.hystrix
 * @Description:
 * @author: 武有
 * @date: 2019/9/27 13:49
 * @Copyright: www.spacecg.cn
 */
@Service
@Slf4j
public class UserFeignHystrix implements UserFeign {
    @Override
    public ImgConfigVo getImgConfig() {
        return null;
    }

    @Override
    public Long getMainUserIdByUserId(Long userId) {
        log.error("售后服务通过用户ID查询主账号ID 熔断 ID:{}",userId);
        log.info("售后服务通过用户ID查询主账号ID 熔断ID:{}",userId);
        throw new MyServiceException("售后服务通过用户ID查询主账号ID 熔断 ID:"+userId);
    }

    @Override
    public List<Integer> getRoleIdByUserId(Long userId) {
        log.error("售后服务通过用户ID查询角色IDs 熔断 ID:{}",userId);
        log.info("售后服务通过用户ID查询角色IDs 熔断ID:{}",userId);
        throw new MyServiceException("售后服务通过用户ID查询角色IDs 熔断 ID:"+userId);
    }

    @Override
    public String getConfigValueByKey(String cKey) {
        log.error("售后服务getConfigValueByKey 熔断 key:{}",cKey);
        log.info("售后服务getConfigValueByKey 熔断key:{}",cKey);
        return "";
    }
}
