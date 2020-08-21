package cn.meiot.client.hystrix;

import org.springframework.stereotype.Component;

import cn.meiot.client.MessageCilent;
import cn.meiot.exception.MyServiceException;

import java.util.List;
import java.util.Map;

@Component
public class MessageHystrix implements MessageCilent {

	@Override
	public void deleteMsg(Long userId, String serialNumber) {
		throw new MyServiceException("消息中心删除错误", "消息中心删除错误");
	}

	@Override
	public int faultNumber(String serialNumber,Long userId) {
		return 0;
	}

	@Override
	public Map queryFaultSwitch(String serialNumber, String switchSn, Integer projectId) {
		return null;
	}

	@Override
	public List<String> queryFaultSerial(List<String> serialNumberList, Integer projectId, Integer type) {
		return null;
	}

}
