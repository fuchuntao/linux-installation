package cn.meiot.aop;

import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.Result;
import cn.meiot.utils.LogUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志管理
 */
@Aspect
@Slf4j
@Component
@SuppressWarnings("ALL")
public class LogAspect extends BaseController {

    @Autowired
    private LogUtil logUtil;


    @Pointcut("@annotation(cn.meiot.aop.Log)")
    public void controllerLog() {
    }

    /**
     * 方法执行之后执行
     *
     * @param point
     */
    @AfterReturning(value = "controllerLog()", returning = "rest")
    public void log(JoinPoint point, Object rest) {
        if (null == rest || !(rest instanceof Result) || null == ((Result) rest).getCode()) {
//            log.info("日志服务获取的返回值类型为：{},不符合日志记录规则",rest);
            return;
        }

        try {
            Result result = (Result) rest;
    //            log.info("controller返回值：" + JSONObject.toJSONString(rest));
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String requestURI = request.getRequestURI();
            log.info("请求接口为：{}", requestURI);
            if (!StringUtils.equals(result.getCode(), "0")) {
                logUtil.saveLog(point, request, getUserId(), result);
            }
            logUtil.saveLog(point, request, getUserId());

        } catch (Exception e) {
            e.printStackTrace();
            log.info("{}", e);
        }
    }
}
