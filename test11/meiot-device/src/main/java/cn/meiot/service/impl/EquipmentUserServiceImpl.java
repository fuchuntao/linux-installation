package cn.meiot.service.impl;

import cn.meiot.client.MessageCilent;
import cn.meiot.client.UserClient;
import cn.meiot.config.HttpConstart;
import cn.meiot.constart.RedisConstart;
import cn.meiot.dao.*;
import cn.meiot.entity.db.Equipment;
import cn.meiot.entity.db.EquipmentUser;
import cn.meiot.entity.db.Switch;
import cn.meiot.entity.dto.EquipmentUserDto;
import cn.meiot.entity.dto.UpdateName;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.equipmentUser.EquipmentUserResp;
import cn.meiot.entity.excel.ProjectExcel;
import cn.meiot.entity.excel.UserExcel;
import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.entity.vo.PersonalSerialVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SysMsgVo;
import cn.meiot.enums.DeviceBindStatus;
import cn.meiot.enums.EquipmentStatus;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.*;
import cn.meiot.utils.*;
import cn.meiot.utlis.PhoneUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.Transient;
import java.util.*;

/**
 * @author lingzhiying
 * @title: EquipmentUserServiceImpl.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月19日
 */
@Service
@Slf4j
public class EquipmentUserServiceImpl implements EquipmentUserService {

	@Autowired
	private EquipmentUserMapper equipmentUserMapper;

	@Autowired
	private EquipmentApiMapper equipmentApiMapper;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private UserClient userClient;
	
	@Autowired
	private SwitchNameService switchNameService;
	
	@Autowired
	private EquipmentMapper equipmentMapper;
	
	@Autowired
	private MessageCilent messageCilent;
	
	@Autowired
	private SwitchMapper switchMapper;
	
	@Autowired
	private RoleEquipmentMapper roleEquipmentMapper;
	
	@Autowired
	private NetworkingUtlis networkingUtlis;
	
	@Autowired
	private HttpConstart httpConstart;
	
	@Autowired
	private BuildingService buildingService;
	
	@Autowired
	private SwitchService switchService;

	@Autowired
	private UseTimeService useTimeService;

	@Autowired
	private UserService userService;

	@Autowired
	private EquipmentService equipmentService;

	@Autowired
	private RoleService roleService;

	@Override
	public boolean queryUserIdAndSerialumber(Long userId, String serialNumber) {
		Integer id = equipmentUserMapper.queryUserIdAndSerialumber(userId, serialNumber);
		if(id == null) {
			return false;
		}
		return true;
	}

	@Override
	public Result bindEquipment(String serialNumber, Long userId) {
		Integer row = equipmentUserMapper.bindEquipmentUser(userId, serialNumber);
		if(row == null || row != 1 ) {
			Result result = Result.getDefaultFalse();
			result.setMsg(ResultCodeEnum.EQUIMENT_NO_EXISTENT.getMsg());
			result.setCode(ResultCodeEnum.EQUIMENT_NO_EXISTENT.getCode());
			return result;
		}
		Result result = Result.getDefaultTrue();
		return result;
	}

	@Override
	public Result 	queryEquipmentUser(Long userId) {
		List<Map> listMap = equipmentUserMapper.queryEquipmentUser(userId);
		Map<String, Map<String,String>> maps = userService.getRule();
		Map<String,String> map = new HashMap<>();
		for (Map map2 : listMap) {
			String serialNumber = (String) map2.get("serialNumber");
			map2.put("networking", networkingUtlis.getNetworkingStatus(httpConstart.getHttp(),serialNumber));
			String imageKey = EquipmentUtil.getImageKey(maps, serialNumber, ConstantsUtil.DEVICE_DEFAULT_RULES_SMAL_KEY);
			String url = map.get(imageKey);
			if(StringUtils.isEmpty(url)){
				url = userClient.getConfigValueByKey(imageKey);
				map.put(imageKey,url);
			}
			String mPath = FileConfigVo.getMPath(url);
			map2.put("image",mPath);
		}
		Result result = Result.getDefaultTrue();
		result.setData(listMap);
		return result;
	}

	@Override
	public Result updateName(UpdateName updateName) {
		Integer row = equipmentUserMapper.updateName(updateName);
		if(row == null || row != 1 ) {
			Result result = Result.getDefaultFalse();
			result.setMsg(ResultCodeEnum.EQUIMENT_NO_EXISTENT.getMsg());
			result.setCode(ResultCodeEnum.EQUIMENT_NO_EXISTENT.getCode());
			return result;
		}
		redisTemplate.opsForHash().put(RedisConstantUtil.NIKNAME_SERIALNUMBER, updateName.getUserId()+"_"+updateName.getSerialNumber(), updateName.getName());
		Result result = Result.getDefaultTrue();
		return result;
	}

