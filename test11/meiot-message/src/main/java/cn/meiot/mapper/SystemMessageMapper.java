package cn.meiot.mapper;

import cn.meiot.entity.SystemMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 系统消息 Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-12
 */
public interface SystemMessageMapper extends BaseMapper<SystemMessage> {

    void deleteByBulletinMapper(@Param("idList") Collection<? extends Serializable> idList);

    List<SystemMessage> selectNewsMsg(@Param("userId") Long userId,
                                      @Param("total") Integer total);

    List<SystemMessage> selectNewsMsgEnterprise(@Param("userId") Long userId,
                                      @Param("total") Integer total,
                                      @Param("projectId") Integer projectId);

    /**
     * 查询用户未读消息数
     * @param userId
     * @return
     */
    Integer selectUnreadTotal(Long userId);
}
