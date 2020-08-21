package cn.meiot.service;

import cn.meiot.entity.SystemMessage;

import java.util.List;

/**
 * @Package cn.meiot.service
 * @Description:新版本app个人消息
 * @author: 武有
 * @date: 2020/2/13 18:20
 * @Copyright: www.spacecg.cn
 */


public interface PersonalMsgService {

    /**
     * 个人app首页动态信息轮播
     * @param userId
     * @return
     */
    List<SystemMessage> getNewsMsg(Long userId);

    /**
     * 企业app首页动态信息轮播
     * @param userId
     * @return
     */
    List<SystemMessage> getNewsMsgEnterprise(Long userId,Integer projectId);


    /**
     * 获取用户未读消息数
     * @param userId
     * @return
     */
    Integer getUnreadTotal(Long userId);
}
