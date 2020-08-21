package cn.meiot.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.meiot.constart.ProjectConstart;
import cn.meiot.entity.db.*;
import cn.meiot.entity.dto.pc.examination.PcExamination;
import cn.meiot.entity.equipment2.TimeEntity;
import cn.meiot.entity.equipment2.time.SetTimer;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.service.BuildingService;
import cn.meiot.service.EquipmentService;
import cn.meiot.service.RoleService;
import cn.meiot.utlis.MqttUtlis;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.meiot.dao.PcTimerModeMapper;
import cn.meiot.dao.SwitchMapper;
import cn.meiot.dao.TimeBuildingMapper;
import cn.meiot.dao.TimeEquimentMapper;
import cn.meiot.dao.TimeSwitchMapper;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.examination.ExaminationBuildingDto;
import cn.meiot.entity.dto.pc.time.PcTimerModerDto;
import cn.meiot.entity.equipment.BaseEntity;
import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.Timeswitch;
import cn.meiot.entity.equipment.sckgds.Sckgds;
import cn.meiot.entity.vo.Result;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.PcTimerModeService;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utlis.TimeUtlis;
import lombok.extern.slf4j.Slf4j;

@Service("PcTimerMode")
@Slf4j
public class PcTimerModeServiceImpl implements PcTimerModeService {

	@Autowired
	private PcTimerModeMapper pcTimerModeMapper;
	

	@Autowired
	private TimeEquimentMapper timeEquimentMapper;
	
	@Autowired
	private TimeBuildingMapper timeBuildingMapper;
	
	@Autowired
	private TimeSwitchMapper timeSwitchMapper;
	
	@Autowired
	private SwitchMapper switchMapper;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private BuildingService buildingService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private EquipmentService equipmentService;


	@Override
	public Result query(PcEquipmentUserCond cond) {
		PageHelper.startPage(cond.getPage(), cond.getPageSize());
		List<PcTimerMode> listMode = pcTimerModeMapper.queryPage(cond);
		PageInfo pageinfo = new PageInfo<>(listMode);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(pageinfo);
		return defaultTrue;
	}

	@Override
	public Result queryById(Long id, Long userId, Integer projectId,Long currentUserId) {
		Map<String,Object> map = new HashMap<String,Object>();
		List<Long> listBuilding = new ArrayList<Long>();
		List<Long> listEquiment = new ArrayList<Long>();
		List<String> listSwitch = new ArrayList<String>();
		PcTimerMode pcTimerMode = new PcTimerMode();
		if(id != null) {
			listBuilding =  pcTimerModeMapper.queryTimeBuilding(id);
			listEquiment =  pcTimerModeMapper.queryTimeEquiment(id);
			listSwitch =  pcTimerModeMapper.queryTimeSwitch(id);
			pcTimerMode = pcTimerModeMapper.selectByPrimaryKey(id);
			pcTimerMode.setSerialCount(listEquiment.size());
			pcTimerMode.setSwitchCount(listSwitch.size());
		}
		Integer flag = pcTimerMode.getFlag();
		List<ExaminationBuildingDto> listData = buildingService.queryScenarioData(userId, projectId, currentUserId);
		//数据处理
		buildingService.queryDg(listData, id, userId, projectId, listBuilding, listEquiment, listSwitch,flag);
		map.put("building", listData);
		map.put("timeMode", pcTimerMode);
		map.put("listBuilding", listBuilding);
		map.put("listEquiment", listEquiment);
		map.put("listSwitch", listSwitch);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}


