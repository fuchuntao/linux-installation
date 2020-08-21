package cn.meiot.config;


import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.RedisConstantUtil;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitConfig {

    public static final String QUEUE_NAME = "saveMsg";

    private static final String SAVE_PERMISSION="savePermission";

    @Bean
    public Queue saveMsg(){

        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Queue savePermission(){

        return new Queue(SAVE_PERMISSION);
    }

    /**
     * 添加用户openid
     * @return
     */
    @Bean
    public Queue saveUserOpenid(){

        return new Queue(QueueConstantUtil.SAVE_USER_OPENID);
    }


    /**
     * 删除用户openid
     * @return
     */
    @Bean
    public Queue deleteUserOpenid(){

        return new Queue(QueueConstantUtil.DELETE_USER_OPENID);
    }

    /**
     * 禁用账号，清理token
     * @return
     */
    @Bean
    public Queue kickUser(){

        return new Queue(QueueConstantUtil.TAKE_THE_USER_OFFLINE);
    }

    /**
     * 删除没用的token信息
     * @return
     */
    @Bean
    public Queue delUserToken(){

        return new Queue(RedisConstantUtil.DEL_USER_TOKEN);
    }


    /**
     * 删除多余的项目权限
     * @return
     */
    @Bean
    public Queue delProjectSurplusPermission(){

        return new Queue(QueueConstantUtil.DELETE_SURPLUS_PROJECT_PERMISSION);
    }


    /**
     * 删除多余的企业权限
     * @return
     */
    @Bean
    public Queue delEnterpriseSurplusPermission(){

        return new Queue(QueueConstantUtil.DELETE_SURPLUS_ENTERPRISE_PERMISSION);
    }



    /**
     * 权限校验，是否需要重新登录
     * @return
     */
    @Bean
    public Queue permission_check(){

        return new Queue(QueueConstantUtil.PERMISSION_CHECK);
    }

}
