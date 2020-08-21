package cn.meiot.service;

import cn.meiot.entity.UserAlarm;
import cn.meiot.entity.vo.ReadNumber;

import java.util.List;

/**
 * (UserAlarm)表服务接口
 *
 * @author makejava
 * @since 2020-04-15 10:05:02
 */
public interface UserAlarmService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    UserAlarm queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<UserAlarm> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param userAlarm 实例对象
     * @return 实例对象
     */
    UserAlarm insert(UserAlarm userAlarm);

    /**
     * 修改数据
     *
     * @param userAlarm 实例对象
     * @return 实例对象
     */
    UserAlarm update(UserAlarm userAlarm);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 通过故障消息id修改用户故障表
     * @param ids
     * @return
     */
    boolean updateByFaultMessageId(List<Long> ids,Long userId,UserAlarm userAlarm,Integer type);


    /**
     * 根据ID查询用户的未读预警报警数
     * @param userId
     * @return
     */
    ReadNumber isRead(Long userId);
}