package cn.meiot.client.hystrix;

import java.util.Map;

import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import cn.meiot.client.CommunicationClient;
import cn.meiot.constart.ResultConstart;
import cn.meiot.entity.vo.Result;

@Component
public class CommunicationHystrix implements CommunicationClient{
	
	public Result sendSwitch(@RequestBody Map map) {
		throw new MyServiceException(ResultCodeEnum.COMMUNICATION_ERROR.getCode(), ResultCodeEnum.COMMUNICATION_ERROR.getMsg());
	}
}