	@Override
	@Transactional
	public Result insert(PcTimerModerDto pcTimerModerDto) {
		log.info("数据-----:{}",pcTimerModerDto.getPcTimerMode());
		Integer projectId = pcTimerModerDto.getPcTimerMode().getProjectId();
		//鉴权
		authentication(pcTimerModerDto, projectId);
		PcTimerMode pcTimerMode = pcTimerModerDto.getPcTimerMode();
        Integer flag = pcTimerMode.getFlag();
        pcTimerMode.setCreateTime(new Date());
		int row = pcTimerModeMapper.insertSelective(pcTimerMode);
		Long id = pcTimerMode.getId();
		if(row != 1) {
			throw new MyServiceException(ResultCodeEnum.INSERT_ERROR.getCode(),ResultCodeEnum.INSERT_ERROR.getMsg());
		}
		//组织架构
		inserTimerEqAndSw(pcTimerModerDto, id,ProjectConstart.TIMER);
        List<String> listSwitch = pcTimerModerDto.getListSwitch();
		List<Map<String, Object>> switchList = getSwitchNum(flag, listSwitch, id);
		if(pcTimerMode.getIsSwitch().equals(1)) {
			//开关控制
			//OnTimer(pcTimerMode, pcTimerModerDto.getListSwitch());
            OnTimer(switchList,pcTimerMode);
		}
		return Result.getDefaultTrue();
	}

	private List<Map<String,Object>> getSwitchNum(Integer flag,List<String> listSwitch,Long id){
		List<Map<String,Object>> switchList = new ArrayList<>();
		if(!CollectionUtils.isEmpty(listSwitch)) {
			for (String switchSn : listSwitch) {
				List<Integer> list = pcTimerModeMapper.querySwitchNum(switchSn,flag);
				for (Integer i = 0; i < 5; i++) {
					if(!list.contains(i)){
						pcTimerModeMapper.insertSwitchNum(switchSn,i,id);
						Map<String,Object> map = new HashMap<>();
						map.put("switchSn",switchSn);
						map.put("num",i);
						switchList.add(map);
						break;
					}
				}
			}
		}
		return switchList;
	}

	private void authentication(PcTimerModerDto pcTimerModerDto, Integer projectId) {
		//项目的组织架构
		List<Long> projectBuilding = roleService.queryBuildingIdByProjectId(projectId);
		if(CollectionUtils.isEmpty(projectBuilding)){
			throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
		}else{
			//取出现有的值
			List<Long> listBuilding = new ArrayList<>(pcTimerModerDto.getListBuilding());
			//删除该项目所有的值
			listBuilding.removeAll(projectBuilding);
			//如果不剩一个值  就说明提交数据都在范围内
			if(!CollectionUtils.isEmpty(listBuilding)){
				throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
			}

			List<Long> listEquiment = new ArrayList<>(pcTimerModerDto.getListEquiment());
			if(CollectionUtils.isEmpty(listEquiment)){
				return;
			}
			//项目的用户设备id
			List<Long> projectEquiment = roleService.queryEquipmentUserByProjectId(projectId);
			if(CollectionUtils.isEmpty(projectEquiment)){
				throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
			}
			listEquiment.removeAll(projectEquiment);
			if(!CollectionUtils.isEmpty(listEquiment)){
				throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
			}
		}
	}

	@Override
	public void inserTimerEqAndSw(PcTimerModerDto pcTimerModerDto, Long id,String table) {
		List<Long> listBuilding = pcTimerModerDto.getListBuilding();
		//情景
		if(!CollectionUtils.isEmpty(listBuilding)) {
			List<Long> pList = pcTimerModeMapper.selectPid(listBuilding);
			listBuilding.removeAll(pList);
			if(!CollectionUtils.isEmpty(listBuilding)) {
				pcTimerModeMapper.insertBuilding(id, listBuilding, table);
			}
		}
		//设备号
		List<Long> listEquiment = pcTimerModerDto.getListEquiment();
		if(!CollectionUtils.isEmpty(listEquiment)) {
			pcTimerModeMapper.insertEquiment(id,listEquiment,table);
		}
		/*//开关
		List<String> listSwitch = pcTimerModerDto.getListSwitch();
		if(!CollectionUtils.isEmpty(listSwitch)) {
            listSwitch.forEach( switchSn ->{
                Integer flag ;
                if(table.equals(ProjectConstart.TIMER)){
                    flag = 1;
                }else {
                    flag = 2;
                }
                List<Integer> list = pcTimerModeMapper.querySwitchNum(switchSn,flag);
                for (Integer i = 0; i < 5; i++) {
                    if(!list.contains(i)){
                        pcTimerModeMapper.insertSwitchNum(switchSn,i,id);
                        break;
                    }
                }
            });
			//pcTimerModeMapper.insertSwitch(id,listSwitch,table);
		}*/
	}

