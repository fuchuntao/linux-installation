package cn.meiot.service.impl;

import cn.meiot.entity.UserAlarm;
import cn.meiot.entity.vo.ReadNumber;
import cn.meiot.mapper.UserAlarmDao;
import cn.meiot.service.UserAlarmService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (UserAlarm)表服务实现类
 *
 * @author makejava
 * @since 2020-04-15 10:05:03
 */
@Service("userAlarmService")
public class UserAlarmServiceImpl implements UserAlarmService {
    @Resource
    private UserAlarmDao userAlarmDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public UserAlarm queryById(Long id) {
        return this.userAlarmDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<UserAlarm> queryAllByLimit(int offset, int limit) {
        return this.userAlarmDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param userAlarm 实例对象
     * @return 实例对象
     */
    @Override
    public UserAlarm insert(UserAlarm userAlarm) {
        this.userAlarmDao.insert(userAlarm);
        return userAlarm;
    }

    /**
     * 修改数据
     *
     * @param userAlarm 实例对象
     * @return 实例对象
     */
    @Override
    public UserAlarm update(UserAlarm userAlarm) {
        this.userAlarmDao.update(userAlarm);
        return this.queryById(userAlarm.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.userAlarmDao.deleteById(id) > 0;
    }

    @Override
    public boolean updateByFaultMessageId(List<Long> ids,Long userId,UserAlarm userAlarm,Integer type) {
        return this.userAlarmDao.updateByFaultMessageId(ids,userId,userAlarm,type) > 0;
    }

    @Override
    public ReadNumber isRead(Long userId) {
        Integer alarm= userAlarmDao.selectNumber(userId,1);
       Integer earlyWarning= userAlarmDao.selectNumber(userId,2);
        return new ReadNumber(alarm,earlyWarning);
    }
}