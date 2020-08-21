package cn.meiot.service.impl;

import cn.meiot.entity.SystemMessage;
import cn.meiot.mapper.SystemMessageMapper;
import cn.meiot.service.ISystemMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统消息 服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-12
 */
@Service
public class SystemMessageServiceImpl extends ServiceImpl<SystemMessageMapper, SystemMessage> implements ISystemMessageService {

    @Autowired
    private SystemMessageMapper systemMessageMapper;
    @Override
    public Integer selectUnreadTotal(Long userId) {
        return systemMessageMapper.selectUnreadTotal(userId);
    }
}
