package cn.meiot.mapper;

import cn.meiot.entity.EnterpriseUserFaultMsgAlarm;
import cn.meiot.entity.vo.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wuyou
 * @since 2019-10-22
 */
public interface EnterpriseUserFaultMsgAlarmMapper extends BaseMapper<EnterpriseUserFaultMsgAlarm> {

    List<FaultMessageAndTypeVo> selectFaultMsgByTypeAndUserId(@Param("type") Integer type,
                                                              @Param("userId")Long userId,
                                                              @Param("currentPage")Integer currentPage,
                                                              @Param("pageSize")Integer pageSize,
                                                              @Param("projectId")Integer projectId);

    Integer selectFaultMsgByTypeAndUserIdTotal(@Param("type") Integer type,
                                               @Param("userId")Long userId,
                                               @Param("currentPage")Integer currentPage,
                                               @Param("pageSize")Integer pageSize,
                                               @Param("projectId")Integer projectId);

    @Select("select count(1) from enterprise_user_fault_msg_alarm where user_id =#{userId} and event = #{event} and is_read = 0 and is_show = 0 and project_id = #{projectId}")
    Integer findUnreadNumByEvent(DeviceInfoVo deviceInfoVo);

    @Select(" select  count(1) from enterprise_user_fault_msg_alarm where  user_id =#{userId} and serial_number = #{serialNumber} and is_show = 0 and project_id = #{projectId}")
    Integer getAllFaultNumBySerialNumber(DeviceInfoVo deviceInfoVo);

    /**
     * 获取当前用户的未读消息数量
     * @param userId
     * @return
     */
    Integer getUnreadNum(@Param("userId") Long userId,
                         @Param("projectId")Integer projectId);

    /**
     * 获取未读消息，指定条数
     * @param userId
     * @return
     */
    List<Map<String,Object>> getUnread(@Param("userId") Long userId, @Param("num") Integer num,
                                       @Param("projectId")Integer projectId);

    Integer findCountByEvent(DeviceInfoVo deviceInfoVo);

    List<FaultMessageVo> getFaultMessageList(@Param("map") Map<String, Object> map);

    Integer getFaultMessageListTotal(@Param("map") Map<String, Object> map);

    List<StatisticsEventTimeVo> selectStatisticalAlarm(@Param("map") Map<String, Object> map);

    List<StatisticsEventTimeVo> selectStatisticalAlarmAll(@Param("map")Map<String, Object> map);

    Integer selectTotal(@Param("map") Map<String, Object> map);

    List<String> selectFaultNumber(@Param("map") Map<String, Object> map);

    /**
     * 通过设备号查询未处理的故障数量
     * @param serialNumber
     * @return
     */
    Integer getUnprocessed(@Param("serialNumber") String serialNumber,
                           @Param("userId")Long userId);

    List<FaultMessageVo> selectStatisticsFaultMessage(@Param("map") Map<String,Object> map);

    Integer selectStatisticsFaultMessageTotal(@Param("map") Map<String,Object> map);

    Integer selectTotalByFaultType(@Param("type") Integer type,@Param("userId")Long userId);

    /**
     * 查询 报警 预警 未读的总数
     * @param userId
     * @param project
     * @param type
     * @return
     */
    Integer getUnreadNoticeTotal(@Param("userId") Long userId,
                                 @Param("projectId") Integer projectId,
                                 @Param("type") Integer type);


    /**
     * 查询设备故障排行前10
     * @param map
     * @return
     */
    List<TopTenVo> selectTopTen( @Param("map") Map<String, Object> map);

    /**
     * 柱状图统计
     * @param map
     * @return
     */
    List<StatisticsEventTimeVo> getTotalDetailed(@Param("map") Map<String, Object> map);


    /**
     * 饼状图统计
     * @param map
     * @return
     */
    List<StatisticsEventTimeVo> getTotal(@Param("map") Map<String, Object> map);

    /**
     *
     * @param type
     * @param userId
     * @param currentPage
     * @param pageSize
     * @param project
     * @return
     */
    List<FaultMessageAndTypeVo> getFaultByUserId(@Param("type") Integer type,
                                                 @Param("userId") Long userId,
                                                 @Param("currentPage") Integer currentPage,
                                                 @Param("pageSize") Integer pageSize,
                                                 @Param("projectId") Integer project);

    Integer getFaultByUserIdTotal(@Param("type") Integer type,
                                  @Param("userId") Long userId,
                                  @Param("currentPage") Integer currentPage,
                                  @Param("pageSize") Integer pageSize,
                                  @Param("projectId") Integer project);

    List<Map<String, Integer>> warningSum(@Param("personalSerialVos") List<PersonalSerialVo> personalSerialVos,
                                          @Param("projectId") Integer projectId,
                                          @Param("startTime") String startTime);

    List<EnterpriseUserFaultMsgAlarm> newsNotice(@Param("userId") Long userId, @Param("projectId") Integer projectId, @Param("total") Integer total);

    List<Map<String, Integer>> warningNumber(List<PersonalSerialVo> personalSerialVos, Integer projectId, Integer type);
}
