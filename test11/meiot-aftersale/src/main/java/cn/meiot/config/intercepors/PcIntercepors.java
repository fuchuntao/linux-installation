package cn.meiot.config.intercepors;

import cn.meiot.controller.BaseController;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.Result;
import cn.meiot.utils.ErrorCodeConstant;
import cn.meiot.utils.UserInfoUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Package cn.meiot.config.intercepors
 * @Description:
 * @author: 武有
 * @date: 2019/9/17 18:33
 * @Copyright: www.spacecg.cn
 */

@Component
@Slf4j
@SuppressWarnings(value = "all")
public class PcIntercepors extends BaseController implements HandlerInterceptor {
    @Autowired
    private UserInfoUtil userInfoUtil;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        log.info("pc拦截器生效");
        System.out.println(getUserId());
        AuthUserBo userInfo = userInfoUtil.getUserInfo();
        if (null == userInfo) {
            log.info("用户信息为空");
            response.getWriter().println(JSONObject.toJSONString(new Result().Faild(ErrorCodeConstant.NO_USER_INFORMATION_WAS_FOUND)));
            response.getWriter().close();
            return false;
        }
//        MaintenanceStatusEnum.REPAIRS.value()
        if (userInfo.getUser().getType() != 1) {
            log.info("用户不是平台");
            response.getWriter().println(JSONObject.toJSONString(new Result().Faild("当前用户没有权限")));
            response.getWriter().close();
            return false;
        }
        return true;
    }

}