	@Override
	public Result getRtuserIdBySerialNumber(String serialNumber) {
		Object obj = null;
		Result defaultTrue = Result.getDefaultTrue();
		try {
			obj = redisTemplate.opsForHash().get(RedisConstantUtil.SERIAL_NUMBER_USER_ID,serialNumber);
			if(obj == null) {
				List<String> listUser = equipmentUserMapper.getRtuserIdBySerialNumber(serialNumber);
				if(listUser.size() > 0 ) {
					redisTemplate.opsForHash().put(RedisConstantUtil.SERIAL_NUMBER_USER_ID, serialNumber, listUser);
				}else {
					listUser = null;
				}
				defaultTrue.setData(listUser);
			}else {
				defaultTrue.setData(obj);
			}
		}catch (Exception e) {
			List<String> listUser = equipmentUserMapper.getRtuserIdBySerialNumber(serialNumber);
			if(listUser.size() > 0 ) {
				redisTemplate.opsForHash().put(RedisConstantUtil.SERIAL_NUMBER_USER_ID, serialNumber, listUser);
			}else {
				listUser = null;
			}
			defaultTrue.setData(listUser);
		}
		log.info("查询设备的用户列表:{}",defaultTrue.getData());
		return defaultTrue;
	}

	@Override
	@Transactional
	public Result approvalEquipment(EquipmentUserDto equipmentUserDto) {
		Integer type = equipmentUserDto.getStatus();
		log.info("审批设备: {}",equipmentUserDto);

		Integer status = 0;
		EquipmentUser equipmentUser = equipmentUserMapper.selectByPrimaryKey(equipmentUserDto.getId());
		if(equipmentUser == null){
			throw new MyServiceException(ResultCodeEnum.MAIN_UNBIND.getCode(),ResultCodeEnum.MAIN_UNBIND.getMsg());
		}
		Long userId = equipmentUser.getUserId();
		Integer isDefault = 0 ;
		if(type.equals(1)) {
			useTimeService.insert(userId,null);
			status = DeviceBindStatus.AGREE.value();
			Long count = equipmentUserMapper.queryIsCountByDefault(userId,equipmentUserDto.getId());
			if(count.equals(0L)) {
				isDefault = 1;
			}
			switchNameService.insertSwitchName(equipmentUser.getSerialNumber(), userId,null);
			redisTemplate.opsForHash().put(RedisConstantUtil.SERIAL_NUMBER_USER_ID, equipmentUser.getSerialNumber(), null);
			log.info("审批同意设备: {}",equipmentUserDto);
		}else  {
			type = 2;
			log.info("审批拒绝设备: {}",equipmentUserDto);
		}
		redisTemplate.opsForHash().delete(RedisConstantUtil.SERIAL_NUMBER_USER_ID, equipmentUser.getSerialNumber());
		Long mainUserId = equipmentUserMapper.getRtuserIdByUserId(equipmentUser.getSerialNumber());
		//默认设备名
		String serialName = userClient.getConfigValueByKey(ConstantsUtil.ConfigItem.SERIAL_NAME);
		if(mainUserId.equals(equipmentUserDto.getUserId())) {
			SysMsgVo sysMsgVo = new SysMsgVo();
			sysMsgVo.setType(1);
			sysMsgVo.setExtendId(equipmentUserDto.getId());
			sysMsgVo.setDealStatus(status);
			Map map = new HashMap();
			sysMsgVo.setExtras(map);
			rabbitTemplate.convertAndSend(QueueConstantUtil.SYS_MSG_QUEUE, sysMsgVo);
			equipmentUser.setUserStatus(type);
			equipmentUser.setIsDefault(isDefault);
			equipmentUser.setName(serialName);
			equipmentUserMapper.updateByPrimaryKeySelective(equipmentUser);
			redisTemplate.opsForHash().put(RedisConstantUtil.NIKNAME_SERIALNUMBER, equipmentUser.getUserId()+"_"+equipmentUser.getSerialNumber(), equipmentUser.getName());
			return Result.getDefaultTrue();
		}
		Result result = Result.getDefaultFalse();
		result.setMsg(ResultCodeEnum.NO_AUTHENTICATION.getMsg());
		result.setCode(ResultCodeEnum.NO_AUTHENTICATION.getCode());
		return Result.getDefaultFalse();
	}

	
	@Override
	@Transactional
	public Result unbound(EquipmentUser equipmentUser) {
		equipmentUser.setUserStatus(1);
		EquipmentUser selectOne = equipmentUserMapper.selectOne(equipmentUser);
		String serialNumber = equipmentUser.getSerialNumber();
		if(selectOne == null) {
			log.info("解绑设备失败: {}",equipmentUser);
			return Result.getDefaultFalse();
		}
		unboundSerialNumber(serialNumber, selectOne.getUserId());
		Long userId = equipmentUser.getUserId();
		redisTemplate.opsForHash().delete(RedisConstantUtil.SERIAL_NUMBER_USER_ID, serialNumber);
		equipmentUserMapper.deleteByPrimaryKey(selectOne.getId());
		switchNameService.deleteSwitchName(serialNumber, userId);
		Map<String, Object> subinfoById = userClient.getInfoById(Long.valueOf(equipmentUser.getUserId()+""));
		log.info("用户解绑设备----------------: {} ",selectOne);
		if(selectOne.getIsPrimary().equals(1)) {
			switchService.resetSwitch(serialNumber);
			messageCilent.deleteMsg(null, serialNumber);
			redisTemplate.delete(RedisConstantUtil.PROJECT_SERIALNUMER+serialNumber);
			//并且删除名称
			switchNameService.deleteSwitchName(serialNumber, null);
			List<Map> queryUserName = equipmentUserMapper.queryUserName(serialNumber);
			//当前账号是主账号解绑
			equipmentUserMapper.deleteBySerialNumber(serialNumber);
			if(!CollectionUtils.isEmpty(queryUserName)) {
				for (Map map :queryUserName) {
					Integer isDefault = (Integer) map.get("isDefault");
					String sonUserIdSTR = map.get("userId") + "";
					Long sonUserId = Long.valueOf(sonUserIdSTR);
					if(isDefault.equals(1)){
						unboundSerialNumber(sonUserId);
					}
					List<String> listUser = new ArrayList<>();
					listUser.add(sonUserIdSTR);
					//走队列发消息
					SysMsgVo sysMsgVo = new SysMsgVo();
					sysMsgVo.setSerialNumber(serialNumber);
					sysMsgVo.setType(2);
					sysMsgVo.setUserId(listUser);
					String serialNumberName = userClient.getConfigValueByKey(ConstantsUtil.ConfigItem.SERIAL_NAME);
					if(StringUtils.isNotBlank((String)map.get("name"))) {
						serialNumberName = (String) map.get("name");
					}
					sysMsgVo.setSerialName(serialNumberName);
					Map<String,String> extras = new HashMap<String,String>();
					extras.put("mainUserPhone", PhoneUtils.getPhone(subinfoById.get("phone")));
					extras.put("mainUserName", subinfoById.get("userName")+"");
					extras.put("mainUser", equipmentUser.getUserId()+"");
					sysMsgVo.setExtras(extras);
					rabbitTemplate.convertAndSend(QueueConstantUtil.UNBIND_DEVICE_NOTIFICATION,sysMsgVo);
					log.info("主用户解绑设备成功: {}  -- 消息参数:{}",equipmentUser,sysMsgVo);
					useTimeService.deleteSerial(sonUserId,null);
				}
			}
		}else {
			messageCilent.deleteMsg(userId, serialNumber);
		}
		useTimeService.deleteSerial(userId,null);
		redisTemplate.opsForHash().delete(RedisConstantUtil.SERIAL_NUMBER_USER_ID,serialNumber);
		redisTemplate.opsForHash().put(RedisConstantUtil.SERIAL_NUMBER_USER_ID, equipmentUser.getSerialNumber(), null);
		return Result.getDefaultTrue();
	}