	@Override
	@Transactional
	public Result update(PcTimerModerDto pcTimerModerDto) {
		//以前的值
		PcTimerMode pcTimerMode =  pcTimerModerDto.getPcTimerMode();
		List<String> listSwitch = pcTimerModerDto.getListSwitch();
		Integer flag = pcTimerMode.getFlag();
		Long id = pcTimerMode.getId();
		Integer projectId = pcTimerMode.getProjectId();
		//authentication(pcTimerModerDto, projectId);
		PcTimerMode oldPcTimerMode = pcTimerModeMapper.selectByPrimaryKey(id);
		if(!pcTimerMode.getProjectId().equals(oldPcTimerMode.getProjectId())){
			throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
		}
		if(pcTimerMode.getType().equals(3)) {
			pcTimerMode.setOff(null);
		}else if(pcTimerMode.getType().equals(4)) {
			pcTimerMode.setKai(null);
		}
		int row = pcTimerModeMapper.updateByPrimaryKey(pcTimerMode);
		//修改的值
		//PcTimerMode pcTimerMode =  pcTimerModerDto.getPcTimerMode();
		if(row != 1) {
			throw new MyServiceException(ResultCodeEnum.UPDATE_ERROR.getCode(),ResultCodeEnum.UPDATE_ERROR.getMsg());
		}
		//执行状态是否相等
		boolean eq = oldPcTimerMode.getIsSwitch().equals(pcTimerMode.getIsSwitch());
		//新的执行状态
		boolean newIsSwitch = pcTimerMode.getIsSwitch().equals(1);
		//旧的执行状态
		boolean oldIsSwitch = oldPcTimerMode.getIsSwitch().equals(1);
		//旧的开关 查出旧的开关
		//List<String> oldSwitch = pcTimerModeMapper.queryTimeSwitch(id);
		List<Map<String,Object>> oldSwitch = pcTimerModeMapper.queryTimeSwitchById(id);
		//全删
		deleteTimer(id);
		//需要执行的开关
		List<Map<String,Object>> implementSwithch = new ArrayList<>();
		//需要关闭的开关
		List<Map<String,Object>> onSwithch = new ArrayList<>();
		//需要重新添加的开关
		List<String> againSwitch = new ArrayList<>();
		if(CollectionUtils.isEmpty(oldSwitch)){
			List<Map<String, Object>> switchNum = getSwitchNum(flag, listSwitch, id);
			implementSwithch.addAll(switchNum);
		}else {
			if(CollectionUtils.isEmpty(listSwitch)){
				onSwithch = oldSwitch;
			}else {
				//在旧开关里面找出和新开关 重复的，和 删除的
				for (Map<String,Object> map : oldSwitch){
					String switchSn = (String) map.get("switchSn");
					Integer num = (Integer) map.get("num");
					if (listSwitch.contains(switchSn)){
						pcTimerModeMapper.insertSwitchNum(switchSn,num,id);
						implementSwithch.add(map);
					}else {
						onSwithch.add(map);
					}
				}
				//在新提交的开关里  找出  新增的
				for (String newSwtichSn : listSwitch){
					//重复标记
					boolean repeatFlag = true;
					String newSw = newSwtichSn;
					for (Map<String,Object> map : oldSwitch) {
						String switchSn = (String) map.get("switchSn");
						if(newSw.equals(switchSn)){
							repeatFlag = false;
							break;
						}
					}
					//如果没有相同的则添加
					if(repeatFlag){
						againSwitch.add(newSw);
					}
				}
				implementSwithch.addAll(getSwitchNum(flag, againSwitch, id));
			}
		}
		//再全加
		inserTimerEqAndSw(pcTimerModerDto, id, ProjectConstart.TIMER);
		//如果相等  并且为执行状态
		List<String> newSwitch = pcTimerModerDto.getListSwitch();
		String cmd = MqttUtlis.getCmd(pcTimerMode.getFlag());
		if(eq && newIsSwitch) {
			//把没有的电箱删除定时
			if(!CollectionUtils.isEmpty(onSwithch)) {
				//删除相同的电箱得到被删除的开关
				//oldSwitch.removeAll(newSwitch);
				//删除定时信息
				offTimer(onSwithch,flag);
			}
			if(!CollectionUtils.isEmpty(implementSwithch)) {
				//发送新的定时信息
				OnTimer(implementSwithch, pcTimerMode);
			}
			//旧的等于开  新的等于关
		}else if(!eq && oldIsSwitch){
			//关掉之前的定时信息
			//oldSwitch
			offTimer(onSwithch,flag);
		}
		//旧的等于关  新的等于开
		else if(!eq && newIsSwitch) {
			if(!CollectionUtils.isEmpty(newSwitch)) {
				//发送新的定时信息
				OnTimer(implementSwithch, pcTimerMode);
			}
		}
		return Result.getDefaultTrue();
	}

