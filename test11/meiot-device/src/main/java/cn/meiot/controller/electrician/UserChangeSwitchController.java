package cn.meiot.controller.electrician;

import cn.meiot.controller.BaseController;
import cn.meiot.entity.db.UserChangeSwitch;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.electrician.UserChangeSwitchService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("userChangeSwitch")
public class UserChangeSwitchController extends BaseController {

    @Autowired
    private UserChangeSwitchService userChangeSwitchService;

    @GetMapping("queryLog")
    public Result queryLog(Integer page,Integer pageSize){
        Long userId = getUserId();
        PageInfo pageInfo = userChangeSwitchService.queryLog(userId,page,pageSize);
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(pageInfo);
        return defaultTrue;
    }

    @PostMapping("changeSwitch")
    public Result changeSwitch(UserChangeSwitch userChangeSwitch){
        Long userId = getUserId();
        userChangeSwitch.setUserId(userId);
        userChangeSwitchService.changeSwitch(userChangeSwitch);
        return Result.getDefaultTrue();
    }

}
