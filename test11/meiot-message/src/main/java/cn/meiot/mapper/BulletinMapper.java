package cn.meiot.mapper;

import cn.meiot.entity.Bulletin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wuyou
 * @since 2019-12-17
 */
public interface BulletinMapper extends BaseMapper<Bulletin> {

    List<Bulletin> selectByType(Integer type);

    void updateExpired();
}
