package cn.meiot.mapper;

import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.vo.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wuyou
 * @since 2019-10-22
 */
public interface AppUserFaultMsgAlarmMapper extends BaseMapper<AppUserFaultMsgAlarm> {

    List<FaultMessageAndTypeVo> selectFaultMsgByTypeAndUserId(@Param("type") Integer type,
                                                              @Param("userId") Long userId,
                                                              @Param("currentPage") Integer currentPage,
                                                              @Param("pageSize") Integer pageSize);

    Integer selectFaultMsgByTypeAndUserIdTotal(@Param("type") Integer type,
                                               @Param("userId") Long userId,
                                               @Param("currentPage") Integer currentPage,
                                               @Param("pageSize") Integer pageSize);

    @Select("select count(1) from app_user_fault_msg_alarm where user_id =#{userId} and event = #{event} and is_read = 0 and is_show = 0")
    Integer findUnreadNumByEvent(DeviceInfoVo deviceInfoVo);

    @Select(" select  count(1) from app_user_fault_msg_alarm where  user_id =#{userId} and serial_number = #{serialNumber} and is_show = 0")
    Integer getAllFaultNumBySerialNumber(DeviceInfoVo deviceInfoVo);

    /**
     * 获取当前用户的未读消息数量
     *
     * @param userId
     * @return
     */
    Integer getUnreadNum(@Param("userId") Long userId);

    /**
     * 获取未读消息，指定条数
     *
     * @param userId
     * @return
     */
    List<Map<String, Object>> getUnread(@Param("userId") Long userId, @Param("num") Integer num);

    Integer findCountByEvent(DeviceInfoVo deviceInfoVo);

    /**
     * 根据用户ID查询未读的故障消息的报警预警总数
     *
     * @param userId
     * @return
     */
    Integer getUnreadNoticeTotal(Long userId);

    /**
     * 按照ID 倒序查询 故障消息
     *
     * @param userId
     * @param i
     * @return
     */
    List<AppUserFaultMsgAlarm> getNewsNotice(@Param("userId") Long userId,
                                             @Param("total") int i);

    /**
     *
     * @param personalSerialVos
     * @param userId
     * @param startTime
     * @return
     */
    List<Map<String, Integer>> warningSum(@Param("personalSerialVos") List<PersonalSerialVo> personalSerialVos, @Param("userId") Long userId, @Param("startTime") String startTime);


    List<Map<String, Integer>> warningNumber(@Param("personalSerialVos") List<PersonalSerialVo> personalSerialVos, @Param("userId") Long userId, @Param("type") Integer type);

    /**
     * 查询所有报警预警类型
     * @param map
     * @return
     */
    List<StatisticsEventTimeVo> getTotal(@Param("map") Map<String, Object> map);

    List<StatisticsEventTimeVo> getTotalDetailed(@Param("map") Map<String, Object> map);

    List<FaultMessageAndTypeVo> getFaultMsgByTypeAndUserId(@Param("type") Integer type,
                                                           @Param("userId") Long userId,
                                                           @Param("currentPage") Integer currentPage,
                                                           @Param("pageSize") Integer pageSize);

    Integer getFaultMsgByTypeAndUserIdTotal(@Param("type") Integer type,
                                            @Param("userId") Long userId,
                                            @Param("currentPage") Integer currentPage,
                                            @Param("pageSize") Integer pageSize);

    /**
     * 获取故障前10
     * @param map
     * @return
     */
    List<TopTenVo> selectTopTen(@Param("map") Map map);
}
