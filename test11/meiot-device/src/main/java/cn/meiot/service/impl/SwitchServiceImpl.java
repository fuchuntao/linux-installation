package cn.meiot.service.impl;

import cn.meiot.client.CommunicationClient;
import cn.meiot.client.MessageCilent;
import cn.meiot.client.StatisticsClient;
import cn.meiot.constart.ProjectConstart;
import cn.meiot.constart.RedisConstart;
import cn.meiot.dao.*;
import cn.meiot.entity.db.Equipment;
import cn.meiot.entity.db.Switch;
import cn.meiot.entity.db.SwitchName;
import cn.meiot.entity.dto.SwitchRespDto;
import cn.meiot.entity.dto.UpdateName;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.examination.SerialDto;
import cn.meiot.entity.dto.pc.examination.SwitchDto;
import cn.meiot.entity.dto.sw.SendSwitch;
import cn.meiot.entity.equipment.*;
import cn.meiot.entity.equipment.sckgzt.Sckgzt;
import cn.meiot.entity.equipment2.BaseEntity2;
import cn.meiot.entity.equipment2.ControlEntity;
import cn.meiot.entity.equipment2.WarnEntity;
import cn.meiot.entity.equipment2.control.Crlpower;
import cn.meiot.entity.equipment2.warn.SetWarnValue;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SerialNumberMasterVo;
import cn.meiot.enums.EquipmentStatus;
import cn.meiot.service.EquipmentService;
import cn.meiot.service.EquipmentUserService;
import cn.meiot.service.SwitchService;
import cn.meiot.utils.*;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author lingzhiying
 * @title: SwitchServiceImpl.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月3日
 */
@Service
@Slf4j
public class SwitchServiceImpl implements SwitchService{

	@Autowired
	private EquipmentUserService equipmentUserService;
	
	@Autowired
	private SwitchNameMapper switchNameMapper;
	
	@Autowired
	private SwitchMapper switchMapper;
	
	@Autowired
	private EquipmentUserMapper equipmentUserMapper;
	
	@Autowired
	private EquipmentMapper equipmentMapper;
	
	@Autowired
	private CommunicationClient communicationClient;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private StatisticsClient statisticsClient;

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private NetworkingUtlis networkingUtlis;

	@Autowired
	private EquipmentService equipmentService;

	@Autowired
	private MessageCilent messageCilent;
	
	@Override
	public Result updateName(UpdateName updateName) {
		if(StringUtils.isBlank(updateName.getName())) {
			return Result.getDefaultFalse();
		}
        String switchSn = updateName.getSwitchSn();
        Integer parentIndex = updateName.getParentIndex();
        if(parentIndex != null){
			String serialNumber = updateName.getSerialNumber();
			if(0 == parentIndex){
                updateMainSwitch(serialNumber,switchSn);
            }else{
                updateSonSwitch(serialNumber,switchSn);
            }
        }
        if(updateName.getId() != null) {
			SwitchName sn = switchNameMapper.selectByPrimaryKey(updateName.getId());
			sn.setSwitchType(updateName.getSwitchType());
			//是否为他自己的设备
			if(updateName.getUserId().equals(sn.getUserId())) {
				if(!sn.getName().equals(updateName.getName())) {
					sn.setName(updateName.getName());
					redisTemplate.opsForHash().put(RedisConstantUtil.NIKNAME_SWITCH, updateName.getUserId()+"_"+switchSn, updateName.getName());
				}
				switchNameMapper.updateByPrimaryKeySelective(sn);
                return Result.getDefaultTrue();
			}else if(sn != null){
				return Result.getDefaultTrue();
			}
		}
		SwitchName sn = new SwitchName();
		sn.setUserId(updateName.getUserId());
		sn.setSwitchSn(switchSn);
		sn.setName(updateName.getName());
		sn.setSwitchType(updateName.getSwitchType());
		switchNameMapper.insertSelective(sn);
		redisTemplate.opsForHash().put(RedisConstantUtil.NIKNAME_SWITCH, updateName.getUserId()+"_"+updateName.getSwitchSn(), updateName.getName());
		return Result.getDefaultTrue();
	}



