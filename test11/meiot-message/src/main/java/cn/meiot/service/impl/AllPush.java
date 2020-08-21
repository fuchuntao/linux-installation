package cn.meiot.service.impl;

import cn.meiot.entity.Bulletin;
import cn.meiot.service.Push;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @Package cn.meiot.service.impl
 * @Description:公告推送 全部推送实现
 * @author: 武有
 * @date: 2019/12/19 11:53
 * @Copyright: www.spacecg.cn
 */
@Component
public class AllPush extends BulletinServiceImpl implements Push {

    @Override
    public void push(Bulletin bulletin) {
        List<Long> ids = userFeign.getUserByType(null);
        super.push(ids,bulletin);
    }


}