	public void deleteTimer(Long id) {
		//根据id 删除  全删
		TimeEquiment t = new TimeEquiment();
		t.setTimeId(id);
		timeEquimentMapper.delete(t);
		TimeBuilding t2 = new TimeBuilding();
		t2.setTimeId(id);
		timeBuildingMapper.delete(t2);
		TimerSwitch t3 = new TimerSwitch();
		t3.setTimeId(id);
		timeSwitchMapper.delete(t3);
	}

	@Override
	public Result querySwitchList(PcEquipmentUserCond cond) {
		Long buildingId = cond.getBuildingId();
		if(buildingId != null){
			List<Building> buildings = buildingService.getBuildings(cond.getProjectId(), cond.getUserId());
			List<Long> ids = new ArrayList<>();
			buildingDg(buildingId,ids,buildings);
			ids.add(buildingId);
			cond.setBuildingList(ids);
		}
		PageHelper.startPage(cond.getPage(), cond.getPageSize());
		List<Map> listMode = pcTimerModeMapper.querySwitchList(cond);
		PageInfo pageinfo = new PageInfo<>(listMode);
		listMode.forEach(map -> {
			Long id = Long.valueOf((Integer)map.get("buildingId"));
			map.put("address",buildingService.queryAddress(id));
		});
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(pageinfo);
		return defaultTrue;
	}

	@Override
	public void authentication(Long id, Integer projectId) {
		PcTimerMode pcTimerMode = pcTimerModeMapper.selectByPrimaryKey(id);
		if(!pcTimerMode.getProjectId().equals(projectId)){
			throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
		}
	}

	@Override
	public Result queryEquipment(PcEquipmentUserCond cond) {
		Long id = cond.getId();
		Long userId = cond.getUserId();
		List<PcExamination> examinationList = pcTimerModeMapper.queryEquipment(id,userId);
		examinationList.forEach( examination->{
			Long buildingId = examination.getBuildingId();
			String s = buildingService.queryAddress(buildingId);
			examination.setAddress(s);
		});
		return Result.OK(examinationList);
	}