	@Override
	public Result sendSwitch(SendSwitch sendSwitch) {
		String serialNumber = sendSwitch.getSerialNumber();
		Integer integer = equipmentService.queryAgreementVersion(serialNumber);
		if(integer.equals(2)){
			//List<Crlpower> crlpowerList = new ArrayList<>();
			Long switchSn = sendSwitch.getSwitchSn();
			Integer status = sendSwitch.getStatus();
			Crlpower crlpower = new Crlpower(switchSn,status);
			ControlEntity controlEntity = new ControlEntity(crlpower);
			return baseEntityJson2(controlEntity,serialNumber);
		}else{
			List<Sckgzt> list = new ArrayList<Sckgzt>();
			list.add(switchEntity(sendSwitch));
			Result baseEntityJson = baseEntityJson(serialNumber, list);
			return baseEntityJson;
		}
	}

	public Result baseEntityJson2(BaseEntity2 controlEntity, String serialNumber){
		String jsonString = JSON.toJSONString(controlEntity, true);
		Result result = sendMessage(serialNumber, jsonString);
		return result;
	}

    @Override
    public void updateMainSwitch(String serialNumber, String switchSn) {
        switchMapper.updateSonSwitch(serialNumber,null);
        switchMapper.updateMainSwitch(serialNumber,switchSn);
    }

    @Override
    public void updateSonSwitch(String serialNumber, String switchSn) {
        switchMapper.updateSonSwitch(serialNumber,switchSn);
    }

    @Override
	public Result sendSwitchAll(SendSwitch sendSwitch,List<String> switchList) {
		String serialNumber = sendSwitch.getSerialNumber();
		Integer integer = equipmentService.queryAgreementVersion(serialNumber);
		//如果是二代设备
		List<Sckgzt> list = new ArrayList<Sckgzt>();
		List<Crlpower> list2 = new ArrayList<>();

		//开启状态
		Integer status = sendSwitch.getStatus();
		equipmentUserMapper.updateSerialNumberByStatus(sendSwitch);
		List<SendSwitch> listSwitch = switchMapper.querySwitchBySerialNumber(sendSwitch.getSerialNumber());
		for (SendSwitch sendSwitch2 : listSwitch) {
			String switchSn = sendSwitch2.getSwitchSn().toString();
			switchList.add(switchSn);
			//updateRedisLoadMax(sendSwitch.getSerialNumber(),switchSn , sendSwitch.getLoadMax());
			sendSwitch2.setStatus(status);
			list.add(switchEntity(sendSwitch2));
			//构建二代数据
			list2.add(new Crlpower(sendSwitch2.getSwitchSn(),status));
		}
		if(integer != null && integer.equals(2)){
			ControlEntity controlEntity = new ControlEntity(list2);
			return baseEntityJson2(controlEntity,serialNumber);
		}
		Result baseEntityJson = baseEntityJson(serialNumber, list);
		return baseEntityJson;
	}

	@Override
	public Result sendSwitchLoadmaxAll(SendSwitch map) {
		List<String> switchList = new ArrayList<>();
		String serialNumber = map.getSerialNumber();
		Integer loadMax = map.getLoadMax();
		//该状态为 功率的状态
		Integer status = map.getStatus();
		//查出数据 修改
		List<SendSwitch> listSwitch = switchMapper.querySwitchAllBySerialNumber(serialNumber);
		Integer integer = equipmentService.queryAgreementVersion(serialNumber);
		Long loadMax2 = ConstantsUtil.MAX_LOAD;
		if(status.equals(1)){
			loadMax2 = loadMax.longValue();
		}
		List<SetWarnValue> list2 = new ArrayList<>();
		for (SendSwitch sendSwitch2 : listSwitch) {
			String switchSn = sendSwitch2.getSwitchSn().toString();
			switchList.add(switchSn);
			updateRedisLoadMax(serialNumber,switchSn , loadMax,status);
			//构建二代条件
			SetWarnValue setWarnValue = new SetWarnValue(loadMax2,Long.valueOf(switchSn));
			list2.add(setWarnValue);
		}
		if(integer.equals(2)){
			WarnEntity warnEntity = new WarnEntity(list2);
			baseEntityJson2(warnEntity,serialNumber);
			return Result.OK(switchList);
		}
		//必须设置为null 否则会对开关进行控制  switchEntity该方法为公用方法
		map.setStatus(null);
		List<Sckgzt> list = new ArrayList<>();
		list.add(switchEntity(map));
		baseEntityJson(serialNumber, list);
		return Result.OK(switchList);
	}

