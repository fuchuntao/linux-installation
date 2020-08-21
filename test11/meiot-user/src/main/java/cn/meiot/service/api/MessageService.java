package cn.meiot.service.api;

import cn.meiot.entity.vo.Result;

import java.util.List;

public interface MessageService {

    /**
     * 获取所有用户
     * @return
     */
    Result findAllUserId(Integer type);

    /**
     * 通过主账号id查询子账号列表
     * @param mainUserId
     * @return
     */
    List<Long> getSubUserIdByMainUserId(Long mainUserId);

    /**
     * 通过角色查询用户id
     * @param roleIds
     * @return
     */
    List<Long> getUserIdsByRoleId(List<Integer> roleIds);
}
