package cn.meiot.service.impl;

import java.util.*;

import cn.meiot.client.CommunicationClient;
import cn.meiot.dao.EquipmentMapper;
import cn.meiot.entity.dto.TimingExamination;
import cn.meiot.entity.equipment.BaseEntity;
import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.kgcs.Kgcs;
import cn.meiot.entity.equipment.kgcs.Status1;
import cn.meiot.entity.equipment2.ControlEntity;
import cn.meiot.entity.equipment2.control.Crlpower;
import cn.meiot.entity.equipment2.examination.CheckResult;
import cn.meiot.service.EquipmentService;
import cn.meiot.utils.MqttUtil;
import cn.meiot.utils.QueueConstantUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.meiot.dao.ExaminationMapper;
import cn.meiot.entity.db.Examination;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.examination.PcExamination;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.BuildingService;
import cn.meiot.service.ExaminationService;

@Service
public class ExaminationServiceImpl implements ExaminationService{

	@Autowired
	private ExaminationMapper examinationMapper;
	
	@Autowired
	private BuildingService buildingService;

	@Autowired
	private EquipmentService equipmentService;

	@Autowired
	private EquipmentMapper equipmentMapper;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private CommunicationClient communicationClient;
	
	@Override
	public Result query(PcEquipmentUserCond cond) {
		PageHelper.startPage(cond.getPage(), cond.getPageSize());
		List<PcExamination> list = examinationMapper.query(cond);
		PageInfo pageinfo = new PageInfo<>(list);
		for (PcExamination pcExamination:list) {
			String queryAddress = buildingService.queryAddress(pcExamination.getBuildingId());
			pcExamination.setAddress(queryAddress);
		}
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(pageinfo);
		return defaultTrue;
	}

	@Override
	public Result queryBySerialNumber(PcEquipmentUserCond cond) {
		PageHelper.startPage(cond.getPage(), cond.getPageSize());
		List<Examination> listExamination = examinationMapper.queryBySerialNumber(cond);
		PageInfo pageinfo = new PageInfo<>(listExamination);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(pageinfo);
		return defaultTrue;
	}

    @Override
    public void insert(String serialNumber, List<CheckResult> data2,Long time) {
		Date date = new Date(time);
		data2.forEach( data->{
			Examination examination = new Examination();
			Long sid = data.getSid();
			examination.setCreateTime(date);
			examination.setLeakage(data.getLeakage());
			examination.setSerialNumber(serialNumber);
			examination.setSwitchSn(sid.toString());
			examination.setType(data.getResult());
			examinationMapper.insertSelective(examination);
		});
    }

    @Override
    public Result test(String serialNumber) {
		Integer integer = equipmentService.queryAgreementVersion(serialNumber);
		if(1 == integer){
			TimingExamination timingExamination = equipmentMapper.queryExaminationBySerial(serialNumber);
			sendExaminationVerOne(timingExamination);
		}else{
			List<TimingExamination> timingExaminations = equipmentMapper.queryExamination2BySerial(serialNumber);
			sendExaminationVerTwo(serialNumber,timingExaminations);
		}
		return Result.OK();
    }

	/**
	 * 一带协议
	 * @param equipment2
	 */
	public void sendExaminationVerOne(TimingExamination equipment2) {
		List<Kgcs> listKgcs = new ArrayList<Kgcs>();
		Kgcs kgcs = new Kgcs();
		listKgcs.add(kgcs);
		//定义开关信息
		Device device = new Device();
		device.setId(new Long(equipment2.getSwitchSn()));
		device.setIndex(equipment2.getSwitchIndex());
		kgcs.setDevice(device);
		Status1 s = new Status1();
		s.setTest(1);
		kgcs.setStatus(s);
		BaseEntity<Kgcs> be = new BaseEntity<Kgcs>(equipment2.getSerialNumber(), MqttUtil.CMD_03, listKgcs);
		String jsonString = JSON.toJSONString(be, true);
		Map map = new HashMap();
		map.put("serialNumber", equipment2.getSerialNumber());
		map.put("json", jsonString);
		Result sendSwitch = communicationClient.sendSwitch(map);
	}

	/**
	 * 二代协议
	 * @param string
	 * @param timingExaminations
	 */
	public void sendExaminationVerTwo(String string, List<TimingExamination> timingExaminations) {
		List<Crlpower> list2 = new ArrayList<>();
		ControlEntity c = new ControlEntity(list2);
		c.setDeviceid(string);
//		for (TimingExamination timingExamination : timingExaminations) {
		for (int i = 0; i < timingExaminations.size(); i++) {
			TimingExamination timingExamination = timingExaminations.get(i);
			Long aLong = new Long(timingExamination.getSwitchSn());
			list2.add(Crlpower.leakageTest(aLong));
			Integer parentIndex = timingExamination.getParentIndex();
			if( parentIndex!= null && 0 == parentIndex){
				break;
			}
		}
		rabbitTemplate.convertAndSend(QueueConstantUtil.EXAMINATION2,c);
	}
}