    @Override
    public Result switchStatus(String serialNumber,Long userId) {
		List<Map> listSwitchRespDto = switchMapper.querySwitchBySerialAndUserId(userId,serialNumber);
		String str1 = RedisConstart.DEVICE+serialNumber;
		for (Map map : listSwitchRespDto) {
			Object status = redisTemplate.opsForHash().get(str1,map.get("switchSn") );
			map.put("loadMax", 0);
			map.put("status",0);
			if(status == null) {
				continue;
			}
			Map status3 = (Map)status;
			map.put("loadMax", SwitchUtil.getLoadMax(status3));
			map.put("status",SwitchUtil.getLoadMaxStatus(status3));
		}
		return Result.OK(listSwitchRespDto);
    }



	//设置最大功率
	@Override
	public Result sendSwitchLoadmax(SendSwitch sendSwitch) {
		//查询设备号
		Switch selectByPrimaryKey = switchMapper.selectByPrimaryKey(sendSwitch.getSwitchSn().toString());
		String serialNumber = selectByPrimaryKey.getSerialNumber();
		sendSwitch.setSerialNumber(serialNumber);
		sendSwitch.setSwitchIndex(selectByPrimaryKey.getSwitchIndex());
		//修改功率
		updateRedisLoadMax(serialNumber, sendSwitch.getSwitchSn().toString(), sendSwitch.getLoadMax());
		List<Sckgzt> list = new ArrayList<>();
		list.add(switchEntity(sendSwitch));
		Result baseEntityJson = baseEntityJson(serialNumber, list);
		return baseEntityJson;
	}

	@Override
	public Result sendSwitchLoadmaxPersonal(SendSwitch sendSwitch) {
		//查询设备号
		Switch selectByPrimaryKey = switchMapper.selectByPrimaryKey(sendSwitch.getSwitchSn().toString());
		String serialNumber = selectByPrimaryKey.getSerialNumber();
		Integer integer = equipmentService.queryAgreementVersion(serialNumber);
		sendSwitch.setSerialNumber(serialNumber);
		sendSwitch.setSwitchIndex(selectByPrimaryKey.getSwitchIndex());
		//修改功率
		updateRedisLoadMax(serialNumber, sendSwitch.getSwitchSn().toString(), sendSwitch.getLoadMax(),sendSwitch.getStatus());
		SetWarnValue setWarnValue = new SetWarnValue();

		setWarnValue.setSid(Long.valueOf(selectByPrimaryKey.getSwitchSn()));
		WarnEntity warnEntity = new WarnEntity(setWarnValue);
		if(sendSwitch.getStatus().equals(0)){
			sendSwitch.setLoadMax(0);
			setWarnValue.setMaxload(100000L);
		}else{
			setWarnValue.setMaxload(sendSwitch.getLoadMax().longValue());
		}
		if(integer.equals(2)){
			Result result = baseEntityJson2(warnEntity, serialNumber);
			return Result.OK(serialNumber);
		}
		sendSwitch.setStatus(null);
		List<Sckgzt> list = new ArrayList<>();
		list.add(switchEntity(sendSwitch));
		Result baseEntityJson = baseEntityJson(serialNumber, list);
		return Result.OK(serialNumber);
	}
	
	public void updateRedisLoadMax(String serialNumber,String sn,Integer loadMax) {
		networkingUtlis.isNetworkingThrowException(serialNumber);
		Map obj = (Map)redisTemplate.opsForHash().get(RedisConstart.DEVICE+serialNumber,sn );
		obj.put("loadmax", loadMax);
		redisTemplate.opsForHash().put(RedisConstart.DEVICE+serialNumber, sn, obj);
	}

