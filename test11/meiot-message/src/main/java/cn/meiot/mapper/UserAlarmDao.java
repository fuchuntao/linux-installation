package cn.meiot.mapper;

import cn.meiot.entity.UserAlarm;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (UserAlarm)表数据库访问层
 *
 * @author makejava
 * @since 2020-04-15 14:47:33
 */
@Mapper
public interface UserAlarmDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    UserAlarm queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<UserAlarm> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param userAlarm 实例对象
     * @return 对象列表
     */
    List<UserAlarm> queryAll(UserAlarm userAlarm);

    /**
     * 新增数据
     *
     * @param userAlarm 实例对象
     * @return 影响行数
     */
    int insert(UserAlarm userAlarm);

    /**
     * 修改数据
     *
     * @param userAlarm 实例对象
     * @return 影响行数
     */
    int update(UserAlarm userAlarm);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    int updateByFaultMessageId(@Param("ids") List<Long> ids,
                                   @Param("userId") Long userId,
                                   @Param("userAlarm")UserAlarm userAlarm,
                               @Param("type") Integer type);

    Integer selectNumber(@Param("userId") Long userId, @Param("type") Integer type);

    /**
     * 通过故障ID查询对应的人
     * @param alarmId
     * @return
     */
    List<UserAlarm> selectByAlarmId(@Param("alarmId") Long alarmId);
}