	@Override
	public Result getRtuserIdByUserId(String serialNumber) {
		Long userId = equipmentUserMapper.getRtuserIdByUserId(serialNumber);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(userId);
		return defaultTrue;
	}

	@Override
	public Result queryEquipment(String serialNumber, Long userId) {
		Map map = equipmentUserMapper.queryEquipment(userId,serialNumber);
		Map<String, Map<String,String>> maps = userService.getRule();
		String imageKey = EquipmentUtil.getImageKey(maps, serialNumber, ConstantsUtil.DEVICE_DEFAULT_RULES_SMAL_KEY);
		String url = userClient.getConfigValueByKey(imageKey);
		map.put("image",FileConfigVo.getMPath(url));
		map.put("networking", networkingUtlis.getNetworkingStatus(null,serialNumber));
		return Result.OK(map);
	}

	@Override
	@Transactional
	public Result unDefault(EquipmentUser equipmentUser) {
		//设置其他设备为非默认设备
		equipmentUser.setIsDefault(0);
		String serialNumber = equipmentUser.getSerialNumber();
		//设置条件设备号为空
		equipmentUser.setSerialNumber(null);
		//先修改当前用户所有设备为非默认设备
		equipmentUserMapper.unDefault(equipmentUser);
		equipmentUser.setSerialNumber(serialNumber);
		equipmentUser.setIsDefault(1);
		//修改当前设备为默认设备
		int row = equipmentUserMapper.unDefault(equipmentUser);
		if(row != 1) {
			throw new MyServiceException(ResultCodeEnum.UPDATE_DEFAULT_ERROR.getCode(), ResultCodeEnum.UPDATE_DEFAULT_ERROR.getMsg());
		}
		return Result.getDefaultTrue();
	}

	@Override
	@Transient
	public Result updateUserName(EquipmentUser equipmentUser) {
		equipmentUser.setUserId(null);
		int row = equipmentUserMapper.updateEquipmentUser(equipmentUser);
		if(row != 1) {
			throw new MyServiceException(ResultCodeEnum.UPDATE_USERNAME_ERROR.getCode(),ResultCodeEnum.UPDATE_USERNAME_ERROR.getMsg());
		}
		return Result.getDefaultTrue();
	}

	@Override
	public boolean queryIsMainUser(String serialNumber, Long userId) {
		Long rtuserIdByUserId = equipmentUserMapper.getRtuserIdByUserId(serialNumber);
		if(userId.equals(rtuserIdByUserId)) {
			return true;
		}
		return false;
	}

