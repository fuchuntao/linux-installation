package cn.meiot.service;

import cn.meiot.entity.Bulletin;

/**
 * @Package cn.meiot.service
 * @Description:
 * @author: 武有
 * @date: 2019/12/19 11:46
 * @Copyright: www.spacecg.cn
 */
public interface Push {

    void push(Bulletin bulletin);
}
