package cn.meiot.dao;

import java.util.List;
import java.util.Map;

import cn.meiot.entity.dto.pc.examination.SerialDto;
import org.apache.ibatis.annotations.Param;

import cn.meiot.entity.db.EquipmentUser;
import cn.meiot.entity.dto.UpdateName;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.entity.dto.pc.equipmentUser.EquipmentUserInsert;
import cn.meiot.entity.dto.pc.equipmentUser.EquipmentUserResp;
import cn.meiot.entity.dto.sw.SendSwitch;
import cn.meiot.entity.excel.ProjectExcel;
import cn.meiot.entity.excel.UserExcel;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author lingzhiying
 * @title: EquipmentUserMapper.java
 * @projectName spacepm
 * @description:
 * @date 2019年8月19日
 */
public interface EquipmentUserMapper extends BaseMapper<EquipmentUser> {

    /**
     * 查询用户和设备是否存在关系
     * 用户id必传
     *
     * @return
     */
    Integer queryUserIdAndSerialumber(@Param("userId") Long userId, @Param("serialNumber") String serialNumber);

    /**
     * 绑定设备
     *
     * @param userId
     * @param serialNumber
     * @return
     */
    Integer bindEquipmentUser(@Param("userId") Long userId, @Param("serialNumber") String serialNumber);

    /**
     * 查询用户的设备状态
     *
     * @param serialNumber
     * @param userId
     * @return
     */
    Integer querySwitchByUserId(@Param("userId") Long userId, @Param("serialNumber") String serialNumber);

    /**
     * 查询用户有哪些设备
     *
     * @param userId
     * @return
     */
    List<Map> queryEquipmentUser(Long userId);

    /**
     * 修改设备名
     *
     * @param updateName
     * @return
     */
    Integer updateName(UpdateName updateName);

    /**
     * 修改设备开关状态
     *
     * @param sendSwitch
     * @return
     */
    Integer updateSerialNumberByStatus(SendSwitch sendSwitch);

    /**
     * 通过设备号查询用户id
     *
     * @param serialNumber
     * @return
     */
    List<String> getRtuserIdBySerialNumber(String serialNumber);

    /**
     * 通过设备人员查询是否绑定设备
     *
     * @param serialNumber
     * @param userId
     * @return
     */
    List<EquipmentUser> queryEqBySerialNumberAnduserId(String serialNumber);

    /**
     * 查询设备主账户
     *
     * @param serialNumber
     * @return
     */
    Long getRtuserIdByUserId(String serialNumber);

    /**
     * @param userId
     * @param serialNumber
     * @return
     */
    Map queryEquipment(@Param("userId") Long userId, @Param("serialNumber") String serialNumber);

    /**
     * 修改设备为默认设备
     *
     * @param equipmentUser
     * @return
     */
    int unDefault(EquipmentUser equipmentUser);

    /**
     * @param serialNumber
     * @return
     */
    List<String> getSubUserIdByserialNumber(String serialNumber);

    /**
     * @param serialNumber
     */
    void deleteBySerialNumber(String serialNumber);

    /**
     * 获取子用户名
     *
     * @param serialNumber
     * @return
     */
    List<Map> queryUserName(String serialNumber);

    /**
     * 查找用户主电箱
     *
     * @param userId
     * @return
     */
    List<Map> queryMain(Long userId);

    /**
     * 通过id查询该设备的主用户s
     *
     * @param id
     * @return
     */
    Long queryIsMainUserById(Long id);

    /**
     * 通过id和用户id查询该设备的主用户
     *
     * @param id
     * @return
     */
    Long queryIsMainUserByIdAndSerial(@Param("serialNumber") String serialNumber, @Param("userId") Long userId);

    /**
     * 修改
     *
     * @param equipmentUser
     * @return
     */
    int updateEquipmentUser(EquipmentUser equipmentUser);

    /**
     * @param id
     * @return
     */
    EquipmentUser queryById(Long id);

    /**
     * 查找条数
     *
     * @param userId
     * @param id
     * @return
     */
    Long queryIsCountByDefault(@Param("userId") Long userId, @Param("id") Long id);

    /**
     * 查找该用户绑定该设备
     *
     * @param serialNumber
     * @param userId
     * @return
     */
    EquipmentUser queryEquipmentByUser(@Param("serialNumber") String serialNumber, @Param("userId") Long userId);

    /**
     * 查找
     *
     * @param cond
     * @return
     */
    List<EquipmentUserResp> queryUser(PcEquipmentUserCond cond);

    /**
     * 查找企业列表
     *
     * @param cond
     * @return
     */
    List<EquipmentUserResp> queryProject(PcEquipmentUserCond cond);

    /**
     * 删除集合内设备
     *
     * @param listIds
     */
    void deleteBySerialNumberS(@Param("list") List<String> listIds);

    /**
     * 添加企业端设备
     *
     * @param equipmentUserInsert
     */
    void pcInsert(EquipmentUserInsert equipmentUserInsert);

    /**
     * 根据名称模式搜索
     *
     * @param name
     * @return
     */
    List<String> getSerialNumberByName(String name);

    List<Map> querySerialByProjectId(PcEquipmentUserCond cond);

    List<String> getSerialNUmbersByProjectId(@Param("projectId") Integer projectId);

    Integer queryDeviceTotal(@Param("projectId") Integer projectId);

    List<UserExcel> queryUserExcel(PcEquipmentUserCond cond);

    List<ProjectExcel> queryProjectExcel(PcEquipmentUserCond cond);

    /**
     * 通过项目id查询设备号
     * @param projectId
     * @return
     */
    List<String> querySerialByProject(Integer projectId);

    /**
     * 根据用户查询设备号
     * @param userId
     * @return
     */
    List<String> querySerialByUserId(Long userId);

    List<String> querySerialAndMasterByProjectId(Integer projectId);

    /**
     *
     * @param projectId
     * @return
     */
    Map queryDefaultSerial(@Param("projectId") Integer projectId, @Param("serialNumber") String serialNumber);

    /**
     * 查询该项目下的设备
     * @param projectId
     * @return
     */
    List<String> querySerialNumberByProjectId(Integer projectId);

    /**
     * 查询该项目下的用户设备表的id
     * @param projectId
     * @return
     */
    List<Long> queryEquipmentUserByProjectId(Integer projectId);


    /**
     *
     * @param projectId
     * @return
     */
    List<SerialDto> querySerialAndBuildingId(Integer projectId);

    /**
     * 初始化数据使用
     * @return
     */
    List<Map> initSerialTotal();

    /**
     * 查询组织架构ID以及名称
     * @param serialNumber
     * @param projectId
     * @return
     */
    Map findBuildingIdAndName(String serialNumber, Integer projectId);

    /**
     *
     * @param serialNumber
     * @param projectId
     * @param switchSn
     * @return
     */
    Map findAddressAndNameBySwitchSn( @Param("projectId") Integer projectId, @Param("switchSn") String switchSn, @Param("userId") Long userId);

    /**
     * 查询id
     * @param serialNumber
     * @return
     */
    Long selectIdBySerialNuber(String serialNumber);
}
