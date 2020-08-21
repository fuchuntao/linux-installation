package cn.meiot.controller.app;

import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.UserVo;
import cn.meiot.service.ISysUserService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.VerifyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户中心
 */
@RestController
@RequestMapping("/app/user")
public class UserController extends BaseController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 修改昵称
     * @param userVo
     * @return
     */
    @PostMapping(value = "/editNikname")
    @Log(operateContent = "修改/保存昵称",operateModule = "用户中心")
    public Result editNikname(@RequestBody UserVo userVo){
        if(null == userVo){
            Result result = Result.getDefaultFalse();
            //昵称不可为空
            result.setMsg(ErrorCodeUtil.PARMA_NOT_BE_NULL);
            return result;
        }
        if(StringUtils.isEmpty(userVo.getNikName())){
            Result result = Result.getDefaultFalse();
            //昵称不可为空
            result.setMsg(ErrorCodeUtil.NIKNAME_NOT_BE_NULL);
            return result;
        }
        String nikName = VerifyUtil.filterEmoji(userVo.getNikName(), ConstantsUtil.REPL_EMOJI_STR);
        userVo.setNikName(nikName);
        userVo.setUserId(getUserId());
        return sysUserService.editNikname(userVo);

    }


}
