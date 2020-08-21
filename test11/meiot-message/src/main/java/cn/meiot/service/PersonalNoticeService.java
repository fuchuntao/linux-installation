package cn.meiot.service;

import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.vo.*;

import java.util.List;
import java.util.Map;

/**
 * @Package cn.meiot.service
 * @Description: 新版个人app报警预警业务接口
 * @author: 武有
 * @date: 2020/2/13 15:22
 * @Copyright: www.spacecg.cn
 */

public interface PersonalNoticeService {

    /**
     * 根据用户ID查询未读的故障预警报警总数
     * @param userId
     * @return
     */
    Integer getUnreadNoticeTotal(Long userId);

    List<AppUserFaultMsgAlarm> newsNotice(Long userId);

    /**
     * 查询设备的预警设备数量和报警设备数量
     * @param personalSerialVos
     * @return
     */
    List<Map<String, Integer>> warningRate(List<PersonalSerialVo> personalSerialVos,Long userId);

    /**
     * 查询设备的预警和报警设备类型数量
     * @param personalSerialVos
     * @param userId
     * @param type
     * @return
     */
    List<Map<String, Integer>> warningNumber(List<PersonalSerialVo> personalSerialVos, Long userId, Integer type);

    /**
     * 查询所有报警预警类型的数量
     * @param map userId 用户ID 时间戳 index 年 月
     * @return
     */
    List<StatisticsEventTimeVo> getTotal(Map<String, Object> map);

    /**
     * 统计报警预警数量 柱状图 年月日方法
     * @param map
     * @return
     */
    List<StatisticsEventTimeVo> getTotalDetailed(Map<String, Object> map);

    /**
     * 根据类型查询
     * @param type
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     */
    List<FaultMessageAndTypeVo> getFaultMsgByTypeAndUserId(Integer type, Long userId, Integer currentPage, Integer pageSize);

    /**
     * 根据类型查询total
     * @param type
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     */
    Integer getFaultMsgByTypeAndUserIdTotal(Integer type, Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取故障前10
     * @param map
     * @return
     */
    List<TopTenVo> getTopTen(Map map);



    /**
     * 根据ID修改备注和状态
     * @param clickRepairVo
     * @return
     */
    boolean updateStatusAndNoteById(ClickRepairVo clickRepairVo,Long userId);

    boolean updateNoteById(ClickRepairVo clickRepairVo, Long userId, Integer projectId);
}
