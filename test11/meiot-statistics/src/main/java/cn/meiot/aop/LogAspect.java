//package cn.meiot.aop;
//
//import cn.meiot.controller.BaseController;
//import cn.meiot.utils.LogUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 日志管理
// */
//@Aspect
//@Slf4j
//@Component
//public class LogAspect extends BaseController {
//
//    @Autowired
//    private LogUtil logUtil;
//
//
//    @Pointcut("@annotation(cn.meiot.aop.Log)")
//    public void controllerLog(){}
//
//    /**
//     * 方法执行之后执行
//     * @param point
//     */
//    @AfterReturning( value = "controllerLog()",returning = "obj")
//    public void log(JoinPoint point,Object obj) throws Throwable {
//        try{
//            log.info("返回结果：{}",obj);
//            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            HttpServletRequest request = attributes.getRequest();
//            logUtil.saveLog(point,request,getUserId());
//        }catch (Exception e){
//            e.printStackTrace();
//            log.error("保存日志发生错误：{}",e.getMessage());
//        }
//
//    }
//
//
//
//}
