package cn.meiot.service.impl;

import cn.meiot.client.CommunicationClient;
import cn.meiot.constart.TableConstart;
import cn.meiot.dao.PowerMapper;
import cn.meiot.dao.SwitchMapper;
import cn.meiot.dao.TimerModeMapper;
import cn.meiot.entity.db.PowerAppUser;
import cn.meiot.entity.db.TimerMode;
import cn.meiot.entity.equipment.BaseEntity;
import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.Timeswitch;
import cn.meiot.entity.equipment.sckgds.Sckgds;
import cn.meiot.entity.equipment2.TimeEntity;
import cn.meiot.entity.equipment2.time.SetTimer;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.*;
import cn.meiot.utils.MqttUtil;
import cn.meiot.utils.NetworkingUtlis;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utlis.TimeUtlis;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lingzhiying
 * @title: TimerModeServiceImpl.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月2日
 */
@Service
@Slf4j
public class TimerModeServiceImpl implements TimerModeService{

	@Autowired
	private TimerModeMapper timerModeMapper;
	
	@Autowired
	private SwitchMapper switchMapper;
	
	@Autowired
	private CommunicationClient communicationClient;

	@Autowired
	private PowerMapper powerMapper;

	@Autowired
	private PowerService powerService;

	@Autowired
	private EquipmentUserService equipmentUserService;

	@Autowired
    private RedisTemplate redisTemplate;

	@Autowired
	private NetworkingUtlis networkingUtlis;

	@Autowired
	private SwitchService switchService;

	@Autowired
	private EquipmentService equipmentService;

	@Override
	public Result querySn(String switchSn) {
		List<TimerMode> listTimer =  timerModeMapper.queryTimerMode(switchSn);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(listTimer);
		return defaultTrue;
	}

	@Override
	@Transactional
	public Result insert(TimerMode timerMode) {
		Set<PowerAppUser> powerAppUserList = timerMode.getPowerAppUserList();
		String serialNumber = timerMode.getSerialNumber();
		//修改失效线路
		updateInvalidSwitch(powerAppUserList,serialNumber,null);
		//添加数据
		int row = timerModeMapper.insertSelective(timerMode);
		powerMapper.insertTableUser(timerMode.getId(),powerAppUserList,serialNumber, TableConstart.TIME_MODE);
		if(timerMode.getIsSwitch()) {
			sendTimer(timerMode,timerMode.getIsSwitch());
		}
		return row>0?Result.getDefaultTrue():Result.getDefaultFalse();
	}

	@Override
	@Transactional
	public Result delete(Long id,Long userId) {
		TimerMode selectByPrimaryKey = timerModeMapper.selectByPrimaryKey(id);
		String serialNumber = selectByPrimaryKey.getSerialNumber();
		//鉴权
		equipmentUserService.authentication(serialNumber,userId);
		//查处之前想关联的开关.
		Set<PowerAppUser> powerAppUserList = timerModeMapper.querySwitchById(id);
		selectByPrimaryKey.setPowerAppUserList(powerAppUserList);
		//之前为开启状态就要关掉定时
		if(selectByPrimaryKey.getIsSwitch()) {
			//发送消息关闭设备
			sendTimer(selectByPrimaryKey,false);
		}
		int row = timerModeMapper.deleteByPrimaryKey(id);
		powerMapper.deleteByTableUser(id,TableConstart.TIME_MODE,null);
		return row>0?Result.getDefaultTrue():Result.getDefaultFalse();
	}

