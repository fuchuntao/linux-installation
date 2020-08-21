package cn.meiot.controller.wx;

import cn.meiot.controller.BaseController;
import cn.meiot.entity.UserOpenid;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.UserWXBindVo;
import cn.meiot.service.IUserOpenidService;
import cn.meiot.service.WxService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.RedisUtil;
import cn.meiot.utils.UserAgentUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/wx")
@Slf4j
public class WxController extends BaseController {

    private WxService wxService;

    private IUserOpenidService userOpenidService;

    private RedisUtil redisUtil;

    WxController(WxService wxService,IUserOpenidService userOpenidService,RedisUtil redisUtil){
        this.wxService = wxService;
        this.userOpenidService = userOpenidService;
        this.redisUtil = redisUtil;
    }

    /**
     * 获取公众号二维码
     * @return
     */
    @GetMapping(value = "/ticket")
    public Result getTicket(){

        return wxService.getTicket(getUserId());
    }

    /**
     * 解除微信消息推送和微信登录
     * @return
     */
    @RequestMapping(value = "/unBindWx",method = RequestMethod.POST)
    public Result unBindWx(){


        return wxService.unBindWx(getUserId());

    }

    /**
     * 微信二维码登录
     * @param code
     * @return
     */
    @RequestMapping(value = "/nofilter/qrCodeLogin/{code}",method = RequestMethod.PUT)
    public Result qrCodeLogin(@PathVariable("code") String code, HttpServletRequest request){
        if(StringUtils.isEmpty(code)){
            return new Result().Faild(ErrorCodeUtil.CODE_NOT_BE_NULL);
        }
        log.info("登录code：{}",code);
        String device = UserAgentUtils.getDeviceName(request.getHeader("User-Agent"));
        return wxService.qrCodeLogin(code,device);
    }

    /**
     * 绑定
     * @param userWXBindVo
     * @return
     */
    @RequestMapping(value = "/nofilter/bindingWx",method = RequestMethod.POST)
    public  Result bindingWx(@RequestBody @Valid UserWXBindVo userWXBindVo, HttpServletRequest request){
        String device = UserAgentUtils.getDeviceName(request.getHeader("User-Agent"));
        return wxService.bindingWx(userWXBindVo,device);

    }



}
