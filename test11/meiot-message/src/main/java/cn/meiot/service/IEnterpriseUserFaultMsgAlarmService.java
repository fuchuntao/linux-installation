package cn.meiot.service;

import cn.meiot.entity.EnterpriseUserFaultMsgAlarm;
import cn.meiot.entity.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuyou
 * @since 2019-10-22
 */
public interface IEnterpriseUserFaultMsgAlarmService extends IService<EnterpriseUserFaultMsgAlarm> {


    /**
     * 通过类型和用户ID获取故障消息
     * @param type
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     */
    List<FaultMessageAndTypeVo> getFaultMsgByTypeAndUserId(Integer type, Long userId, Integer currentPage, Integer pageSize,Integer projectId);

    /**
     * 通过类型和用户ID获取total
     * @param type
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     */
    Integer getFaultMsgByTypeAndUserIdTotal(Integer type, Long userId, Integer currentPage, Integer pageSize,Integer projectId);

    Result statisticsWarn(Long userId, String serialNumber,Integer projectId);

    Result getReportTotal(Long userId,Integer projectId);

    Result unread(Long userId,Integer projectId);

    /**
     * 获取故障消息列表
     * @return
     */
    Result getFaultMessageList(Map<String, Object> map);

    /**
     * 通过类型来获取统计结果
     * @param map
     * @return
     */
    Result getStatisticalAlarm(Map<String,Object> map);

    /**
     * 根据类型分组进行统计
     * @param map
     * @return
     */
    Result getStatisticalAlarmAll(Map<String, Object> map);

    /**
     * 查询total
     * @param map
     * @return
     */
    Integer getStatisticalAlarmTotal(Map<String, Object> map);

    /**
     * 查询故障设备数
     * @param map
     * @return
     */
    Integer getFaultNumber(Map<String, Object> map);

    /**
     * 根据设备号查询未处理故障数量
     * @param serialNumber
     * @return
     */
    int getUnprocessed(String serialNumber,Long userId);

    /**
     * 根据统计条件查询故障消息列表
     * @param map
     * @return
     */
    Result getStatisticsFaultMessage(Map<String, Object> map);

    /**
     * 通过故障类型获取这个类型的故障总数
     * @param type
     * @return
     */
    Integer getTotalByFaultType(Integer type,Long userId);

    /**
     * 查询报警/预警数 总和
     * @param userId  用户ID
     * @param project 项目ID
     * @param type 类型 1报警 2预警
     * @return
     */
    Integer getUnreadNoticeTotal(Long userId,Integer project,Integer type);

    /**
     * 根据ID查询系统消息未读数
     * @param userId
     * @return
     */
    Integer getUnreadTotal(Long userId);

    /**
     * 获取设备故障前10
     * @param map
     * @return
     */
    List<TopTenVo> getTopTen(Map<String, Object> map);

    /**
     * 统计所有类型的报警预警次数 饼状图
     */
    List<StatisticsEventTimeVo> getTotal(Map<String, Object> map);


    /**
     * 统计所有类型的报警预警次数 饼状图
     */
    List<StatisticsEventTimeVo> getTotalDetailed(Map<String, Object> map);

    /**
     * 通过用户ID和项目获取故障信息
     * @param type 1报警 2预警
     * @param userId 用户ID
     * @param currentPage
     * @param pageSize
     * @param project  项目id
     * @return
     */
    List<FaultMessageAndTypeVo> getFaultByUserId(Integer type, Long userId, Integer currentPage, Integer pageSize, Integer project);

    /**
     * 通过用户ID和项目获取故障信息 total
     * @param type 1报警 2预警
     * @param userId 用户ID
     * @param currentPage
     * @param pageSize
     * @param project  项目id
     * @return
     */
    Integer getFaultByUserIdTotal(Integer type, Long userId, Integer currentPage, Integer pageSize, Integer project);


    List<Map<String, Integer>> warningRate(List<PersonalSerialVo> personalSerialVos, Integer project);

    /**
     * 一键报修
     * @param clickRepairVo
     * @return
     */
    boolean updateStatusAndNoteById(ClickRepairVo clickRepairVo,Long userId,Integer projectId);

    /**
     * 给故障消息记录添加备注
     * @param clickRepairVo
     * @param userId
     * @param projectId
     * @return
     */
    boolean updateNoteById(ClickRepairVo clickRepairVo, Long userId, Integer projectId);

    List<EnterpriseUserFaultMsgAlarm> newsNotice(Long userId,Integer projectId);

    /**
     *
     * @param personalSerialVos
     * @param projectId
     * @param type
     * @return
     */
    List<Map<String, Integer>> warningNumber(List<PersonalSerialVo> personalSerialVos, Integer projectId, Integer type);
}
