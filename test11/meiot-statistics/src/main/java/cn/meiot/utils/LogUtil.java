//package cn.meiot.utils;
//
//import cn.meiot.config.Log;
//import cn.meiot.entity.bo.AuthUserBo;
//import cn.meiot.entity.vo.ActionLog;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.google.gson.Gson;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.Signature;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.Method;
//
//@Component
//@Slf4j
//public class LogUtil {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//
//    /**
//     * 异步保存日志信息
//     *
//     * @param point
//     * @param request
//     * @throws JsonProcessingException
//     */
//    @Async
//    public void saveLog(JoinPoint point,  HttpServletRequest request,Long userId)  {
//        try{
//            String param = "";//参数
//
//            StringBuffer requestLog = new StringBuffer();
//            requestLog.append("请求信息：")
//                    .append("URL = {" + request.getRequestURI() + "},\t")
//                    .append("HTTP_METHOD = {" + request.getMethod() + "},\t")
//                    .append("IP = {" + request.getRemoteAddr() + "},\t")
//                    .append("CLASS_METHOD = {" + point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName() + "},\t");
//
//            String ip = request.getRemoteAddr();
//            String agent = request.getHeader("User-Agent");
//            String userAgent = UserAgentUtils.getDeviceName(agent);
//            Signature signature = point.getSignature();
//            MethodSignature methodSignature = (MethodSignature) signature;
//            //2.获取到方法的所有参数名称的字符串数组
//            String[] parameterNames = methodSignature.getParameterNames();
//            Method method = methodSignature.getMethod();
//
//            //获取参数
//            Object[] args = point.getArgs();
//            if("POST".equals(request.getMethod().toUpperCase())){
//                param =args[0].toString();
//            }else{
//                for (int i = 0, len = parameterNames.length; i < len; i++) {
//                    System.out.println("参数名：" + parameterNames[i] + " = " + args[i]);
//                    param += parameterNames[i] + ":" + args[i]+",";
//                }
//            }
//            log.info("哈哈哈哈哈=========>：{}", param);
//            Log l = method.getAnnotation(Log.class);
//            System.out.println("自定义注解 keyField:" + l.operateContent());
//            log.info(requestLog.toString());
//            String auth= (String) redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN + userId);
//            AuthUserBo authUserBo = new Gson().fromJson(auth,AuthUserBo.class);
//            String nikeName = authUserBo.getUser().getNickName();
//            ActionLog actionLog = ActionLog.builder()
//                    .param(param)
//                    .actionModel("用户中心")
//                    .ip(ip)
//                    .useragent(userAgent)
//                    .userId(userId)
//                    .username(null == nikeName ? authUserBo.getUser().getUserName() : nikeName)
//                    .content(l.operateContent())
//                    .build();
//
//            rabbitTemplate.convertAndSend(QueueConstantUtil.SAVE_OPERATION_LOG, actionLog);
//        }catch (Exception e){
//            e.printStackTrace();
//            log.info(e.getMessage());
//        }
//
//    }
//
//}
