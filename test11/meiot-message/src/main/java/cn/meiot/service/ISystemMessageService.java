package cn.meiot.service;

import cn.meiot.entity.SystemMessage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统消息 服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-12
 */
public interface ISystemMessageService extends IService<SystemMessage> {

    Integer selectUnreadTotal(Long userId);
}
