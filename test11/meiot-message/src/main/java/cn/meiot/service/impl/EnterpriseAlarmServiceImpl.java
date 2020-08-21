package cn.meiot.service.impl;

import cn.meiot.entity.EnterpriseAlarm;
import cn.meiot.entity.vo.ReadNumber;
import cn.meiot.mapper.EnterpriseAlarmDao;
import cn.meiot.service.EnterpriseAlarmService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (EnterpriseAlarm)表服务实现类
 *
 * @author makejava
 * @since 2020-04-20 09:19:04
 */
@Service("enterpriseAlarmService")
public class EnterpriseAlarmServiceImpl implements EnterpriseAlarmService {
    @Resource
    private EnterpriseAlarmDao enterpriseAlarmDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public EnterpriseAlarm queryById(Long id) {
        return this.enterpriseAlarmDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<EnterpriseAlarm> queryAllByLimit(int offset, int limit) {
        return this.enterpriseAlarmDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param enterpriseAlarm 实例对象
     * @return 实例对象
     */
    @Override
    public EnterpriseAlarm insert(EnterpriseAlarm enterpriseAlarm) {
        this.enterpriseAlarmDao.insert(enterpriseAlarm);
        return enterpriseAlarm;
    }

    /**
     * 修改数据
     *
     * @param enterpriseAlarm 实例对象
     * @return 实例对象
     */
    @Override
    public EnterpriseAlarm update(EnterpriseAlarm enterpriseAlarm) {
        this.enterpriseAlarmDao.update(enterpriseAlarm);
        return this.queryById(enterpriseAlarm.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.enterpriseAlarmDao.deleteById(id) > 0;
    }

    @Override
    public boolean updateByFaultMessageId(List<Long> faultIds, Long userId, EnterpriseAlarm enterpriseAlarm, Integer type) {
      return this.enterpriseAlarmDao.updateByFaultMessageId(faultIds,userId,enterpriseAlarm,type) > 0;
    }

    @Override
    public ReadNumber isRead(Long userId, Integer projectId) {
        Integer number1= enterpriseAlarmDao.selectIsReadNumber(userId,projectId,1);
        Integer number2= enterpriseAlarmDao.selectIsReadNumber(userId,projectId,2);
        return new ReadNumber(number1,number2);
    }
}