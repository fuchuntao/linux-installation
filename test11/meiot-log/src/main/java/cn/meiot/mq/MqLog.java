package cn.meiot.mq;

import cn.meiot.entity.ActionLog;
import cn.meiot.entity.ActionLogVo;
import cn.meiot.entity.ExceptionLog;
import cn.meiot.entity.LoginLog;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.ExceptionLogVo;
import cn.meiot.entity.vo.SaveLogVo;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IExceptionLogService;
import cn.meiot.service.ILoginLogService;
import cn.meiot.utils.DateUtil;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import cn.meiot.service.IActionLogService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@Slf4j
public class MqLog {
    @Autowired
   private IActionLogService actionLogService;

    @Autowired
    private IExceptionLogService iExceptionLogService;

    @Autowired
    private ILoginLogService iLoginLogService;
    /**
     * 用户服务
     *
     * @param actionLog
     */
    @Autowired
    private UserFeign userFeign;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @RabbitListener(queues = QueueConstantUtil.SAVE_OPERATION_LOG)
    public void insertLog( ActionLogVo actionLogVo) {
       try{
           actionLogVo.setCreateTime(LocalDateTime.now());
           log.info("============--------------------->>>>>>>>>>>>>>操作日志服务接受到的参数为：{}", actionLogVo
           );
           //通过用户服务根据当前账号获取主账号
           ActionLog actionLog=new ActionLog();
           BeanUtils.copyProperties(actionLogVo,actionLog);
           Long mainUserId = userFeign.getMainUserIdByUserId(actionLog.getUserId());
           actionLog.setMainUserId(mainUserId);
           log.info("用户主账户ID为:{}", mainUserId);
           actionLogService.insertLog(actionLog);
       }catch (Exception e){
           e.printStackTrace();
           log.info("操作日志队列异常{}",e.getStackTrace());
       }
    }

    @RabbitListener(queues = QueueConstantUtil.SAVE_EXCEPTION_LOG)
    public void insertExceptionLog(ExceptionLogVo exceptionLogVo){
        try{
            log.info("-------->>>>>>>>>>>>>>>>>>>>>>>插入异常日志为：{}",exceptionLogVo);
            ExceptionLog exceptionLog=new ExceptionLog();
            BeanUtils.copyProperties(exceptionLogVo,exceptionLog);
            exceptionLog.setMainUserId(userFeign.getMainUserIdByUserId(exceptionLogVo.getUserId()));
            iExceptionLogService.save(exceptionLog);
            log.info("插入异常日志为：{}--异常时间为：{}",exceptionLog,exceptionLogVo.getCreatetime());
        }catch (Exception e){
            e.printStackTrace();
            log.info("异常日志队列异常：{}",e.getStackTrace());
        }

    }

    @RabbitListener(queues = QueueConstantUtil.SAVE_LOGIN_LOG)
    public void insertLoginLog(SaveLogVo saveLogVo){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try{
            log.info("--------------------------->>>>>>>>>>>>>>>>>>登录日志接受参数为：{}",saveLogVo);
            if (null==saveLogVo.getStatus() || saveLogVo.getStatus()!= 1){
                ExceptionLog exceptionLog=new ExceptionLog();
                BeanUtils.copyProperties(saveLogVo,exceptionLog);
                exceptionLog.setMsg(saveLogVo.getFailMsg());
                exceptionLog.setStatus(Long.valueOf(saveLogVo.getStatus()));
                exceptionLog.setMainUserId(userFeign.getMainUserIdByUserId(saveLogVo.getUserId()));
                exceptionLog.setCreatetime(DateUtil.sd.parse(saveLogVo.getCreateTime()));
                log.info("时间{}",exceptionLog.getCreatetime());
                iExceptionLogService.save(exceptionLog);
                return;
            }
            LoginLog loginLog=new LoginLog();
            BeanUtils.copyProperties(saveLogVo,loginLog);
            loginLog.setCreatetime(DateUtil.sd.parse(saveLogVo.getCreateTime()));
            log.info("时间{}",loginLog.getCreatetime());
            iLoginLogService.save(loginLog);
        }catch (Exception e){
            e.printStackTrace();
            log.info("登录日志队列异常：{}",e.getStackTrace());
        }

    }
}