	@Override
	@Transactional
	public Result update(TimerMode timerMode) {
		Integer status = 1;
		Set<PowerAppUser> powerAppUserList = timerMode.getPowerAppUserList();
		String serialNumber = timerMode.getSerialNumber();
		Long id = timerMode.getId();
		//修改失效线路
		updateInvalidSwitch(powerAppUserList,serialNumber,id);

		TimerMode selectByPrimaryKey = timerModeMapper.selectByPrimaryKey(id);
		Boolean isSwitch = timerMode.getIsSwitch();
		boolean flag = !selectByPrimaryKey.getIsSwitch() && isSwitch;
		if(flag) {
			//修改之前的开关  为关闭状态
			Set<PowerAppUser> oldPowerAppUserList = timerModeMapper.querySwitchById(id);
			selectByPrimaryKey.setPowerAppUserList(oldPowerAppUserList);
			sendTimer(selectByPrimaryKey,false);
		}
		if(selectByPrimaryKey.getIsSwitch() || isSwitch) {
			sendTimer(timerMode,isSwitch);
		}
		//删除生效的开关
		powerMapper.deleteByTableUser(id,TableConstart.TIME_MODE,status);
		int row = timerModeMapper.updateByPrimaryKeySelective(timerMode);
		//再添加进去
		powerMapper.insertTableUser(timerMode.getId(),timerMode.getPowerAppUserList(),timerMode.getSerialNumber(), TableConstart.TIME_MODE);
		return row>0?Result.getDefaultTrue():Result.getDefaultFalse();
	}

	@Override
	@Transactional
	public Result isSwitch(TimerMode timerMode) {
		Long id = timerMode.getId();
		Boolean isSwitch = timerMode.getIsSwitch();
		timerMode = timerModeMapper.selectByPrimaryKey(id);
		timerMode.setIsSwitch(isSwitch);
        String serialNumber = timerMode.getSerialNumber();
        Set<PowerAppUser> powerAppUserList = timerModeMapper.querySwitchById(id);
        //修改失效线路
		List<Long> ids = timerModeMapper.queryIdBySerial(serialNumber,1);
		ids.remove(id);
		timerModeMapper.updateInvalidSwitch(powerAppUserList,ids);

		//修改开关状态
        timerModeMapper.updateByPrimaryKeySelective(timerMode);
        timerMode.setPowerAppUserList(powerAppUserList);
		sendTimer(timerMode,timerMode.getIsSwitch());
		return Result.getDefaultTrue();
	}

