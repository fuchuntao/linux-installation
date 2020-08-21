package cn.meiot.service.impl;

import cn.meiot.entity.TroubleTicketUser;
import cn.meiot.mapper.TroubleTicketUserDao;
import cn.meiot.service.TroubleTicketUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (TroubleTicketUser)表服务实现类
 *
 * @author makejava
 * @since 2020-04-17 17:21:15
 */
@Service("troubleTicketUserService")
public class TroubleTicketUserServiceImpl implements TroubleTicketUserService {
    @Resource
    private TroubleTicketUserDao troubleTicketUserDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public TroubleTicketUser queryById(Long id) {
        return this.troubleTicketUserDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<TroubleTicketUser> queryAllByLimit(int offset, int limit) {
        return this.troubleTicketUserDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param troubleTicketUser 实例对象
     * @return 实例对象
     */
    @Override
    public TroubleTicketUser insert(TroubleTicketUser troubleTicketUser) {
        this.troubleTicketUserDao.insert(troubleTicketUser);
        return troubleTicketUser;
    }

    /**
     * 修改数据
     *
     * @param troubleTicketUser 实例对象
     * @return 实例对象
     */
    @Override
    public TroubleTicketUser update(TroubleTicketUser troubleTicketUser) {
        this.troubleTicketUserDao.update(troubleTicketUser);
        return this.queryById(troubleTicketUser.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.troubleTicketUserDao.deleteById(id) > 0;
    }
}