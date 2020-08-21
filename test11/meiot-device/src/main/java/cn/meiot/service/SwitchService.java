package cn.meiot.service;

import java.util.List;
import java.util.Map;

import cn.meiot.entity.dto.SwitchRespDto;
import cn.meiot.entity.dto.UpdateName;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.sw.SendSwitch;
import cn.meiot.entity.equipment.sckgzt.Sckgzt;
import cn.meiot.entity.equipment2.BaseEntity2;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SerialNumberMasterVo;

/**
 * @author lingzhiying
 * @title: SwitchService.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月3日
 */
public interface SwitchService {

	/**
	 * 修改开关别名
	 * @param obj
	 * @return
	 */
	Result updateName(UpdateName obj);

	/**
	 * 控制单个开关
	 * @param sendSwitch
	 * @return
	 */
	Result sendSwitch(SendSwitch sendSwitch);

	/**
	 * 全部开关
	 * @param sendSwitch
	 * @return
	 */
	Result sendSwitchAll(SendSwitch sendSwitch,List<String> switchList);

	/**
	 * 设置最大功率
	 * @param map
	 * @return
	 */
	Result sendSwitchLoadmax(SendSwitch map);

	/**
	 * 
	 * @param serialNumber
	 * @return
	 */
	Result getMasterIndex(String serialNumber);

	/**
	 * 根据设备和用户查找 开关信息
	 * @param serialNumber
	 * @param mainUserId
	 * @return
	 */
	List<SwitchRespDto> querySwitchStatus(String serialNumber, Long mainUserId,Integer projectId,Integer status);

	/**
	 * 设置开关功率
	 * @param listSendSwitch
	 * @return
	 */
	Result sendPcSwitchLoadmax(List<SendSwitch> listSendSwitch);

	/**
	 * 统一设置功率
	 * @param serialNumber
	 * @param loadMax 
	 * @return
	 */
	Result sendPcSwitchLoadmaxAll(String serialNumber, Integer loadMax);

	/**
	 *           
	 * @param serialNumber
	 * @param mainUserId
	 * @return
	 */
	List<SwitchRespDto> querySwitchDetails(String serialNumber, Long mainUserId,Integer projectId);

	/**
	 * 根据项目ID全开全关
	 * @param cond
	 * @return
	 */
	Result sendSwitchProject(PcEquipmentUserCond cond);

	/**
	 * 查询开关状态
	 * @param cond
	 * @return
	 */
	Result querySwitchColseProject(PcEquipmentUserCond cond);

	/**
	 * 通过设备号查询主开关编号
	 * @param serialNumbers
	 * @return
	 */
	List<SerialNumberMasterVo> queryMasterIndexBySerialNUmber(List<String> serialNumbers);

	Result querySwitch(String serialNumber, Long mainUserId);

	/**
	 * 通过组织架构查询设备名和主开关号
	 * @param buildingId
	 * @param mainUserId
	 * @return
	 */
	List<Map> querySwitchByBuilding(Long buildingId, Integer projectId);
	
	void resetSwitch(String serialNumber);

	/**
	 * 高交会控制设备
	 * @param sendSwitch
	 * @return
	 */
	Result sendSwitchGjh(SendSwitch sendSwitch);

	/**
	 * 修改缓存功率
	 */
	void updateRedisLoadMax(String serialNumber,String sn,Integer loadMax);

	/**
	 * 修改缓存功率
	 */
	void updateRedisLoadMax(String serialNumber,String sn,Integer loadMax,Integer status);

	/**
	 * 开关控制
	 */
	Result baseEntityJson(String serialNumber, List<Sckgzt> list);

	void authentication(Long userId,String switchSn);

	/**
	 * 批量设置功率限定
	 * @param map
	 * @return
	 */
	Result sendSwitchLoadmaxAll(SendSwitch map);

	/**
	 * 通过设备查询开关状态
	 * @param serialNumber
	 * @return
	 */
	Result switchStatus(String serialNumber,Long userId);


	Result sendSwitchLoadmaxPersonal(SendSwitch map);

	/**
	 * 发送消息
	 */
	Result sendMessage(String serialNumber,String jsonString);

	/**
	 *
	 */
	Result baseEntityJson2(BaseEntity2 controlEntity, String serialNumber);

	/**
	 * 修改主开关
	 */
	void updateMainSwitch(String serialNumber,String switchSn);

	/**
	 * 修改主开关
	 */
	void updateSonSwitch(String serialNumber,String switchSn);
}
