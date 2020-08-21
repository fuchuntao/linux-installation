package cn.meiot.client.hystrix;

import java.util.HashMap;
import java.util.Map;

import cn.meiot.constart.ProjectConstart;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.utils.ConstantsUtil;
import org.springframework.stereotype.Component;

import cn.meiot.client.UserClient;
import cn.meiot.constart.ResultConstart;
import cn.meiot.exception.MyServiceException;

@Component
public class UserHystrix implements UserClient{

	@Override
	public Map<String, Object> getInfoById(Long userId) {
		throw new MyServiceException(ResultCodeEnum.USER_ERROR.getCode(), ResultCodeEnum.USER_ERROR.getMsg());
		/*Map<String, Object>  map = new HashMap<String, Object>();
		map.put("phone", "13000000000");
		map.put("userName", "未找到用户名");
		return map;*/
	}

	@Override
	public Long getMainById(Long userId) {
		throw new MyServiceException("查询主用户异常", "查询主用户异常");
	}

	@Override
	public Map<String, String> queryProNameByProjectId(Integer projectId) {
		throw new MyServiceException("查询项目名和企业名异常", "查询项目名和企业名异常");
	}

	@Override
	public String getConfigValueByKey(String cKey) {
		switch (cKey){
			case ConstantsUtil.ConfigItem.SERIAL_NAME:
				return ProjectConstart.SERIAL_NAME;
			case ConstantsUtil.ConfigItem.MAIN_SWITCH:
				return ProjectConstart.MAIN_SWITCH;
			case ConstantsUtil.ConfigItem.SUB_SWITCH:
				return ProjectConstart.SUB_SWITCH;
		}
		return null;
	}

}
