package cn.meiot.mapper;

import cn.meiot.entity.EnterpriseAlarm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (EnterpriseAlarm)表数据库访问层
 *
 * @author makejava
 * @since 2020-04-20 09:19:03
 */
@Mapper
public interface EnterpriseAlarmDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    EnterpriseAlarm queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<EnterpriseAlarm> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param enterpriseAlarm 实例对象
     * @return 对象列表
     */
    List<EnterpriseAlarm> queryAll(EnterpriseAlarm enterpriseAlarm);

    /**
     * 新增数据
     *
     * @param enterpriseAlarm 实例对象
     * @return 影响行数
     */
    int insert(EnterpriseAlarm enterpriseAlarm);

    /**
     * 修改数据
     *
     * @param enterpriseAlarm 实例对象
     * @return 影响行数
     */
    int update(EnterpriseAlarm enterpriseAlarm);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    List<EnterpriseAlarm> selectByAlarmId(Long id);

    Integer updateByFaultMessageId(@Param("ids") List<Long> ids,
                                   @Param("userId") Long userId,
                                   @Param("userAlarm")EnterpriseAlarm userAlarm,
                                   @Param("type") Integer type);

    Integer selectIsReadNumber(@Param("userId") Long userId,
                               @Param("projectId") Integer projectId,
                               @Param("type") Integer type);
}