    @Override
    public Result isSwitch(PcTimerMode pcTimerMode) {
		Long id = pcTimerMode.getId();
		PcTimerMode pcTimerMode1 = pcTimerModeMapper.selectByPrimaryKey(id);
		if(pcTimerMode1 == null || !pcTimerMode.getProjectId().equals(pcTimerMode1.getProjectId())){
			throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
		}
		Integer isSwitch = pcTimerMode.getIsSwitch();
		pcTimerMode1.setIsSwitch(isSwitch);
		pcTimerModeMapper.updateByPrimaryKey(pcTimerMode1);
		if(pcTimerMode.getType().equals(3)) {
			pcTimerMode.setOff(null);
		}else if(pcTimerMode.getType().equals(4)) {
			pcTimerMode.setKai(null);
		}
		//查询旧开关
		//List<String> oldSwitch = pcTimerModeMapper.queryTimeSwitch(id);
        List<Map<String,Object>> oldSwitch = pcTimerModeMapper.queryTimeSwitchById(id);
        String cmd = MqttUtlis.getCmd(pcTimerMode.getFlag());
        Integer flag = pcTimerMode1.getFlag();
        if(isSwitch.equals(0)){
			offTimer(oldSwitch,flag);
		}else{
			OnTimer(oldSwitch,pcTimerMode1);
		}
		return Result.OK();
    }

    @Override
    public Result isImplement(Long id,List<String> switchSn,Integer projectId) {
		List<String> oldSwitchSn = pcTimerModeMapper.queryNoTimeSwitch(id,projectId);
		int size = oldSwitchSn.size();
		if(switchSn == null){
			switchSn = pcTimerModeMapper.queryTimeSwitch(id);
		}
		oldSwitchSn.removeAll(switchSn);
		int size1 = oldSwitchSn.size();
		if(size == size1){
			return Result.OK(true);
		}
		return Result.OK(false);
    }

