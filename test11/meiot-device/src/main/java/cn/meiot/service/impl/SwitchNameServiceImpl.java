package cn.meiot.service.impl;

import java.util.List;
import java.util.Map;

import cn.meiot.client.UserClient;
import cn.meiot.service.SwitchTypeService;
import cn.meiot.utils.ConstantsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.meiot.dao.SwitchMapper;
import cn.meiot.dao.SwitchNameMapper;
import cn.meiot.entity.db.SwitchName;
import cn.meiot.service.SwitchNameService;
import cn.meiot.utils.RedisConstantUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingzhiying
 * @title: SwitchServiceImpl.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月3日
 */
@Service
@Slf4j
public class SwitchNameServiceImpl implements SwitchNameService{
	
	@Autowired
	private SwitchNameMapper switchNameMapper;
	
	@Autowired
	private SwitchMapper switchMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private SwitchTypeService switchTypeService;

	@Autowired
	private UserClient userClient;
	
	@Override
	public void insertSwitchName(String serialNumber, Long userId,Integer projectId) {
		List<SwitchName> listSn = switchMapper.querySwitch(serialNumber);
		Long switchTypeId = null;
		if(projectId != null){
			switchTypeId = switchTypeService.queryDefaultId(projectId);
		}
		String mainSwitch = userClient.getConfigValueByKey(ConstantsUtil.ConfigItem.MAIN_SWITCH);
		String subSwitch = userClient.getConfigValueByKey(ConstantsUtil.ConfigItem.SUB_SWITCH);
		for (int i = 0; i < listSn.size(); i++) {
			SwitchName switchName = listSn.get(i);
			if(i == 0) {
				switchName.setName(mainSwitch);
			}else {
				switchName.setName(subSwitch+i);
			}
			switchName.setUserId(userId);
			switchName.setSwitchType(switchTypeId);
			switchNameMapper.insertSelective(switchName);
			redisTemplate.opsForHash().put(RedisConstantUtil.NIKNAME_SWITCH, userId+"_"+switchName.getSwitchSn(), switchName.getName());
		}
	}

	@Override
	public void deleteSwitchName(String serialNumber, Long userId) {
		switchNameMapper.deleteSwitchName(serialNumber,userId);
		List<SwitchName> listSn = switchMapper.querySwitch(serialNumber);
		for (SwitchName switchName : listSn) {
			if(userId == null) {
				redisTemplate.opsForHash().delete(RedisConstantUtil.NIKNAME_SWITCH, "*_"+switchName.getSwitchSn());
			}else {
				redisTemplate.opsForHash().delete(RedisConstantUtil.NIKNAME_SWITCH, userId+"_"+switchName.getSwitchSn());
			}
		}
		redisTemplate.opsForHash().delete(RedisConstantUtil.SERIAL_NUMBER_USER_ID,serialNumber);
	}

	@Override
	public List<Map> querySwitchAll(String serialNumber, Long mainUserId) {
		return switchNameMapper.querySwitchAll(serialNumber,mainUserId);
	}
}
