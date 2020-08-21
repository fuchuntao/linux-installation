package cn.meiot.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import cn.meiot.utils.QueueConstantUtil;

/**
 * @ClassName: RabbitConfig
 * @Description: 系统日志
 * @author 贺志辉
 * @date 2019年9月5日
 */
@Component
public class RabbitConfig {

//	public static final String SAVE_OPERATION_LOG = "saveOperationLog";

	/**
	 * 保存操作日志
	 * @return
	 */
	@Bean
	public Queue saveLog() {
		return new Queue(QueueConstantUtil.SAVE_OPERATION_LOG);
	}


	/**
	 * 保存异常日志队列
	 * @return
	 */
	@Bean
	public Queue saveExceptionLog() {
		return new Queue(QueueConstantUtil.SAVE_EXCEPTION_LOG);
	}
/**
 * 保存登录日志队列
 * @return
 */
	@Bean
	public Queue saveLoginLog() {
		return new Queue(QueueConstantUtil.SAVE_LOGIN_LOG);
	}
}