    //递归查询所需组织架构内层并放入list 中,
	private Long buildingDg(final Long id,List<Long> list,List<Building> buildings){
		if(id == null){
			return null;
		}
		List<Building> collect = buildings.stream().filter(building -> building.getParentId().equals(id)).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(collect)){
			return null;
		}
		for (Building building: collect) {
			Long buildingId = building.getId();
			list.add(buildingId);
			buildingDg(buildingId,list,buildings);
		}
		return null;
	}

	@Override
	@Transactional
	public Result delete(PcTimerMode pcTimerMode) {
		Long id = pcTimerMode.getId();
		//旧的
		PcTimerMode pcTimerMode1 = pcTimerModeMapper.selectByPrimaryKey(id);
		//如果查不到或者项目id不匹配
		if (pcTimerMode1 == null || !pcTimerMode1.getProjectId().equals(pcTimerMode.getProjectId())){
			throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
		}
		if(pcTimerMode1.getIsSwitch().equals(1)){
			/*List<String> oldSwitch = pcTimerModeMapper.queryTimeSwitch(id);
			String cmd = MqttUtlis.getCmd(pcTimerMode1.getFlag());
			OffTimer(oldSwitch,cmd);*/
			List<Map<String,Object>> oldSwitch = pcTimerModeMapper.queryTimeSwitchById(id);
			if(!CollectionUtils.isEmpty(oldSwitch)){
				offTimer(oldSwitch,pcTimerMode1.getFlag());
			}
		}
		pcTimerModeMapper.deleteByPrimaryKey(id);
		deleteTimer(id);
		return Result.getDefaultTrue();
	}

	private Timeswitch getTimeswitch(PcTimerMode pcTimerMode){
        Timeswitch ts = new Timeswitch();
        //结束时间
        ts.setEnd(pcTimerMode.getEndTime());
        //开始时间
        ts.setStart(pcTimerMode.getStartTime());
        //星期数组
        if(StringUtils.isNotBlank(pcTimerMode.getCycle())) {
            String cycle = "["+ pcTimerMode.getCycle() +"]";
            List<Integer> parseArray = JSONObject.parseArray(cycle, Integer.class);
            ts.setCycle(parseArray);
        }
        //关闭时间和打开时间
        ts.setOff(pcTimerMode.getOff());
        ts.setOn(pcTimerMode.getOn());
        ts.setMode(pcTimerMode.getType());
        if(ts.getMode() == 3 || ts.getMode() == 4) {
            //结束时间加一天
            ts.setEnd(ts.getStart()+86400L);
        }else if(ts.getMode() == 2) {
            //从当前时间开始
            ts.setStart(System.currentTimeMillis()/1000);
        }else {
            ts.setStart(TimeUtlis.getTime2(ts.getStart(), ts.getOn()));
            ts.setEnd(TimeUtlis.getTime2(ts.getEnd(), ts.getOff()));
        }
        ts.setLoadmax(pcTimerMode.getLoadmax());
        return ts;
    }

    public void OnTimer(List<Map<String,Object>> listSwitch,PcTimerMode pcTimerMode) {
        Timeswitch ts = getTimeswitch(pcTimerMode);
        Device device= null;
        List<Sckgds> listsckgds = null;
        List<Timeswitch> listTime = null;
		SetTimer setTimer = null;
        for (Map<String,Object> map : listSwitch) {
            listTime = new ArrayList<Timeswitch>();
			String switchSn = (String) map.get("switchSn");
			listsckgds = new ArrayList<Sckgds>();
			Sckgds sckgds = new Sckgds();
			Switch selectByPrimaryKey = switchMapper.selectByPrimaryKey(switchSn);
			String serialNumber = selectByPrimaryKey.getSerialNumber();
			if(selectByPrimaryKey == null) {
				continue;
			}
			Integer integer = equipmentService.queryAgreementVersionBySwitchSn(switchSn);
			//添加数量
			Integer num = (Integer) map.get("num");
			if(integer.equals(2)){
				setTimer = new SetTimer(ts);
				setTimer.setSid(Long.valueOf(switchSn));
				setTimer.setNum(num);
				TimeEntity timeEntity = new TimeEntity(setTimer);
				timeEntity.setDeviceid(serialNumber);
				rabbitTemplate.convertAndSend(QueueConstantUtil.SEND_TIMER2, timeEntity);
				continue;
			}
			device= new Device();
            //device.setId(new Long(selectByPrimaryKey.getSwitchSn()));
            device.setIndex(selectByPrimaryKey.getSwitchIndex());
            ts.setNum(num);
            listTime.add(ts);
            sckgds.setTimer(listTime);
            sckgds.setDevice(device);
            listsckgds.add(sckgds);
            String mqtt =  MqttUtlis.getCmd(pcTimerMode.getFlag());
            BaseEntity<Sckgds> be = new BaseEntity<Sckgds>(serialNumber,mqtt , listsckgds);
            rabbitTemplate.convertAndSend(QueueConstantUtil.SEND_TIMER, be);
            log.info("设置定时参数:{}",be);
        }
        //String switchSn = timerMode.getSwitchSn();
        //拼接数据
    }

	public void offTimer(List<Map<String,Object>> listSwitch,Integer flag) {
		//Timeswitch ts = getTimeswitch(pcTimerMode);
		Device device= null;
		List<Sckgds> listsckgds = null;
		List<Timeswitch> listTime = null;
		Timeswitch ts = new Timeswitch();
		SetTimer setTimer = null;
		Long time = System.currentTimeMillis() / 1000;
		for (Map<String,Object> map : listSwitch) {
			listTime = new ArrayList<Timeswitch>();
			String switchSn = (String) map.get("switchSn");
			listsckgds = new ArrayList<Sckgds>();
			Sckgds sckgds = new Sckgds();
			Switch selectByPrimaryKey = switchMapper.selectByPrimaryKey(switchSn);
			if(selectByPrimaryKey == null) {
				continue;
			}
			String serialNumber = selectByPrimaryKey.getSerialNumber();
			Integer integer = equipmentService.queryAgreementVersionBySwitchSn(switchSn);
			//添加数量
			Integer num = (Integer) map.get("num");
			if(integer.equals(2)){
				setTimer = SetTimer.closeTime(Long.valueOf(switchSn),flag,num,time);
				TimeEntity timeEntity = new TimeEntity(setTimer);
				timeEntity.setDeviceid(serialNumber);
				rabbitTemplate.convertAndSend(QueueConstantUtil.SEND_TIMER2, timeEntity);
				continue;
			}
			device= new Device();
			//device.setId(new Long(selectByPrimaryKey.getSwitchSn()));
			device.setIndex(selectByPrimaryKey.getSwitchIndex());
			ts.setNum(num);
			listTime.add(ts);
			sckgds.setTimer(listTime);
			sckgds.setDevice(device);
			listsckgds.add(sckgds);
			String mqtt =  MqttUtlis.getCmd(flag);
			BaseEntity<Sckgds> be = new BaseEntity<Sckgds>(serialNumber,mqtt , listsckgds);
			rabbitTemplate.convertAndSend(QueueConstantUtil.SEND_TIMER, be);
			log.info("设置定时参数:{}",be);
		}
		//String switchSn = timerMode.getSwitchSn();
		//拼接数据
	}

	public void OnTimer(PcTimerMode pcTimerMode,List<String> listSwitch) {
		List<Timeswitch> listTime = new ArrayList<Timeswitch>();
        Timeswitch ts = getTimeswitch(pcTimerMode);
		listTime.add(ts);
		Device device= null;
		List<Sckgds> listsckgds = null;
		for (String switchSn : listSwitch) {
			listsckgds = new ArrayList<Sckgds>();
			Sckgds sckgds = new Sckgds();
			Switch selectByPrimaryKey = switchMapper.selectByPrimaryKey(switchSn);
			if(selectByPrimaryKey == null) {
				continue;
			}
			device= new Device();
			//device.setId(new Long(selectByPrimaryKey.getSwitchSn()));
			device.setIndex(selectByPrimaryKey.getSwitchIndex());
			sckgds.setTimer(listTime);
			sckgds.setDevice(device);
			listsckgds.add(sckgds);
			String mqtt =  MqttUtlis.getCmd(pcTimerMode.getFlag());
			BaseEntity<Sckgds> be = new BaseEntity<Sckgds>(selectByPrimaryKey.getSerialNumber(),mqtt , listsckgds);
			rabbitTemplate.convertAndSend(QueueConstantUtil.SEND_TIMER, be);
			log.info("设置定时参数:{}",be);
		}
		//String switchSn = timerMode.getSwitchSn();
		//拼接数据
	}
	public void OffTimer(List<String> listSwitch,String cmd) {
		for (String switchSn : listSwitch) {
			List<Sckgds> listsckgds = new ArrayList<Sckgds>();
			Sckgds sckgds = new Sckgds();
			Switch selectByPrimaryKey = switchMapper.selectByPrimaryKey(switchSn);
			if(selectByPrimaryKey == null) {
				continue;
			}
			Device device= new Device();
			//device.setId(new Long(selectByPrimaryKey.getSwitchSn()));
			device.setIndex(selectByPrimaryKey.getSwitchIndex());
			sckgds.setDevice(device);
			listsckgds.add(sckgds);
			BaseEntity<Sckgds> be = new BaseEntity<Sckgds>(selectByPrimaryKey.getSerialNumber(), cmd, listsckgds);
			rabbitTemplate.convertAndSend(QueueConstantUtil.OFF_TIMER, be);
		}
	}

	@Override
	public Result querySwitch(PcEquipmentUserCond cond) {
		PageHelper.startPage(cond.getPage(), cond.getPageSize());
		List<PcTimerMode> listMode = pcTimerModeMapper.querySwitch(cond);
		PageInfo pageinfo = new PageInfo<>(listMode);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(pageinfo);
		return defaultTrue;
	}
}
