package cn.meiot.service;

import cn.meiot.entity.EnterpriseAlarm;
import cn.meiot.entity.vo.ReadNumber;

import java.util.List;

/**
 * (EnterpriseAlarm)表服务接口
 *
 * @author makejava
 * @since 2020-04-20 09:19:04
 */
public interface EnterpriseAlarmService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    EnterpriseAlarm queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<EnterpriseAlarm> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param enterpriseAlarm 实例对象
     * @return 实例对象
     */
    EnterpriseAlarm insert(EnterpriseAlarm enterpriseAlarm);

    /**
     * 修改数据
     *
     * @param enterpriseAlarm 实例对象
     * @return 实例对象
     */
    EnterpriseAlarm update(EnterpriseAlarm enterpriseAlarm);

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
    boolean updateByFaultMessageId(List<Long> faultIds, Long userId, EnterpriseAlarm enterpriseAlarm, Integer type);

    ReadNumber isRead(Long userId,Integer projectId);
}