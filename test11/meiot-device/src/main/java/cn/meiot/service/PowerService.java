package cn.meiot.service;

import cn.meiot.entity.db.Power;
import cn.meiot.entity.vo.Result;

import java.util.List;
import java.util.Map;

public interface PowerService {
    /**
     * 新增
     * @param power
     * @return
     */
    Result insert(Power power);

    /**
     * 修改
     * @param power
     * @return
     */
    Result update(Power power);

    /**
     * 删除
     * @param power
     * @return
     */
    Result delete(Power power);

    /**
     *
     * @param power
     * @return
     */
    Result query(String switchSn,Long userId);

    /**
     *
     */
    void queryAdminUser(String powerAppUserList, Long userId);

    /**
     * 通过
     * @param id
     * @param userId
     * @return
     */
    List<Map> queryById(Integer id, Long userId, String serialNumber, String table);

    /**
     * 开启或关闭功率
     * @param power
     * @return
     */
    Result isSwitch(Power power);
}
