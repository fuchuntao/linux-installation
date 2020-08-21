package cn.meiot.mapper;

import cn.meiot.entity.TroubleTicketUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (TroubleTicketUser)表数据库访问层
 *
 * @author makejava
 * @since 2020-04-17 17:21:14
 */
@Mapper
public interface TroubleTicketUserDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TroubleTicketUser queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<TroubleTicketUser> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param troubleTicketUser 实例对象
     * @return 对象列表
     */
    List<TroubleTicketUser> queryAll(TroubleTicketUser troubleTicketUser);

    /**
     * 新增数据
     *
     * @param troubleTicketUser 实例对象
     * @return 影响行数
     */
    int insert(TroubleTicketUser troubleTicketUser);

    /**
     * 修改数据
     *
     * @param troubleTicketUser 实例对象
     * @return 影响行数
     */
    int update(TroubleTicketUser troubleTicketUser);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}