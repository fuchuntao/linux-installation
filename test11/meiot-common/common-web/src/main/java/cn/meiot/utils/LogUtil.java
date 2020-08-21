package cn.meiot.utils;

import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.ActionLogVo;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.ExceptionLogVo;
import cn.meiot.entity.vo.Result;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@Component
@Slf4j
@SuppressWarnings("all")
public class LogUtil  {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Setter
    private RedisTemplate redisTemplate;


    /**
     * 异步保存操作日志信息
     *
     * @param point
     * @param request
     * @throws JsonProcessingException
     */
    @Async
    public void saveLog(JoinPoint point,  HttpServletRequest request,Long userId) {
        ActionLogVo actionLog=getActionLog(point, request, userId);
        log.info("用户{}操作日志：{}",userId,actionLog);
        if (null == actionLog) {
            log.info("操作日志为空 此条不记录");
            return;
        }
            rabbitTemplate.convertAndSend(QueueConstantUtil.SAVE_OPERATION_LOG,actionLog);
    }

    /**
     * 保存异常日志
     * @param point
     * @param request
     * @param userId
     * @param result
     */
    @Async
    public void saveLog(JoinPoint point, HttpServletRequest request, Long userId, Result result) {
        String ip = getIpAddress(request);
        String agent = request.getHeader("User-Agent");
        String userAgent = UserAgentUtils.getDeviceName(agent);
        Log l = getLog(point);
        AuthUserBo authUserBo = getAuthUserBo(userId,request);
        String nikeName=authUserBo.getUser().getNickName();
        ExceptionLogVo exceptionLogVo=ExceptionLogVo.builder()
                .actionModel(l.operateModule())
                .content(l.operateContent())
                .createtime(LocalDateTime.now())
                .ip(ip)
                .msg(result.getMsg())
                .param(getParam(point.getArgs(),request, (MethodSignature) point.getSignature()))
                .name(null == nikeName ? "-" : nikeName)
                .useragent(userAgent)
                .userId(userId)
                .status(Long.parseLong(result.getCode()))
                .userType(authUserBo.getUser().getType())
                .username(authUserBo.getUser().getUserName())
                .url(request.getRequestURI())
                .build();
//        log.info("用户{}异常日志：{}",userId,exceptionLogVo);
        rabbitTemplate.convertAndSend(QueueConstantUtil.SAVE_EXCEPTION_LOG,exceptionLogVo);
    }

    /**
     * 获取普通日志对象
     * @param point
     * @param request
     * @param userId
     * @return
     */
    private ActionLogVo   getActionLog(JoinPoint point,  HttpServletRequest request,Long userId){
        String ip = getIpAddress(request);
        String agent = request.getHeader("User-Agent");
        String userAgent = UserAgentUtils.getDeviceName(agent);
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Log l = getLog(point);
        log.info(getRequestLog(request,point).toString());
        AuthUserBo authUserBo = getAuthUserBo(userId,request);
        if (authUserBo==null){
//            log.info("redis查询用户为空，此日志不记录");
            return null;
        }
        String nikeName = authUserBo.getUser().getNickName();

        ActionLogVo actionLog = ActionLogVo.builder()
                .param(getParam(point.getArgs(),request,methodSignature))
                .actionModel(l.operateModule())
                .ip(ip)
                .url(request.getRequestURI())
                .useragent(userAgent)
                .userId(userId)
                .username(authUserBo.getUser().getUserName())
                .name(null == nikeName ? "-" : nikeName)
                .content(l.operateContent())
                .type(authUserBo.getUser().getType())
                .createTime(LocalDateTime.now())
                .build();
        return actionLog;
    }

    private ExceptionLogVo getExceptionLog(JoinPoint point,  HttpServletRequest request,Long userId){

        return null;
    }

    private String getParam(Object[] args,HttpServletRequest request,MethodSignature methodSignature){
        if (args==null || request==null || request.getMethod()==null || methodSignature==null){
//            log.info("=======>>{},{},{},{}",args,request,request.getMethod(),methodSignature);
//            log.info("=======>> 有参数为空 返回“” getParam(Object[] args,HttpServletRequest request,MethodSignature methodSignature)");
            return "";
        }
        String param = "";//参数
        String[] parameterNames = methodSignature.getParameterNames();
        if ("POST".equals(request.getMethod().toUpperCase())) {
            param = args[0].toString();
        } else {
            for (int i = 0, len = parameterNames.length; i < len; i++) {
                System.out.println("参数名：" + parameterNames[i] + " = " + args[i]);
                param += parameterNames[i] + ":" + args[i] + ",";
            }
        }
        log.info("日志：请求参数为：=========>：{}", param);
        return param;
    }

    private StringBuffer getRequestLog(HttpServletRequest request,JoinPoint point){
        StringBuffer requestLog = new StringBuffer();
        requestLog.append("请求信息：")
                .append("URL = {" + request.getRequestURI() + "},\t")
                .append("HTTP_METHOD = {" + request.getMethod() + "},\t")
                .append("IP = {" + request.getRemoteAddr() + "},\t")
                .append("CLASS_METHOD = {" + point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName() + "},\t");
        return requestLog;
    }

    private Log getLog(JoinPoint point){
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Log annotation = method.getAnnotation(Log.class);
        log.info("当前接口信息{}，{}",annotation.operateModule(),annotation.operateContent());
        return annotation;
    }

    private AuthUserBo getAuthUserBo(Long userId,HttpServletRequest request){
        String  device = UserAgentUtils.getDeviceName(request.getHeader("User-Agent"));
        if("pc".equals(device)){
            device = "pc";
        }else{
            device = "phone";
        }
        String auth = (String) redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN +device+"_"+ userId);
//        log.info(auth);
        AuthUserBo authUserBo=null;
        try {
             authUserBo = new Gson().fromJson(auth, AuthUserBo.class);
        }catch (Exception e){
            Object parse = JSONObject.parse(auth);
//            log.info("=====>{}",parse);
            authUserBo =new Gson().fromJson((String) parse,AuthUserBo.class);
        }
        return authUserBo;
    }

    public static  String getIpAddress(HttpServletRequest request) {
//        String ip = request.getHeader("x-forwarded-for");
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("Proxy-Client-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_CLIENT_IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//            if("127.0.0.1".equals(ip)||"0:0:0:0:0:0:0:1".equals(ip)){
//                //根据网卡取本机配置的IP
//                InetAddress inet=null;
//                try {
//                    inet = InetAddress.getLocalHost();
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }
//                ip= inet.getHostAddress();
//            }
//        }
        String[] split = getClientIpAddress(request).split(",");
        return split[0];
    }

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };
    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                int index = ip.indexOf(",");
                if (index != -1) {
                    return ip.substring(0, index);
                }
                return ip;
            }
        }
        return request.getRemoteAddr();
    }


}
