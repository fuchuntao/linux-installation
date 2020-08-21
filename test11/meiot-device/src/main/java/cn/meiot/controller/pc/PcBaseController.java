package cn.meiot.controller.pc;

import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.exception.MyTokenExcption;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cn.meiot.controller.BaseController;
import cn.meiot.service.UserService;

import java.util.List;

@Controller
public class PcBaseController extends BaseController{
	
	@Autowired
	private UserService userService;
	
	public Long getMainUserId() {
		return 10000121L;
		/*Long userId = getUserId();
		Long mainUserId = userService.getMainUserId(userId);
		return mainUserId;*/
	}
	/**
	 * 获取项目id(前端传值)
	 *
	 * @return
	 */
	public Integer getProjectId(Integer projectId) {
		return 23;/*
		if (projectId == null) {
			return getProjectId();
		}
		String device = getDevice();
		Object object = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN+device+"_"+getUserId());
		if(null == object){
			throw  new MyTokenExcption("未获取到用户信息","未获取到用户信息");
		}
		AuthUserBo authUserBo = new Gson().fromJson(object.toString(),AuthUserBo.class);
		List<Integer> projectIds = authUserBo.getProjectIds();
		if(null == projectIds || projectIds.size() == 0){
			throw  new MyTokenExcption("当前用户没有该项目,请尝试重新登录","当前用户没有项目,请尝试重新登录");
		}
		Integer id = Integer.valueOf(projectId);
		if(projectIds.contains(id))
			return id;
		throw  new MyTokenExcption("当前用户没有该项目,请尝试重新登录","当前用户没有项目,请尝试重新登录");
*/	}
	
}
