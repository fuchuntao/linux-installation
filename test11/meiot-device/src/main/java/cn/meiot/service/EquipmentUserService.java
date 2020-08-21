package cn.meiot.service;

import java.util.List;
import java.util.Map;

import cn.meiot.entity.db.EquipmentUser;
import cn.meiot.entity.dto.EquipmentUserDto;
import cn.meiot.entity.dto.UpdateName;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.excel.ProjectExcel;
import cn.meiot.entity.excel.UserExcel;
import cn.meiot.entity.vo.PersonalSerialVo;
import cn.meiot.entity.vo.Result;

/**
 * @author lingzhiying
 * @title: EquipmentUserService.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月19日
 */
public interface EquipmentUserService {

	/**
	 * 查询用户是否有改设备权限
	 * @param userId
	 * @param serialNumber  设备号
	 * @return
	 */
	boolean queryUserIdAndSerialumber(Long userId, String serialNumber);

	/**
	 * 绑定设备
	 * @param serialNumber
	 * @param userId
	 * @return
	 */
	Result bindEquipment(String serialNumber, Long userId);

	/**
	 * 查询用户设备
	 * @param userId
	 * @return
	 */
	Result queryEquipmentUser(Long userId);

	/**
	 * 修改设备名
	 * @param updateName
	 * @return
	 */
	Result updateName(UpdateName updateName);

	/**
	 * 通过设备查询相关联设备
	 * @param serialNumber
	 * @return
	 */
	Result getRtuserIdBySerialNumber(String serialNumber);

	/**
	 * 审批设备
	 * @param equipmentUserDto
	 * @return
	 */
	Result approvalEquipment(EquipmentUserDto equipmentUserDto);

	/**
	 * 解绑设备
	 * @param equipmentUser
	 * @return
	 */
	Result unbound(EquipmentUser equipmentUser);

	/**
	 * 根据设备查询主账户
	 * @param serialNumber
	 * @return
	 */
	Result getRtuserIdByUserId(String serialNumber);

	/**
	 * 查询该设备的状态
	 * @param serialNumber
	 * @param userId
	 * @return
	 */
	Result queryEquipment(String serialNumber, Long userId);

	/**
	 * 设置为默认设备
	 * @param equipmentUser
	 * @return
	 */
	Result unDefault(EquipmentUser equipmentUser);

	/**
	 * 修改子用户名称
	 * @param equipmentUser
	 * @return
	 */
	Result updateUserName(EquipmentUser equipmentUser);
	
	/**
	 * 是否为主用户
	 */
	boolean queryIsMainUser(String serialNumber, Long userId);

	/**
	 * 获取用户名
	 * @param serialNumber
	 * @return
	 */
	Result queryUserName(String serialNumber);

	/**
	 * 
	 * @param userId
	 * @return
	 */
	Result queryMain(Long userId);

	/**
	 * 通过id查询当前用户是否为设备的主用户
	 * @param id
	 * @param userId
	 * @return
	 */
	boolean queryIsMainUserById(Long id, Long userId);

	/**
	 * 删除子用户
	 * @param equipmentUser
	 * @return
	 */
	Result delete(EquipmentUser equipmentUser);

	/**
	 * 查找用户设备列表
	 * @return
	 */
	Result queryUser(PcEquipmentUserCond cond);

	/**
	 * 查找项目设备列表
	 * @param cond
	 * @return
	 */
	Result queryProject(PcEquipmentUserCond cond);

	/**
	 * 
	 * @param cond
	 * @returns
	 */
	Result queryUserBySerialNumber(PcEquipmentUserCond cond);

	/**
	 * pc后台删除设备和人绑定
	 * @param listIds
	 * @return
	 */
	Result pcDelete(List<String> listIds);

	/**
	 * 修改
	 * @param equipmentUser
	 * @return
	 */
	Result updatePc(EquipmentUser equipmentUser);

	/**
	 * 删除pc
	 * @param equipmentUser
	 * @return
	 */
	Result deletePc(EquipmentUser equipmentUser);
	
	/**
	 * 解绑该设备 并且添加其他设备为默认设备
	 */
	void unboundSerialNumber(String serialNumber, Long userId);

	/**
	 * 查询用户是否有权限访问设备
	 */
	public boolean querySerialNumberJurisdiction(Long userId,Integer projectId,String serialNumber, String switchSn,Long mainUserId);

	/**
	 * 查询该用户和企业的配电箱
	 * @param cond
	 * @return
	 */
	Result querySerialNumberAll(PcEquipmentUserCond cond);

	/**
	 *  通过项目id查询所有的设备号
	 * @param projectId
	 * @return
	 */
	List<String> getSerialNUmbersByProjectId(Integer projectId);

	/**
	 * 项目下的设备数量
	 * @param projectId
	 * @return
	 */
	Integer queryDeviceTotal(Integer projectId);

	/**
	 * 用户导出
	 * @param cond
	 * @return
	 */
	List<UserExcel> queryUserExcel(PcEquipmentUserCond cond);

	/**
	 * 项目导出
	 * @param cond
	 * @return
	 */
	List<ProjectExcel> queryProjectExcel(PcEquipmentUserCond cond);

	/**
	 * 获取当前用户下的设备及版本号
	 * @param projectId
	 * @param userId
	 * @return
	 */
	List<Map> querySerialVer(Integer projectId, Long userId);

	/**
	 * 通过项目id查询设备号
	 * @param projectId
	 * @return
	 */
    List<String> querySerialByProjectId(Integer projectId);

	/**
	 * 根据用户查询拥有设备以及该设备的主账户
	 * @param userId
	 * @return
	 */
	List<PersonalSerialVo> querySerialAndMaster(Long userId);

	/**
	 * 设备鉴权
	 */
	void authentication(String serialNumber,Long userId);

	/**
	 * 设备鉴权
	 */
	String authenticationSwtichSn(String switchSn,Long userId);

	/**
	 * 查询设备名喝组织架构
	 * @param projectId
	 * @return
	 */
	Map queryDefaultSerial(Integer projectId,String serialNumber);

	/**
	 *
	 * @param projectId
	 * @return
	 */
    List<PersonalSerialVo> querySerialAndMasterByProjectId(Integer projectId);

	/**
	 * 查询相关设备
	 * @param userId
	 * @param mainUserId
	 * @param projectId
	 * @param equipmentStatus
	 * @return
	 */
	List<String> listEquial(Long builidingId,Long userId, Long mainUserId, Integer projectId, Integer equipmentStatus);

	/**
	 * 获取设备名
	 */
	String findSerialName(String serialNumber,Long userId);

	/**
	 * 获取设备各个状态的数量
	 * @param listEquial
	 * @return
	 */
    Map listEquialNumber(List<String> listEquial,Integer projectId);

	/**
	 * 查询设备的名称和地址
	 * @param buildingId
	 * @param serialNumber
	 * @param mainUserId
	 * @return
	 */
	Map findAddressAndName( String serialNumber, Integer projectId);

	/**
	 * 根据设备查询出数据
	 * @param serialNumber
	 * @param projectId
	 * @param switchSn
	 * @return
	 */
	Map findAddressAndNameBySwitchSn(Integer projectId, String switchSn,Long userId);

	/**
	 * 是否使用改设备
	 * @param serialNumber
	 */
	boolean isExistence(String serialNumber);
}
