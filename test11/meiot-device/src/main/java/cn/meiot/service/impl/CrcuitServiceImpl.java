package cn.meiot.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.meiot.constart.ProjectConstart;
import cn.meiot.dao.CrcuitMapper;
import cn.meiot.entity.dto.pc.examination.SwitchDto;
import cn.meiot.entity.equipment.BaseEntity;
import cn.meiot.entity.equipment.kgcs.Kgcs;
import cn.meiot.entity.equipment2.WarnEntity;
import cn.meiot.entity.equipment2.warn.SetWarnValue;
import cn.meiot.utils.MqttUtil;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utlis.MqttUtlis;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.meiot.dao.SwitchMapper;
import cn.meiot.entity.bo.Crcuit;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.examination.SerialDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.CrcuitService;
import cn.meiot.utils.RedisConstantUtil;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrcuitServiceImpl implements CrcuitService {

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private SwitchMapper switchMapper;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private CrcuitMapper crcuitMapper;

	@Override
	public Result query(Integer projectId,String mode) {
		Result defaultTrue = Result.getDefaultTrue();
		Object object = redisTemplate.opsForValue().get(RedisConstantUtil.PROJECT_PARAMETER+projectId+"_"+mode);
		if(object == null) {
			Crcuit crcuit = new Crcuit();
			crcuit.setProjectId(projectId);
			crcuit.setMode(mode);
			Crcuit crcuit1 = crcuitMapper.selectOne(crcuit);
			if(crcuit1 == null){
				defaultTrue.setData(new Crcuit());
			}else{
				redisTemplate.opsForValue().set(RedisConstantUtil.PROJECT_PARAMETER+projectId+"_"+mode, crcuit1,7, TimeUnit.DAYS);
				defaultTrue.setData(crcuit1);
			}
			return defaultTrue;
		}
		defaultTrue.setData(object);
		return defaultTrue;
	}

	@Override
	@Transactional
	public Result update(Crcuit crcuit,Integer projectId,Long userId) {
		crcuit.setProjectId(projectId);
		String mode = crcuit.getMode();
		Integer count = crcuitMapper.selectCount(crcuit);
		if(count != null || count > 0){
			crcuitMapper.updateByPrimaryKeySelective(crcuit);
		}else{
			crcuitMapper.insertSelective(crcuit);
		}
		PcEquipmentUserCond cond = new PcEquipmentUserCond();
		cond.setUserId(userId);
		cond.setProjectId(projectId);
		//List<SerialDto> querySerialNumberAndSwitch = switchMapper.querySerialNumberAndSwitch(cond);
		List<SerialDto> numberAndSwitch = switchMapper.querySerialSwtichByProjectMode(projectId,mode);
		//循环外层设备
		numberAndSwitch.forEach( serialDto -> {
			//获取设备号
			final String str = serialDto.getSerialNumber();
			//serialDto.get
			List<SwitchDto> listSwitch = serialDto.getListSwitch();
			List<SetWarnValue> list = new ArrayList<>();
			List<Kgcs> kgcsList = new ArrayList<>();
			for (SwitchDto switchDto: listSwitch) {
				SetWarnValue setWarnValue = new SetWarnValue(crcuit,new Long(switchDto.getSwitchSn()));
				list.add(setWarnValue);
				Kgcs kgcs = new Kgcs();
				kgcs.setCrcuit(crcuit, switchDto.getSwitchIndex(), null);
				kgcsList.add(kgcs);
			}
			if(serialDto.getType().equals(2)){

				//List<SwitchDto> listSwitch = serialDto.getListSwitch();
				/*for (SwitchDto switchDto: listSwitch) {
					String switchSn = switchDto.getSwitchSn();
					SetWarnValue setWarnValue = new SetWarnValue(crcuit,Long.valueOf(switchSn));
					list.add(setWarnValue);
				}*/
				WarnEntity warnEntity = new WarnEntity(list);
				warnEntity.setDeviceid(str);
				rabbitTemplate.convertAndSend(QueueConstantUtil.CRCUIT2,warnEntity);
			}else{
//				Kgcs kgcs = new Kgcs();
//				kgcs.setCrcuit(crcuit, ProjectConstart.SWITCH_INDEX_ALL, null);
				//List<Kgcs> kgcs1 = Arrays.asList(kgcs);
				BaseEntity<Kgcs> baseEntity = new BaseEntity<>(str, MqttUtil.CMD_03, kgcsList);
				rabbitTemplate.convertAndSend(QueueConstantUtil.CRCUIT,baseEntity);
			}
		});
		redisTemplate.opsForValue().set(RedisConstantUtil.PROJECT_PARAMETER+projectId+"_"+mode, crcuit,7, TimeUnit.DAYS);
		return Result.getDefaultTrue();
	}

}
