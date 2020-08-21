package cn.meiot.service;

import cn.meiot.entity.db.Equipment;
import cn.meiot.entity.dto.pc.equipmentUser.EquipmentUserInsert;
import cn.meiot.entity.equipment2.upswitch.Switchd;
import cn.meiot.entity.vo.EquialStatusVo;
import cn.meiot.entity.vo.Result;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author lingzhiying
 * @title: EquipmentService.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月16日
 */
public interface EquipmentService {

	/**
	 * 查看该设备是否绑定用户
	 * @param serialNumber
	 * @param userId
	 * @return
	 */
	Result queryEquipment(String serialNumber, Long userId);

	/**
	 * 设备绑定用户
	 * @param serialNumber
	 * @param userId
	 * @return
	 */
	Result bindEquipment(String serialNumber, Long userId);

	/**
	 * 查询设备状态
	 * @param serialNumber
	 * @param userId
	 * @return
	 */
	Result queryEquipmentStatus(String serialNumber, Long userId);

	/**
	 * 上传设备108消息
	 */
	void up108(String serialNumber, JSONObject content);

	/**
	 * 设置漏电自检
	 * @param equipment
	 * @return
	 */
	Result examination(Equipment equipment);

	/**
	 * 定时自检
	 * @return
	 */
	Result timingExamination();

	/**
	 * 获取实时数据
	 * @param serialNumber
	 * @return
	 */
	Result realtime(String serialNumber);


	/**
	 * 根据building查询设备列表
	 * @param id
	 * @param mainUserId 
	 * @param userId 
	 * @return
	 */
	Result queryBuilding(Long id, Long mainUserId, Long userId);

	/**
	 * 添加设备
	 * @param equipmentUserInsert
	 * @return
	 */
	Result pcInsert(EquipmentUserInsert equipmentUserInsert);

	/**
	 * 激活设备
	 * @param linkedHashMap
	 */
    void activationSerial(LinkedHashMap linkedHashMap);

	/**
	 * 查询设备所属公司
	 * @param serialNumber
	 * @return
	 */
	Integer serialCompany(String serialNumber);

	/**
	 * 批量设置漏点自检
	 * @param serialList
	 * @param examinationStatus
	 * @param examinationTime
	 * @return
	 */
	Result batchExamination(List<String> serialList, Integer examinationStatus, String examinationTime);

	/**
	 * 二代设备新增
	 */
	void insertEquipment2(String serialNumber, List<Switchd> list);

	/**
	 * 二代版本
	 * @param serialNumber
	 * @param ver
	 */
	void updateVersion(String serialNumber, String ver);

	/**
	 * 查询第几代产品
	 */
	Integer queryAgreementVersion(String serialNumber);

	/**
	 * 查询第几代产品
	 */
	Integer queryAgreementVersionBySwitchSn(String switchSn);

	/**
	 * 下发命令查
	 * @param serialNumber
	 * @return
	 */
    Result selectSwitch(String serialNumber);

	/**
	 *查询设备名
	 * @param listEquial
	 * @param equipmentStatus
	 * @return
	 */
    List<EquialStatusVo> listEquialStatus(List<String> listEquial, Integer equipmentStatus,Long userId,Integer projectId);
}

