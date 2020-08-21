package cn.meiot.service.impl;

import cn.meiot.entity.SystemMessage;
import cn.meiot.mapper.SystemMessageMapper;
import cn.meiot.service.PersonalMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Package cn.meiot.service.impl
 * @Description:
 * @author: 武有
 * @date: 2020/2/13 18:25
 * @Copyright: www.spacecg.cn
 */

@Service
public class PersonalMsgServiceImpl implements PersonalMsgService {
    @Autowired
    private SystemMessageMapper systemMessageMapper;

    @Override
    public List<SystemMessage> getNewsMsg(Long userId) {
        return systemMessageMapper.selectNewsMsg(userId,5);
    }

    @Override
    public Integer getUnreadTotal(Long userId) {
     return  systemMessageMapper.selectUnreadTotal(userId);
    }

    @Override
    public List<SystemMessage> getNewsMsgEnterprise(Long userId,Integer projectId) {
        return systemMessageMapper.selectNewsMsgEnterprise(userId,5,projectId);
    }
}
