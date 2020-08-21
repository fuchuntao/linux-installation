package cn.meiot.service;

import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.vo.FaultMessageAndTypeVo;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuyou
 * @since 2019-10-22
 */
public interface IAppUserFaultMsgAlarmService extends IService<AppUserFaultMsgAlarm> {

    /**
     * 通过类型和用户ID获取故障消息
     * @param type
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     */
    List<FaultMessageAndTypeVo> getFaultMsgByTypeAndUserId(Integer type, Long userId, Integer currentPage, Integer pageSize);

    /**
     * 通过类型和用户ID获取total
     * @param type
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     */
    Integer getFaultMsgByTypeAndUserIdTotal(Integer type, Long userId, Integer currentPage, Integer pageSize);

    Result statisticsWarn(Long userId, String serialNumber);

    Result getReportTotal(Long userId);

    Result unread(Long userId);

    void deleteMsgByUserIdAndSerialNumber(Long userId, String serialNumber);
}
