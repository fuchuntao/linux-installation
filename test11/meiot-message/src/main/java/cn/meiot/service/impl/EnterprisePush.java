package cn.meiot.service.impl;

import cn.meiot.entity.Bulletin;
import cn.meiot.service.Push;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Package cn.meiot.service.impl
 * @Description:公告推送 目标为企业的推送实现类
 * @author: 武有
 * @date: 2019/12/19 11:51
 * @Copyright: www.spacecg.cn
 */
@Component
public class EnterprisePush extends BulletinServiceImpl implements Push {
    @Override
    public void push(Bulletin bulletin) {
        List<Long> ids = userFeign.getUserByType(bulletin.getPushAims());
        super.push(ids,bulletin);
    }
}