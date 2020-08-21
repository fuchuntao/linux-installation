package cn.meiot.service;

import cn.meiot.entity.TroubleTicketUser;
import java.util.List;

/**
 * (TroubleTicketUser)表服务接口
 *
 * @author makejava
 * @since 2020-04-17 17:21:15
 */
public interface TroubleTicketUserService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TroubleTicketUser queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<TroubleTicketUser> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param troubleTicketUser 实例对象
     * @return 实例对象
     */
    TroubleTicketUser insert(TroubleTicketUser troubleTicketUser);

    /**
     * 修改数据
     *
     * @param troubleTicketUser 实例对象
     * @return 实例对象
     */
    TroubleTicketUser update(TroubleTicketUser troubleTicketUser);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

}