	@Override
	public void updateRedisLoadMax(String serialNumber, String sn, Integer loadMax, Integer status) {
		networkingUtlis.isNetworkingThrowException(serialNumber);
		Map obj = (Map)redisTemplate.opsForHash().get(RedisConstart.DEVICE+serialNumber,sn );
		if(status.equals(1)){
			obj.put("loadmax", loadMax);
		}else{
			obj.put("offLoadMax", loadMax);
		}
		obj.put("loadMaxStatus", status);
		redisTemplate.opsForHash().put(RedisConstart.DEVICE+serialNumber, sn, obj);
	}

	//控制开关 的打开与关闭
	public Result baseEntityJson(String serialNumber, List<Sckgzt> list) {
        networkingUtlis.isNetworkingThrowException(serialNumber);
		BaseEntity<Sckgzt> be = new BaseEntity<Sckgzt>(serialNumber, MqttUtil.CMD_03, list);
		String jsonString = JSON.toJSONString(be, true);
		//调用通信中心 cmd03操作
		Result result = sendMessage(serialNumber, jsonString);
		result.setData(be.getMessageid()+"");
		return result;
	}

	@Override
	public Result sendMessage(String serialNumber,String jsonString){
		Map map = new HashMap();
		map.put("serialNumber", serialNumber);
		map.put("json", jsonString);
		log.info("map"+map);
		Result sendSwitch1 = communicationClient.sendSwitch(map);
		return sendSwitch1;
	}



	@Override
	public void authentication(Long userId, String switchSn) {
		Switch aSwitch = switchMapper.selectByPrimaryKey(switchSn);
		String serialNumber = aSwitch.getSerialNumber();
		equipmentUserService.authentication(serialNumber,userId);
	}

	private Sckgzt switchEntity(SendSwitch sendSwitch) {
		Device d = new Device();
		d.setId(sendSwitch.getSwitchSn());
		d.setIndex(sendSwitch.getIndex());
		Status s = new Status();
		s.setSwitch(sendSwitch.getStatus());
		s.setLoadmax(sendSwitch.getLoadMax());
		Sckgzt sckgzt = new Sckgzt();
		sckgzt.setDevice(d);
		sckgzt.setStatus(s);
		return sckgzt;
	}

	@Override
	public Result getMasterIndex(String serialNumber) {
		Object obj = redisTemplate.opsForHash().get(RedisConstantUtil.DEVICE_MASTER_INDEX, serialNumber);
		Result defaultTrue = Result.getDefaultTrue();
		if(obj == null || "".equals(obj.toString())) {
			Integer index = switchMapper.getMasterIndex(serialNumber);
			defaultTrue.setData(index);
		}else {
			defaultTrue.setData(obj);
		}
		return defaultTrue;
	}

