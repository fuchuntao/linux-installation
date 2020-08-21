package cn.meiot.service.impl;

import cn.meiot.utils.ConstantsUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.meiot.client.UserClient;
import cn.meiot.service.UserService;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserClient userClient;
	
	@Override
	public Long getMainUserId(Long userId) {
		Long mainById = userClient.getMainById(userId);
		return mainById;
	}

	@Override
	public Map<String, Map<String, String>> getRule() {
		String configValueByKey = userClient.getConfigValueByKey(ConstantsUtil.ConfigItem.DEVICE_FAMILY_RULES);
		Map<String,Map<String,String>> maps = (Map) JSON.parse(configValueByKey);
		return maps;
	}

}