	@Override
	public Result querySerial(String serialNumber, Long userId) {
		List<TimerMode> mapList = timerModeMapper.querySerial(serialNumber);
        Map<String,String> map = new HashMap<>();
        for (TimerMode timerMode: mapList) {
        	//把名字补上，少关联几张表
			Set<PowerAppUser> powerAppUserList = timerMode.getPowerAppUserList();
			Iterator<PowerAppUser> it = powerAppUserList.iterator();
			for (int i = 0; i < powerAppUserList.size(); i++) {
				PowerAppUser next = it.next();
				String switchSn = next.getSwitchSn();
				String name = map.get(switchSn);
				if (StringUtils.isEmpty(name)) {
					name = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SWITCH, userId +"_"+ switchSn);
					map.put(switchSn, name);
				}
				next.setName(name);
				//如果为失效线路 则添加到失效字段中
				if(next.getStatus().equals(0)){
					timerMode.getInvalidPowerAppUserList().add(next);
					it.remove();
					i--;
				}
			}
        }
		return Result.OK(mapList);
	}

	/**
	 * 先修改正常线路为失效线路,必须放在 增改执行操作前面
	 * @param powerAppUserList
	 * @param id
	 */
	@Override
	public void updateInvalidSwitch(Set<PowerAppUser> powerAppUserList, String serialNumber,Long id) {
		if(id != null){
			timerModeMapper.deleteByIdAndSwitch(id,powerAppUserList);
		}
		List<Long> ids = timerModeMapper.queryIdBySerial(serialNumber,1);
		if(CollectionUtils.isEmpty(ids)){
			return;
		}
		timerModeMapper.updateInvalidSwitch(powerAppUserList,ids);
	}

	@Override
	public Result load(Long id) {
		TimerMode selectByPrimaryKey = timerModeMapper.selectByPrimaryKey(id);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(selectByPrimaryKey);
		return defaultTrue;
	}

	@Override
	public Result querySwitchSn(String switchSn,final Long userId) {
		List<Map> mapList = timerModeMapper.querySn(switchSn);
		mapList.forEach(map->{
			Integer id = Integer.valueOf(map.get("id").toString());
			String serialNumber = (String) map.get("serialNumber");
			List<Map> result = powerService.queryById(id,userId,serialNumber, TableConstart.TIME_MODE);
			List<Map> selection = result.stream().filter(map1 -> map1.get("selection")!= null && !map1.get("selection").equals(0)).collect(Collectors.toList());
			map.put("powerAppUserList",selection);
			//timerModeMapper.queryInvalidSwitch();
			//map.put("invalidSwtich");
		});
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(mapList);
		return defaultTrue;
	}



	private void sendTimer(TimerMode timerMode,boolean isSwitch) {
		String serialNumber = timerMode.getSerialNumber();
		networkingUtlis.isNetworkingThrowException(serialNumber);
		//查询设备协议

		List<Timeswitch> timeswitch = JSON.parseArray(timerMode.getTimes(), Timeswitch.class);
		if(!CollectionUtils.isEmpty(timeswitch)) {
			Timeswitch timeswitch2 = timeswitch.get(0);
			if(!isSwitch) {
				timeswitch2.setMode(0);
			}else {
				if(timeswitch2.getMode() == 3 ) {
					//结束时间加一天
					timeswitch2.setEnd(timeswitch2.getStart()+86400L);
				}else if(timeswitch2.getMode() == 4) {
					//timeswitch2.setStart(timeswitch2.getEnd());
					timeswitch2.setEnd(timeswitch2.getStart()+86400L);
				}
				else if(timeswitch2.getMode() == 2){
					//如果没有开始时间，则添加当前时间为默认开始时间
					if(timeswitch2.getStart() == null) {
						timeswitch2.setStart(System.currentTimeMillis()/1000L);
					}
				}else {
					timeswitch2.setStart(TimeUtlis.getTime2(timeswitch2.getStart(), timeswitch2.getOn()));
					timeswitch2.setEnd(TimeUtlis.getTime2(timeswitch2.getEnd(), timeswitch2.getOff()));
				}
			}
			timeswitch.remove(0);
			timeswitch.add(timeswitch2);
		}
		Integer integer = equipmentService.queryAgreementVersion(serialNumber);

		if(integer.equals(2)){
			Timeswitch timeswitch1 = timeswitch.get(0);
			List<SetTimer> list = new ArrayList<>();
			for (PowerAppUser powerAppUser: timerMode.getPowerAppUserList()) {
				SetTimer setTimer = new SetTimer(timeswitch1);
				setTimer.setSid(Long.valueOf(powerAppUser.getSwitchSn()));
				list.add(setTimer);
			}
			TimeEntity timeEntity = new TimeEntity(list);
			String jsonString = JSON.toJSONString(timeEntity, true);
			switchService.sendMessage(serialNumber,jsonString);
			return;
		}

		Sckgds sckgds = null;
		Device device = null;
		for (PowerAppUser powerAppUser: timerMode.getPowerAppUserList()) {
			List<Sckgds> listsckgds = new ArrayList<Sckgds>();
			//拼接数据
			sckgds = new Sckgds();
			device = new Device();
			device.setId(new Long(powerAppUser.getSwitchSn()));
			device.setIndex(new Integer(powerAppUser.getIndex()));
			sckgds.setDevice(device);
			sckgds.setTimer(timeswitch);
			listsckgds.add(sckgds);
			BaseEntity<Sckgds> be = new BaseEntity<>(timerMode.getSerialNumber(), MqttUtil.CMD_03, listsckgds);
			String jsonString = JSON.toJSONString(be, true);
			//发送给设备通信中心控制开关
			switchService.sendMessage(serialNumber,jsonString);
		}
	}
}