	@Override
	public Result queryUserName(String serialNumber) {
		List<Map> listMap = equipmentUserMapper.queryUserName(serialNumber);
		for (Map map : listMap) {
			Map<String, Object> subinfoById = userClient.getInfoById(Long.valueOf(map.get("userId").toString()));
			String userName = (String) map.get("userName");
			if(subinfoById != null) {
				if(subinfoById.get("phone")!= null) {
					String phone = PhoneUtils.getPhone(subinfoById.get("phone"));
					map.put("phone",phone);
					if(StringUtils.isEmpty(userName)){
						map.put("userName",phone);
					}
				}
			}
			String avatar = (String) redisTemplate.opsForHash().get(RedisConstantUtil.USER_HEAD_PORTRAIT, String.valueOf(map.get("userId")));
			if(StringUtils.isEmpty(avatar)){
                avatar = userClient.getConfigValueByKey(ConstantsUtil.ConfigItem.USER_DEFAULT_HEAD_PORTRAIT);
			}
			avatar = FileConfigVo.getThuPath(avatar);
			map.put("avatar", avatar);
		}
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(listMap);
		return defaultTrue;
	}

	@Override
	public Result queryMain(Long userId) {
		List<Map> listMap = equipmentUserMapper.queryMain(userId);
		Map<String, Map<String,String>> maps = userService.getRule();
		for (Map map2 : listMap) {
			String serialNumber = (String) map2.get("serialNumber");
			String imageKey = EquipmentUtil.getImageKey(maps, serialNumber, ConstantsUtil.DEVICE_DEFAULT_RULES_SMAL_KEY);
			String url = userClient.getConfigValueByKey(imageKey);
			map2.put("image",FileConfigVo.getMPath(url));
		}
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(listMap);
		return defaultTrue;
	}