	@Override
	public List<SwitchRespDto> querySwitchStatus(String serialNumber, Long mainUserId,Integer projectId,Integer deleted) {
		String str1 = RedisConstart.DEVICE+serialNumber;
		List<SwitchRespDto> querySwitchIndexByNumber = switchMapper.querySwitchIndexByNumber(mainUserId, serialNumber,deleted);
		Integer networkingStatus = networkingUtlis.getNetworkingStatus("", serialNumber);
		for(SwitchRespDto switchRespDto : querySwitchIndexByNumber) {
			if(switchRespDto.getTypeId() == null || switchRespDto.getTypeId().equals(0L)){
				switchRespDto.setTypeName(ProjectConstart.OTHER);
			}
			Object status = redisTemplate.opsForHash().get(str1,switchRespDto.getSwitchSn() );
			if(status == null) {
				continue;
			}
			//新增开关故障状态
			String switchSn = switchRespDto.getSwitchSn();
			Map map = messageCilent.queryFaultSwitch(serialNumber, switchSn, projectId);
			//如果map为null 则为空
			if(map != null){
				Integer alarmCount = (Integer) map.get(EquipmentStatus.ALARM);
				//如果告警为空 则找预警数量
				if(alarmCount == null){
					Integer warningNum = (Integer) map.get(EquipmentStatus.WARNING);
					if(warningNum != null){
						switchRespDto.setFaultNum(warningNum);
						switchRespDto.setSwitchStatus(EquipmentStatus.WARNING.status());
					}
				}else{
					switchRespDto.setFaultNum(alarmCount);
					switchRespDto.setSwitchStatus(EquipmentStatus.ALARM.status());
				}
			}
			/*Integer alarmNum = (Integer) redisTemplate.opsForHash().get(ConstantsUtil.ALARM_SERAIL + serialNumber, switchSn);
			if(alarmNum == null || 0 == alarmNum){
				Integer warningNum = (Integer) redisTemplate.opsForHash().get(ConstantsUtil.WARNING_SERAIL + serialNumber, switchSn);
				if(warningNum != null && 0 != warningNum){
					switchRespDto.setSwitchStatus(EquipmentStatus.WARNING.status());
				}
			}else{
				switchRespDto.setSwitchStatus(EquipmentStatus.ALARM.status());
			}*/
			//新增开关电量
			Map<String, Object> pcMonthAndDayStatistics = statisticsClient.pcMonthAndDayStatistics(serialNumber, Long.valueOf(switchRespDto.getSwitchSn()), projectId);
			switchRespDto.setMonthElectric(pcMonthAndDayStatistics.get("month"));
			switchRespDto.setDayElectric(pcMonthAndDayStatistics.get("day"));
			//新增故障状态
			Object obj = redisTemplate.opsForValue().get(RedisConstantUtil.FAULT_SERIALNUMER + "_" +switchSn);
	    	if(null == obj) {
	    		switchRespDto.setCloseStatus(0);
	    	}else {
	    		switchRespDto.setCloseStatus((Integer)obj);
	    	}
			log.info("获取数据:{}",status);
			BigDecimal current = BigDecimal.ZERO;
			BigDecimal voltage = BigDecimal.ZERO;
			BigDecimal power = BigDecimal.ZERO;
			Map status3 = (Map)status;
			//额定功率
			//String loadmax = status3.get("loadmax")!= null?(String)status3.get("loadmax"):"0";
			//开光状态
			//String status2 = status3.get("switch")!=null ?(String)status3.get("switch"):"0";
			//温度
			//String temp = status3.get("temp")!=null ?(String)status3.get("temp"):"0";
			//漏电
			switchRespDto.setLeakage(status3.get("leakage")!=null?(Integer)status3.get("leakage"):0);
			switchRespDto.setLoadmax(status3.get("loadmax")!= null?(Integer)status3.get("loadmax"):0);
			switchRespDto.setStatus(status3.get("switch")!=null ?(Integer) status3.get("switch"):0);
			if(networkingStatus.equals(0)) {
				switchRespDto.setStatus(0);
			}
			switchRespDto.setTemp(status3.get("temp")!=null ?(Integer)status3.get("temp"):0);
			//电流
			List list = status3.get("current")!=null?(List) status3.get("current") :new ArrayList();
			//电压
			List list2 = status3.get("voltage")!=null?(List) status3.get("voltage") :new ArrayList();
			if(!CollectionUtils.isEmpty(list)) {
				String string = list.get(0).toString();
				current = new BigDecimal(string).divide(new BigDecimal(1000),1,BigDecimal.ROUND_HALF_UP);
				switchRespDto.setCurrent(current);
			}
			if(!CollectionUtils.isEmpty(list2)) {
				String string = list2.get(0).toString();
				voltage = new BigDecimal(string).divide(new BigDecimal(1000),0,BigDecimal.ROUND_HALF_UP);
				switchRespDto.setVoltage(voltage);
			}
			Object power1 = status3.get("power");
			if(power1 != null){
				power = new BigDecimal(power1.toString());
			}else {
				power = current.multiply(voltage).setScale(1);
			}switchRespDto.setPower(power);
			//是否故障
		}
		return querySwitchIndexByNumber;
	}

	@Override
	public Result sendPcSwitchLoadmax(List<SendSwitch> listSendSwitch) {
		for (SendSwitch sendSwitch : listSendSwitch) {
			sendSwitchLoadmax(sendSwitch);
		}
		return  Result.getDefaultTrue();
	}



