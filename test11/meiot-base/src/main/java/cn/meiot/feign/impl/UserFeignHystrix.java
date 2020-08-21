package cn.meiot.feign.impl;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserFeignHystrix implements UserFeign {
    @Override
    public Result findAllUserId(Integer type) {
        Result result = Result.getDefaultFalse();
        result.setMsg("超时");
        return result;
    }

    @Override
    public ImgConfigVo getImgConfig() {
        log.info("public ImgConfigVo getImgConfig() Hystrix");
        return null;
    }

    @Override
    public Long getMainUserIdByUserId(Long userId) {
        return -1L;
    }

    @Override
    public List<Long> getAllUserIdByMainUser(Long mainUserId) {
        return null;
    }

    @Override
    public List<Long> getUserIdsByRoleId(Map map) {
        log.info("通过角色ids获取用户List网络请求失败");
        return null;
    }

    @Override
    public String queryProjectNameById(Integer projectId) {
        log.info("通过项目id查询项目名称");
        return null;
    }


}
