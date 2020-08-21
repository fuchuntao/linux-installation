package cn.meiot.service;

import cn.meiot.entity.FaultMessage;
import cn.meiot.entity.vo.FaultMessageAndTypeVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatisticsEventTimeVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 故障消息 服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-12
 */
public interface IFaultMessageService extends IService<FaultMessage> {

    /**
     * 获取报警的总记录数以及是否包含唯独消息
     * @param userId
     * @return
     */
    Result getReportTotal(Long userId);

    /**
     * 开始重复推送用户未查看的重要信息
     * @return
     */
//    Result repetitionSend();

    /**
     * 根据设备统计报警数量信息
     * @param userId
     * @param serialNumber
     * @return
     */
    Result statisticsWarn(Long userId, String serialNumber);

    /**
     * 获取当前用户的未读消息数量
     * @param userId
     * @return
     */
    Result unread(Long userId);

    /**
     * 获取故障消息列表
     * @return
     */
    Result getFaultMessageList(Map map);

    /**
     * 根据用户ID和设备序列号删除消息
     * @param userId
     * @param serialNumber
     * @return
     */
    Integer deleteMsgByUserIdAndSerialNumber(Long  userId , String serialNumber);


    /**
     * 查询故障消息列表
     * @param type
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     */
    List<FaultMessageAndTypeVo> getFaultMsgByTypeAndUserId(Integer type, Long userId, Integer currentPage, Integer pageSize);

    /**
     * 查询故障消息列表Total
     * @param type
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     */
    Integer getFaultMsgByTypeAndUserIdTotal(Integer type, Long userId, Integer currentPage, Integer pageSize);

    /**
     * 通过时间与事件获取一天内此类事件报警次数
     * @param time
     * @param event
     * @return
     */
    List<StatisticsEventTimeVo> getCountByEventAndTime(String time, Integer event, Long userId, Integer projectId);
}
