package cn.meiot.service.impl;

import cn.meiot.entity.Bulletin;
import cn.meiot.entity.SystemMessage;
import cn.meiot.feign.UserFeign;
import cn.meiot.mapper.BulletinMapper;
import cn.meiot.mapper.SystemMessageMapper;
import cn.meiot.service.IBulletinService;
import cn.meiot.utils.CommonUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2019-12-17
 */
@Service
@Slf4j
public class BulletinServiceImpl extends ServiceImpl<BulletinMapper, Bulletin> implements IBulletinService {

    @Autowired
    protected SystemMessageMapper systemMessageMapper;
    @Autowired
    protected UserFeign userFeign;
    @Autowired
    private BulletinMapper bulletinMapper;



    @Override
    @Transactional
    public boolean myRemoveByIds(Collection<? extends Serializable> idList) {
        systemMessageMapper.deleteByBulletinMapper(idList);
        return super.removeByIds(idList);
    }

    public void push(List<Long> ids, Bulletin bulletin){
        for (Long id:ids) {
            SystemMessage systemMessage = SystemMessage.builder().bulletinId(bulletin.getId()).type(0)
                    .createTime(CommonUtil.getDate()).content(bulletin.getTitle()).userId(id)
                    .subtitle("系统公告").extendId(0L).build();
            systemMessageMapper.insert(systemMessage);
        }
    }



    @Transactional
    @Override
    public boolean myUpdateById(Bulletin entity){
        log.info("公告已经删除:{}",entity);
        systemMessageMapper.deleteByBulletinMapper(Arrays.asList(entity.getId()));
        entity.setStatus(0);
        log.info("公告已经删除");
        return super.updateById(entity);
    }

    @Override
    public List<Bulletin> findByType(Integer type) {

        return  bulletinMapper.selectByType(type);
    }

    @Override
    public void updateExpired() {
        bulletinMapper.updateExpired();
    }
}