	@Override
	public boolean queryIsMainUserById(Long id, Long userId) {
		Long rtuserIdByUserId = equipmentUserMapper.queryIsMainUserById(id);
		if(userId.equals(rtuserIdByUserId)) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public Result delete(EquipmentUser equipmentUser) {
		EquipmentUser selectByPrimaryKey = equipmentUserMapper.selectByPrimaryKey(equipmentUser.getId());
		String serialNumber = selectByPrimaryKey.getSerialNumber();
		Long userId = selectByPrimaryKey.getUserId();
		switchNameService.deleteSwitchName(serialNumber, userId);
		//消息删除该用户的设备
		messageCilent.deleteMsg(userId, serialNumber);
		//解绑默认设备 并添加默认设备
		unboundSerialNumber(serialNumber, userId);
		//修改用户使用时间
		useTimeService.deleteSerial(userId,null);
		int row = equipmentUserMapper.deleteByPrimaryKey(equipmentUser.getId());
		//走队列发消息
		List<String> listUser = new ArrayList<>();
		listUser.add(selectByPrimaryKey.getUserId()+"");
		Map<String, Object> subinfoById = userClient.getInfoById(Long.valueOf(equipmentUser.getUserId()+""));
		SysMsgVo sysMsgVo = new SysMsgVo();
		sysMsgVo.setSerialNumber(selectByPrimaryKey.getSerialNumber());
		sysMsgVo.setType(3);
		sysMsgVo.setUserId(listUser);
		String serialNumberName = userClient.getConfigValueByKey(ConstantsUtil.ConfigItem.SERIAL_NAME);
		if(StringUtils.isNotBlank(selectByPrimaryKey.getName())) {
			serialNumberName = selectByPrimaryKey.getName();
		}
		sysMsgVo.setSerialName(serialNumberName);
		Map<String,String> extras = new HashMap<String,String>();
		extras.put("mainUserPhone", PhoneUtils.getPhone(subinfoById.get("phone")));
		extras.put("mainUserName", subinfoById.get("userName")+"");
		extras.put("mainUser", equipmentUser.getUserId()+"");
		sysMsgVo.setExtras(extras);
		rabbitTemplate.convertAndSend(QueueConstantUtil.UNBIND_DEVICE_NOTIFICATION,sysMsgVo);
		log.info("主用户解绑子设备成功: {}  -- 消息参数:{}",equipmentUser,sysMsgVo);
		if(row != 1) {
			throw new MyServiceException(ResultCodeEnum.UNBIND_SUB_USER_ERROR.getCode(), ResultCodeEnum.UNBIND_SUB_USER_ERROR.getMsg());
		}
		return Result.getDefaultTrue();
	}

	@Override
	public Result queryUser(PcEquipmentUserCond cond) {
		PageHelper.startPage(cond.getPage(), cond.getPageSize());
		List<EquipmentUserResp> listData = equipmentUserMapper.queryUser(cond);
		PageInfo pageinfo = new PageInfo<>(listData);
		for (EquipmentUserResp equipmentUserResp : listData) {
			//去用户名
			Result userIds = getRtuserIdBySerialNumber(equipmentUserResp.getSerialNumber());
			try {
			List<String> data2 = (List<String>) userIds.getData();
			equipmentUserResp.setUserCount(data2.size());
			}catch (Exception e) {
				log.info("设备号获取用户列表失败:{}",userIds);
			}
		}
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(pageinfo);
		return defaultTrue;
	}

	
	@Override
	public List<UserExcel> queryUserExcel(PcEquipmentUserCond cond) {
		List<UserExcel> listData = equipmentUserMapper.queryUserExcel(cond);
		final String serialName = userClient.getConfigValueByKey(ConstantsUtil.ConfigItem.SERIAL_NAME);
		for (UserExcel equipmentUserResp : listData) {
			if(StringUtils.isEmpty(equipmentUserResp.getSerialName())){
				equipmentUserResp.setSerialName(serialName);
			}
			//去用户名
			Result userIds = getRtuserIdBySerialNumber(equipmentUserResp.getSerialNumber());
			try {
				Map<String, Object> map = userClient.getInfoById(equipmentUserResp.getUserId().longValue());
				equipmentUserResp.setUserName((String)map.get("userName"));
				List<String> data2 = (List<String>) userIds.getData();
				equipmentUserResp.setUserCount(data2.size());
			}catch (Exception e) {
				log.info("设备号获取用户列表失败:{}",userIds);
			}
		}
		return listData;
	}
	
	@Override
	public List<ProjectExcel> queryProjectExcel(PcEquipmentUserCond cond) {
		List<ProjectExcel> listData = equipmentUserMapper.queryProjectExcel(cond);
		final String serialName = userClient.getConfigValueByKey(ConstantsUtil.ConfigItem.SERIAL_NAME);
		listData.forEach( pe->{
			if(StringUtils.isEmpty(pe.getSerialName())){
				pe.setSerialName(serialName);
			}
			try {
				Map<String, String> queryProNameByProjectId = userClient.queryProNameByProjectId(pe.getProjectId());
				pe.setEnterpriseName(queryProNameByProjectId.get("enterpriseName"));
				pe.setProjectName(queryProNameByProjectId.get("projectName"));
			}catch(Exception e) {
				log.info("获取项目名和企业名异常：{}",pe.getProjectId());
			}
		});
		return listData;
	}
	
	@Override
	public Result queryProject(PcEquipmentUserCond cond) {
		PageHelper.startPage(cond.getPage(), cond.getPageSize());
		List<EquipmentUserResp> listData = equipmentUserMapper.queryProject(cond);
		PageInfo pageinfo = new PageInfo<>(listData);
		for (EquipmentUserResp equipmentUserResp : listData) {
			Map<String, String> queryProNameByProjectId = userClient.queryProNameByProjectId(equipmentUserResp.getProjectId());
			equipmentUserResp.setEnterpriseName(queryProNameByProjectId.get("enterpriseName"));
			equipmentUserResp.setProjectName(queryProNameByProjectId.get("projectName"));
		}
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(pageinfo);
		return defaultTrue;
	}

	@Override
	public Result queryUserBySerialNumber(PcEquipmentUserCond cond) {
		Result userIds = getRtuserIdBySerialNumber(cond.getSerialNumber());
		List<String> data2 = (List<String>) userIds.getData();
		List<Map> listMap = new ArrayList<Map>(data2.size());
		for (String userId : data2 ) {
			listMap.add(userClient.getInfoById(Long.valueOf(userId)));
		}
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(listMap);
		return defaultTrue;
	}

	@Override
	@Transactional
	public Result pcDelete(List<String> listIds) {
		equipmentUserMapper.deleteBySerialNumberS(listIds);
		listIds.forEach(serial->{
			messageCilent.deleteMsg(null, serial);
			//修改旧设备的组织架构
			Equipment oldEquipment = new Equipment();
			oldEquipment.setSerialNumber(serial);
			oldEquipment.setBuildingId(0L);
			equipmentMapper.updateByPrimaryKeySelective(oldEquipment);
			
			/*roleEquipmentMapper.deleteSerialnumber(seria);
			switchNameService.deleteSwitchName(seria, null);
			redisTemplate.delete(RedisConstantUtil.PROJECT_SERIALNUMER+seria);
			redisTemplate.opsForHash().delete(RedisConstantUtil.SERIAL_NUMBER_USER_ID, seria);
			redisTemplate.opsForHash().delete(RedisConstantUtil.NIKNAME_SERIALNUMBER, "*_"+seria);
			switchService.resetSwitch(seria);*/
			deleteSerial(serial);
		});
		Result defaultTrue = Result.getDefaultTrue();
		return defaultTrue;
	}

	public void deleteSerial(String serial){
		roleEquipmentMapper.deleteSerialnumber(serial);
		switchNameService.deleteSwitchName(serial, null);
		redisTemplate.delete(RedisConstantUtil.PROJECT_SERIALNUMER+serial);
		redisTemplate.opsForHash().delete(RedisConstantUtil.SERIAL_NUMBER_USER_ID, serial);
		redisTemplate.opsForHash().delete(RedisConstantUtil.NIKNAME_SERIALNUMBER, "*_"+serial);
		switchService.resetSwitch(serial);
		//equipmentUserMapper.deleteBySerialNumber(oldSerialNumber);
	}

	@Override
	@Transactional
	public Result updatePc(EquipmentUser equipmentUser) {
		String serialNumber = equipmentUser.getSerialNumber();
		EquipmentUser oldEquipmentUser = equipmentUserMapper.selectByPrimaryKey(equipmentUser.getId());
		if(!equipmentUser.getUserId().equals(oldEquipmentUser.getUserId()) || !equipmentUser.getProjectId().equals(oldEquipmentUser.getProjectId())) {
			return Result.getDefaultFalse();
		}
		//旧设备号
		String oldSerialNumber = oldEquipmentUser.getSerialNumber();
		switchService.resetSwitch(oldSerialNumber);
		messageCilent.deleteMsg(null, oldSerialNumber);
		equipmentUserMapper.updateByPrimaryKeySelective(equipmentUser);
		redisTemplate.opsForHash().put(RedisConstantUtil.NIKNAME_SERIALNUMBER, equipmentUser.getUserId()+"_"+serialNumber, equipmentUser.getName());
		if(serialNumber.equals(oldSerialNumber)) {
			Result defaultTrue = Result.getDefaultTrue();
			return defaultTrue;
		}
		roleEquipmentMapper.deleteSerialnumber(oldSerialNumber);
		roleEquipmentMapper.insertBuildingEquipment(equipmentUser.getBuilding(),equipmentUser.getSerialNumber(),oldEquipmentUser.getProjectId());
		//删除旧设备
		redisTemplate.delete(RedisConstantUtil.PROJECT_SERIALNUMER+oldSerialNumber);
		redisTemplate.opsForHash().delete(RedisConstantUtil.SERIAL_NUMBER_USER_ID, oldSerialNumber);
		redisTemplate.opsForHash().delete(RedisConstantUtil.NIKNAME_SERIALNUMBER, equipmentUser.getUserId()+"_"+oldSerialNumber);
		//删除旧开关别名
		switchNameService.deleteSwitchName(oldSerialNumber, null);
		//修改旧设备的组织架构
		/*Equipment oldEquipment = new Equipment();
		oldEquipment.setSerialNumber(oldSerialNumber);
		oldEquipment.setBuildingId(0L);*/
		//查询开关
		Equipment oldEquipment = equipmentMapper.selectByPrimaryKey(oldSerialNumber);
		Long buildingId = oldEquipment.getBuildingId();
		oldEquipment.setExaminationStatus(0);
		oldEquipment.setExaminationTime("");
		oldEquipment.setBuildingId(0L);
		//并恢复自由
		equipmentMapper.updateByPrimaryKeySelective(oldEquipment);
		
		//Equipment selectByPrimaryKey = equipmentMapper.selectByPrimaryKey(serialNumber);
		Equipment selectByPrimaryKey = new Equipment();
		selectByPrimaryKey.setSerialNumber(serialNumber);
		selectByPrimaryKey.setBuildingId(buildingId);
		equipmentMapper.updateByPrimaryKeySelective(selectByPrimaryKey);
		//添加新开关别名
		switchNameService.insertSwitchName(equipmentUser.getSerialNumber(), equipmentUser.getUserId(),null);
		//添加新设备
		redisTemplate.opsForValue().set(RedisConstantUtil.PROJECT_SERIALNUMER+serialNumber, equipmentUser.getProjectId());
		Result defaultTrue = Result.getDefaultTrue();
		return defaultTrue;
	}

	@Override
	@Transactional
	public Result deletePc(EquipmentUser equipmentUser) {
		Long id = equipmentUser.getId();
		Integer projectId = equipmentUser.getProjectId();
		Long userId = equipmentUser.getUserId();
		EquipmentUser oldEquipmentUser = equipmentUserMapper.selectByPrimaryKey(id);
		if(!equipmentUser.getUserId().equals(oldEquipmentUser.getUserId()) || !equipmentUser.getProjectId().equals(oldEquipmentUser.getProjectId())) {
			return Result.getDefaultFalse();
		}
		equipmentUserMapper.deleteByPrimaryKey(equipmentUser.getId());
		String oldSerialNumber = oldEquipmentUser.getSerialNumber();
		deleteSerial(oldSerialNumber);
		Equipment selectByPrimaryKey = equipmentMapper.selectByPrimaryKey(oldSerialNumber);
		selectByPrimaryKey.setBuildingId(0L);
		equipmentMapper.updateByPrimaryKeySelective(selectByPrimaryKey);
		useTimeService.deleteSerial(userId,projectId);
		buildingService.deleteSerial(id,userId,projectId);
		Result defaultTrue = Result.getDefaultTrue();
		return defaultTrue;
	}

	public void unboundSerialNumber(Long userId) {
		EquipmentUser equipmentUser = new EquipmentUser();
		equipmentUser.setUserId(userId);
		equipmentUser.setUserStatus(1);
		List<EquipmentUser> selectOne = equipmentUserMapper.select(equipmentUser);
		if(CollectionUtils.isEmpty(selectOne)) {
			return ;
		}
		EquipmentUser equipmentUser1 = selectOne.get(0);
		equipmentUser1.setIsDefault(1);
		equipmentUserMapper.updateByPrimaryKeySelective(equipmentUser1);
	}

	@Override
	public void unboundSerialNumber(String serialNumber, Long userId) {
		EquipmentUser equipmentUser = new EquipmentUser();
		equipmentUser.setSerialNumber(serialNumber);
		equipmentUser.setUserId(userId);
		equipmentUser.setUserStatus(1);
		List<EquipmentUser> selectOne = equipmentUserMapper.select(equipmentUser);
		log.info("查询当前设备信息:{}",selectOne);
		if(CollectionUtils.isEmpty(selectOne)) {
			return ;
		}
		if(selectOne.get(0).getIsDefault()!= 1) {
			return ;
		}
		//当前设备不为默认设备
		equipmentUser.setSerialNumber(null);
		List<EquipmentUser> select = equipmentUserMapper.select(equipmentUser);
		if(CollectionUtils.isEmpty(select)) {
			return;
		}
		for (EquipmentUser equipmentUser2 : select) {
			if(!equipmentUser2.getSerialNumber().equals(serialNumber)) {
				equipmentUser2.setIsDefault(1);
				equipmentUserMapper.updateByPrimaryKeySelective(equipmentUser2);
				return;
			}
		}
	}

	@Override
	public boolean querySerialNumberJurisdiction(Long userId, Integer projectId, String serialNumber, String switchSn,Long mainUserId) {
		//return true;
		if(userId==null) 
			return false;
		if(projectId == null || projectId == 0)
			return false;
		if(StringUtils.isNotEmpty(serialNumber) ) {
			Integer serProjectId = (Integer)redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_SERIALNUMER+serialNumber);
			if(serProjectId.equals(projectId) && projectId > 0) {
				if(userId.equals(mainUserId)) {
					return true;
				}else {
					List<Integer> listRole = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES+userId);
					Long roleId = roleEquipmentMapper.queryBySerialAndRoleId(listRole,serialNumber);
					if(roleId != null && roleId > 0) {
						return true;
					}
					return false;
				}
			}else if(serProjectId.equals(projectId) && projectId.equals(0)) {
				return true;
			}
			else {
				return false;
			}
		}
		if(StringUtils.isNotEmpty(switchSn) ) {
			Long id = switchMapper.querySwitchJurisdiction(userId,projectId,switchSn);
			if(id == null || id == 0) {
				return false;
			}else {
				return true;
			}
		}
		return false;
	}

	@Override
	public Result querySerialNumberAll(PcEquipmentUserCond cond) {
		PageHelper.startPage(cond.getPage(), cond.getPageSize());
		List<Map> listData = equipmentUserMapper.querySerialByProjectId(cond);
		PageInfo pageinfo = new PageInfo<>(listData);
		listData.forEach( map ->{
			Integer buildingId = (Integer) map.get("buildingId");
			String queryAddress = buildingService.queryAddress(buildingId.longValue());
			map.put("address", queryAddress);
			String serial = (String) map.get("serialNumber");
			List<cn.meiot.entity.dto.pc.examination.SwitchDto> listSwitch = switchMapper.querySwitchBySerial(serial,cond.getUserId());
			listSwitch.forEach( sw ->{
				Object object = redisTemplate.opsForHash().get(RedisConstart.DEVICE+serial,sw.getSwitchSn() );
				if(object == null ) {
					return;
				}
				Map obj = (Map) object;
				sw.setTemp(obj.get("temp"));
			});
			map.put("listData", listSwitch);
		});
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(pageinfo);
		return defaultTrue;
	}

	@Override
	public List<String> getSerialNUmbersByProjectId(Integer projectId) {
		return equipmentUserMapper.getSerialNUmbersByProjectId(projectId);
	}

	@Override
	public Integer queryDeviceTotal(Integer projectId) {
		return equipmentUserMapper.queryDeviceTotal(projectId);
	}

	@Override
	public List<Map> querySerialVer(Integer projectId, Long userId) {
		return null;
	}

	@Override
	public List<String> querySerialByProjectId(Integer projectId) {
		return equipmentUserMapper.querySerialByProject(projectId);
	}

	@Override
	public List<PersonalSerialVo> querySerialAndMaster(Long userId) {
		List<String> listSerial = equipmentUserMapper.querySerialByUserId(userId);
		List<PersonalSerialVo> list = new ArrayList<>();
		return getPersonalSerialVos(listSerial, list);
	}

	private List<PersonalSerialVo> getPersonalSerialVos(List<String> listSerial, List<PersonalSerialVo> list) {
		PersonalSerialVo personalSerialVo = null;
		for (String serial: listSerial) {
			Result rtuserIdBySerialNumber = getRtuserIdBySerialNumber(serial);
			List<String> listUser = (List<String>) rtuserIdBySerialNumber.getData();
			if(!CollectionUtils.isEmpty(listUser)){
				Long masterId = Long.valueOf(listUser.get(0));
				//缓存取值
				Integer masterSn = (Integer) redisTemplate.opsForHash().get(RedisConstantUtil.DEVICE_MASTER_SN, serial);
				Long sn = null;
				//缓存没取到
				if(masterSn == null){
					//数据库取值
					String masterSn1 = switchMapper.getMasterSn(serial);
					//数据库取不到 就说明该设备没有开关
					if(StringUtils.isNotEmpty(masterSn1)){
						sn = Long.valueOf(masterSn1);
					}
				}else{
					sn = masterSn.longValue();
				}
				personalSerialVo = new PersonalSerialVo(serial,masterId,sn);
				list.add(personalSerialVo);
			}
		}
		return list;
	}

	@Override
	public List<PersonalSerialVo> querySerialAndMasterByProjectId(Integer projectId) {
		List<String> listSerial = equipmentUserMapper.querySerialAndMasterByProjectId(projectId);
		List<PersonalSerialVo> list = new ArrayList<>();
		return getPersonalSerialVos(listSerial, list);
	}

	@Override
	public List<String> listEquial(Long buildingId,Long userId, Long mainUserId, Integer projectId, Integer equipmentStatus) {
		List<Long> longs = buildingService.queryBuildingIds(buildingId, projectId, mainUserId, userId);
		List<String> serialList = equipmentMapper.listSerialByBuildingIds(longs);
		if(!mainUserId.equals(userId)){
			//查询当前用户的角色
			List<Integer> listRole = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES+userId);
			if(CollectionUtils.isEmpty(listRole)){
				return null;
			}
			List<String> stringList = roleService.queryEquipment(projectId, listRole);
			if(CollectionUtils.isEmpty(listRole)){
				return null;
			}
			serialList.retainAll(stringList);
		}
		//全部的话直接返回
		if(CollectionUtils.isEmpty(serialList) || equipmentStatus.equals(EquipmentStatus.ALL.status())){
			return serialList;
		}
		//查询断网设备
		if(EquipmentStatus.DISCONNECTION.status().equals(equipmentStatus)){
			List<String> stringList = networkingUtlis.deviceOnLineNumList(serialList);
			serialList.removeAll(stringList);
			return serialList;
		}
		boolean alarm = EquipmentStatus.ALARM.status().equals(equipmentStatus);
		boolean waring = EquipmentStatus.WARNING.status().equals(equipmentStatus);
		if( alarm || waring){
			List<String> stringList = messageCilent.queryFaultSerial(serialList, projectId, equipmentStatus);
			return stringList;
		}
		return serialList;
	}

	@Override
	public Map listEquialNumber(List<String> listEquial,Integer projectId) {
		Map map = new HashMap();
		int all = 0;
		int alarm = 0;
		int warning = 0;
		int disconnection =0;
		if(!CollectionUtils.isEmpty(listEquial)){
			all = listEquial.size();
			List<String> stringList = networkingUtlis.deviceOnLineNumList(listEquial);
			disconnection = all - stringList.size();
			List<String> alarmList = messageCilent.queryFaultSerial(listEquial, projectId, EquipmentStatus.ALARM.status());
			if (!CollectionUtils.isEmpty(alarmList)){
				alarm = alarmList.size();
				listEquial.removeAll(alarmList);
			}
			List<String> warningList = messageCilent.queryFaultSerial(listEquial, projectId, EquipmentStatus.WARNING.status());
			if (!CollectionUtils.isEmpty(warningList)){
				warning = warningList.size();
			}
		}
		map.put("all",all);
		map.put("alarm",alarm);
		map.put("warning",warning);
		map.put("disconnection",disconnection);
		return map;
	}

	@Override
	public Map findAddressAndName(String serialNumber, Integer projectId) {
		Map map = equipmentUserMapper.findBuildingIdAndName(serialNumber,projectId);
		if(map == null){
			return map;
		}
		map.put("address",buildingService.queryAddress(new Long(map.get("buildingId").toString())));
		return map;
	}

	@Override
	public Map findAddressAndNameBySwitchSn(Integer projectId, String switchSn,Long userId) {
		Map map = equipmentUserMapper.findAddressAndNameBySwitchSn(projectId,switchSn,userId);
		if(map == null){
			return map;
		}
		map.put("address",buildingService.queryAddress(new Long(map.get("buildingId").toString())));
		return map;
	}

	@Override
	public boolean isExistence(String serialNumber) {
		Long id = equipmentUserMapper.selectIdBySerialNuber(serialNumber);
		if(id != null){
			return true;
		}
		id = equipmentApiMapper.selectIdBySerialNuber(serialNumber);
		return id != null;
	}


	@Override
    public String findSerialName(String serialNumber, Long userId) {
		String serialName = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER, userId + "_" + serialNumber);
		if(serialName == null){
			return "";
		}
		return serialName;
    }




	public void authentication(String serialNumber,Long userId){
		Result userIds = getRtuserIdBySerialNumber(serialNumber);
		List<String> data2 = (List<String>) userIds.getData();
		if(CollectionUtils.isEmpty(data2) || !data2.contains(userId.toString())){
			throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
		}
	}

    @Override
    public String authenticationSwtichSn(String switchSn, Long userId) {
		if(StringUtils.isBlank(switchSn)){
			throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
		}
		Switch aSwitch = switchMapper.selectByPrimaryKey(switchSn);
		String serialNumber = aSwitch.getSerialNumber();
		authentication(serialNumber,userId);
		return serialNumber;
	}

    @Override
	public Map queryDefaultSerial(Integer projectId,String serialNumber) {
		Map map = equipmentUserMapper.queryDefaultSerial(projectId,serialNumber);
		if(map == null){
			throw new MyServiceException(ResultCodeEnum.MAIN_UNBIND.getCode(),ResultCodeEnum.MAIN_UNBIND.getMsg());
		}
		Long buildingId = Long.valueOf(map.get("buildingId").toString());
        map.put("serialNumber",serialNumber);
		Integer switchCount = (Integer) map.get("switchCount");
		map.put("totalPower",/*switchCount **/ 5000);
		String address = buildingService.queryAddress(buildingId);
		map.put("address",address);
		Integer networkingStatus = networkingUtlis.getNetworkingStatus(null, serialNumber);
		map.put("isOnline",networkingStatus);
		Result realtime = equipmentService.realtime(serialNumber);
		Map data = (Map) realtime.getData();
		map.putAll(data);
		return map;
	}


}