	@Override
	public Result sendPcSwitchLoadmaxAll(String serialNumber,Integer loadMax) {
		Equipment selectByPrimaryKey = equipmentMapper.selectByPrimaryKey(serialNumber);
		selectByPrimaryKey.setLoadmax(loadMax);
		equipmentMapper.updateByPrimaryKeySelective(selectByPrimaryKey);
		List<SendSwitch>listSendSwitch = switchMapper.querySerialNumber(serialNumber);
		Integer integer = equipmentService.queryAgreementVersion(serialNumber);
		List<SetWarnValue> list2 = new ArrayList<>();
		for (SendSwitch sendSwitch : listSendSwitch) {
			sendSwitch.setLoadMax(loadMax);
			sendSwitch.setSerialNumber(serialNumber);
			List<Sckgzt> list = new ArrayList<Sckgzt>();
			list.add(switchEntity(sendSwitch));
			SetWarnValue setWarnValue = new SetWarnValue(loadMax.longValue(),sendSwitch.getSwitchSn());
			list2.add(setWarnValue);
			if(integer.equals(1)){
				baseEntityJson(serialNumber, list);
			}
		}
		if(integer.equals(2)) {
			WarnEntity warnEntity = new WarnEntity(list2);
			baseEntityJson2(warnEntity, serialNumber);
			return Result.OK();
		}
		return Result.getDefaultTrue();

	}

	@Override
	public List<SwitchRespDto> querySwitchDetails(String serialNumber, Long mainUserId,Integer projectId) {
		// TODO Auto-generated method stub
		List<SwitchRespDto> querySwitchStatus = querySwitchStatus(serialNumber,mainUserId,projectId,0);
		for(SwitchRespDto switchRespDto:querySwitchStatus) {
			//添加
			//Map<String, BigDecimal> queryDayAndMonth = statisticsClient.queryDayAndMonth(serialNumber, switchRespDto.getSwitchSn(), switchRespDto.getSwitchIndex());
			Map<String, Object> pcMonthAndDayStatistics = statisticsClient.pcMonthAndDayStatistics(serialNumber, Long.valueOf(switchRespDto.getSwitchSn()), projectId);
			switchRespDto.setMonthElectric(pcMonthAndDayStatistics.get("month"));
			switchRespDto.setDayElectric(pcMonthAndDayStatistics.get("day"));
		}
		return querySwitchStatus;
	}

