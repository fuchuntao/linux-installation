package cn.meiot.utils;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.SaveLogVo;
import cn.meiot.interceptor.xss.XssFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 登录的工具类
 */
@Component

@Slf4j
public class LoginUtil {

    private RabbitTemplate rabbitTemplate;

    public LoginUtil(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 保存登录登录日志
     */
    public  void saveLoginLog(SaveLogVo saveLogVo)  {

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();
        String bodyString = null;
        try {
            bodyString = XssFilter.getBodyString(request.getReader());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("请求参数：{}",bodyString);
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            System.out.println(entry.getKey());
            for (String string : entry.getValue()) {
                System.out.println(string);
            }
            System.out.println("----------------");
        }
        saveLogVo.setParam(bodyString);
        saveLogVo.setUrl(request.getRequestURI());
        saveLogVo.setIp(LogUtil.getIpAddress(request));
        saveLogVo.setActionModel("用户中心");
        String time = ConstantsUtil.DF.format(new Date());
        log.info("操作时间=================>：{}",time);
        saveLogVo.setCreateTime(time);
        log.info("保存日志为：{}",saveLogVo);
        rabbitTemplate.convertAndSend(QueueConstantUtil.SAVE_LOGIN_LOG,saveLogVo);
    }
}
