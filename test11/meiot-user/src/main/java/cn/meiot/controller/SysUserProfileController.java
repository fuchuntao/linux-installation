package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.common.ErrorCode;
import cn.meiot.entity.SysUser;
import cn.meiot.entity.SysUserProfile;
import cn.meiot.entity.vo.HeadPhotoVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.ISysUserProfileService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-28
 */
@RestController
@Slf4j
public class SysUserProfileController extends BaseController{

    @Autowired
    private ISysUserProfileService sysUserProfileService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping(value = "/saveHeadPortrait")
    @Log(operateContent = "保存/修改头像",operateModule = "用户中心")
    public Result saveHeadPortrait(@RequestBody HeadPhotoVo headPhotoVo){
        log.info("接收到的头像路径：{}",headPhotoVo);
        if(StringUtils.isEmpty(headPhotoVo.getSavePath())){
            log.info("图像路径不能为空");
            return new Result().Faild(ErrorCodeUtil.HEAD_PATH_NOT_BE_NULL);
        }
        Long userId = getUserId();
        if(null == userId){
            return new Result().Faild(ErrorCode.USER_ID_NOT_NULL);
        }
        boolean count = sysUserProfileService.update(new UpdateWrapper<SysUserProfile>().set("head_portrait", headPhotoVo.getSavePath()).eq("user_id", userId));
        if(count){
            redisTemplate.opsForHash().put(RedisConstantUtil.USER_HEAD_PORTRAIT,userId.toString(),headPhotoVo.getSavePath());
            return Result.getDefaultTrue();
        }
        throw  new MyServiceException(ErrorCodeUtil.UPDATE_FAIL,ErrorCodeUtil.UPDATE_FAIL);
    }

}
