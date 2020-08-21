package cn.meiot.feign.hystrix;

import cn.meiot.aop.Log;
import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.exception.MyServiceException;
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

    @Override
    public List<Long> getUserByType(Integer type) {
        return null;
    }

    @Override
    public String getAccessToken() {
        log.info("获取微信AccessToken失败 ");
        return null;
    }

    @Override
    public String getOpenid(Long userId) {
        log.info("获取微信Openid失败 ");
        return null;
    }

    @Override
    public Integer getUserTypeByUserId(Long userId) {
        log.info("获取用户类型失败");
        return null;
    }

    @Override
    public String getConfigValueByKey(String cKey) {
        log.info("网络错误，");
        return null;
    }

    @Override
    public boolean checkPermission(Long userId, String permission, Integer projectId) {
        log.info("网络错误，checkPermission   用户服务");
        return false;
    }

//    @Override
//    public String getNiknameByUserId(Long userId) {
//
//    }


    @Override
    public String getNiknameByUserId(Long userId) {
        throw new  MyServiceException("用户服务挂机","用户服务挂机");
    }

    @Override
    public List<String> getRoleNameByUserId(Long userId) {
        throw new  MyServiceException("用户服务挂机","用户服务挂机");
    }
}
