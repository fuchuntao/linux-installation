package cn.meiot.mapper;

import cn.meiot.entity.FaultMessage;
import cn.meiot.entity.vo.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 故障消息 Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-12
 */
public interface FaultMessageMapper extends BaseMapper<FaultMessage> {

    /**
     * 获取报警的总记录数以及是否包含未读消息
     * @param userId
     * @return
     */
    List<Map<String,Object>> getReportTotal(@Param("userId") Long userId);

    /**
     * 根据设备统计报警数量信息
     * @param userId
     * @param serialNumber
     * @return
     */
    List<Map<String, Object>> statisticsWarn(@Param("userId") Long userId,@Param("serialNumber") String serialNumber);

    /**
     * 获取当前用户的未读消息数量
     * @param userId
     * @return
     */
    Integer getUnreadNum(@Param("userId") Long userId);

    /**
     * 获取未读消息，指定条数
     * @param userId
     * @return
     */
    List<Map<String,Object>> getUnread(@Param("userId") Long userId,@Param("num") Integer num);

    /**
     * 通过用户id和设备序列号查询当前含有多少事件
     * @param  deviceInfoVo
     * @return
     */
    List<Integer> selectEvent(DeviceInfoVo deviceInfoVo);

    /**
     * 根据事件类型获取总数
     * @param deviceInfoVo
     * @return
     */
    //@Select(" SELECT COUNT(1) FROM `fault_message`  WHERE user_id = #{userId} AND serial_number = #{serialNumber} AND switch_event = #{event}")
    Integer findCountByEvent(DeviceInfoVo deviceInfoVo);

    @Select("select count(1) from fault_message where user_id =#{userId} and switch_event = #{event} and is_read = 0")
    Integer findUnreadNumByEvent(DeviceInfoVo deviceInfoVo);

    @Select(" select  count(1) from fault_message where  user_id =#{userId} and serial_number = #{serialNumber} ")
    Integer getAllFaultNumBySerialNumber(DeviceInfoVo deviceInfoVo);

    List<FaultMessageVo> getFaultMessageList(@Param("map") Map map);

    Integer getFaultMessageListTotal(@Param("map") Map map);

    /**
     * 根据用户id和设备序列号删除消息
     * @param userIds
     * @param serialNumber
     * @return
     */
    Integer deleteMsgByUserIdAndSerialNumber(@Param("userId") Long userIds,@Param("serialNumber") String serialNumber);

    List<FaultMessageAndTypeVo> selectFaultMsgByTypeAndUserId(@Param("type") Integer type,
                                                              @Param("userId")Long userId,
                                                              @Param("currentPage")Integer currentPage,
                                                              @Param("pageSize")Integer pageSize);

    Integer selectFaultMsgByTypeAndUserIdTotal(@Param("type") Integer type,
                                               @Param("userId")Long userId,
                                               @Param("currentPage")Integer currentPage,
                                               @Param("pageSize")Integer pageSize);

    List<StatisticsEventTimeVo> getCountByEventAndTime(@Param("time") String time,
                                                       @Param("event") Integer event,
                                                       @Param("userId") Long userId,
                                                       @Param("projectId") Integer projectId);
}
