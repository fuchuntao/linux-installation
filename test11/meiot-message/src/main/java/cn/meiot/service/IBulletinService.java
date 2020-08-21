package cn.meiot.service;

import cn.meiot.entity.Bulletin;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuyou
 * @since 2019-12-17
 */
public interface IBulletinService extends IService<Bulletin> {

    boolean myUpdateById(Bulletin bulletin);

    boolean myRemoveByIds(Collection<? extends Serializable> idList);

    List<Bulletin> findByType(Integer type);

    void updateExpired();
}