	@Override
	public Result querySwitchColseProject(PcEquipmentUserCond cond) {
		Integer status = cond.getStatus();
		List<SerialDto> listSer = switchMapper.querySerialNumberAndSwitch(cond);
		log.info("获取设备数据:{}",listSer);
		List<Map> listMap = new LinkedList<Map>();
		for (SerialDto ser : listSer) {
			String serialNumber = ser.getSerialNumber();
			List<SwitchDto> listSwitch = ser.getListSwitch();
			if(CollectionUtils.isEmpty(listSwitch)) {
				continue;
			}
			List<Sckgzt> list = new ArrayList<Sckgzt>();
			for (SwitchDto switchDto : listSwitch) {
				Integer obj = (Integer)redisTemplate.opsForValue().get(RedisConstantUtil.FAULT_SERIALNUMER + "_" +switchDto.getSwitchSn());
				//log.info("获取开关合闸"+switchDto.getSwitchSn()+"的状态:{}",obj);
				if(null != obj && obj.equals(8)) {
		    		Map map = new HashMap();
		    		String name = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER, cond.getUserId()+"_"+serialNumber);
		    		map.put("serialName", name);
		    		map.put("serialNumber", serialNumber);
		    		listMap.add(map);
		    		break ;
		    	}
			}
		}
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(listMap);
		return defaultTrue;
	}
	
	@Override
	public Result sendSwitchProject(PcEquipmentUserCond cond) {
		Integer status = cond.getStatus();
		List<SerialDto> listSer = switchMapper.querySerialNumberAndSwitch(cond);
		List<Crlpower> crlpowerList = null;
		for (SerialDto ser : listSer) {
			String serialNumber = ser.getSerialNumber();
			List<SwitchDto> listSwitch = ser.getListSwitch();
			if(CollectionUtils.isEmpty(listSwitch)) {
				continue;
			}
			crlpowerList = new ArrayList<>();
			List<Sckgzt> list = new ArrayList<Sckgzt>();

			//构建一代协议
			SendSwitch sendSwitch = new SendSwitch();
			sendSwitch.setIndex(ProjectConstart.SWITCH_INDEX_ALL);
			sendSwitch.setStatus(status);
			Sckgzt switchEntity = switchEntity(sendSwitch);
			list.add(switchEntity);

			//二代协议
			crlpowerList.add(new Crlpower(0L,status));
			if(ser.getType().equals(2)){
				ControlEntity controlEntity = new ControlEntity(crlpowerList);
				baseEntityJson2(controlEntity,serialNumber);
				continue;
			}
			BaseEntity<Sckgzt> be = new BaseEntity<Sckgzt>(serialNumber, MqttUtil.CMD_03, list);
			String jsonString = JSON.toJSONString(be, true);
			Map map = new HashMap();
			map.put("serialNumber", serialNumber);
			map.put("json", jsonString);
			Result sendSwitch1 = communicationClient.sendSwitch(map);
		}
		return Result.getDefaultTrue();
	}

	@Override
	public List<SerialNumberMasterVo> queryMasterIndexBySerialNUmber(List<String> serialNumbers) {
		return switchMapper.queryMasterIndexBySerialNUmber(serialNumbers);
	}

	@Override
	public Result querySwitch(String serialNumber, Long mainUserId) {
		List<SwitchDto> listSwitch = switchMapper.querySwitchBySerial(serialNumber,mainUserId);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(listSwitch);
		return defaultTrue;
	}

	@Override
	public List<Map> querySwitchByBuilding(Long buildingId, Integer projectId) {
		return switchMapper.querySwitchByBuilding(buildingId,projectId);
	}

	@Override
	public void resetSwitch(String serialNumber) {
		Equipment selectByPrimaryKey = equipmentMapper.selectByPrimaryKey(serialNumber);
		selectByPrimaryKey.setExaminationStatus(0);
		selectByPrimaryKey.setExaminationTime("");
		equipmentMapper.updateByPrimaryKeySelective(selectByPrimaryKey);
		Switch sw = new Switch();
		sw.setSerialNumber(serialNumber);
		List<Switch> select = switchMapper.select(sw);
		Device device = null;
		List<ResetSwitch> listData = new ArrayList<ResetSwitch>();
		ResetSwitch resetSwitch = null ;
		List<Timeswitch> timer = new ArrayList<Timeswitch>();
		Timeswitch timeswitch = new Timeswitch();
		timer.add(timeswitch);
		Status status = new Status();
		status.setLoadmax(13200);
		for (Switch switch1 : select) {
			device = new Device();
			device.setId(Long.valueOf(switch1.getSwitchSn()));
			device.setIndex(switch1.getSwitchIndex());
			resetSwitch = new ResetSwitch();
			resetSwitch.setDevice(device);
			resetSwitch.setStatus(status);
			resetSwitch.setTimer(timer);
			listData.add(resetSwitch);
		}
		BaseEntity<ResetSwitch> be = new BaseEntity<ResetSwitch>(serialNumber, MqttUtil.CMD_03, listData);
		rabbitTemplate.convertAndSend(QueueConstantUtil.RESET_SWITCH, be);
	}

	@Override
	public Result sendSwitchGjh(SendSwitch sendSwitch) {
		List<Sckgzt> list = new ArrayList<Sckgzt>();
		if(sendSwitch.getSwitchSn() == null) {
			List<SendSwitch> listSwitch = switchMapper.querySwitchBySerialNumber(sendSwitch.getSerialNumber());
			for (SendSwitch sendSwitch2 : listSwitch) {
				//updateRedisLoadMax(sendSwitch.getSerialNumber(), sendSwitch2.getSwitchSn().toString(), sendSwitch.getLoadMax());
				sendSwitch2.setStatus(sendSwitch.getStatus());
				list.add(switchEntity(sendSwitch2));
			}
		}else {
			list.add(switchEntity(sendSwitch));
		}
		Result baseEntityJson = baseEntityJson(sendSwitch.getSerialNumber(), list);
		return baseEntityJson;
	}
	
}
