package cn.meiot.dao;

import cn.meiot.entity.db.Power;
import cn.meiot.entity.db.PowerAppUser;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PowerMapper  extends BaseMapper<Power> {
    void insertTableUser(@Param("id") Long id, @Param("powerAppUserList") Set<PowerAppUser> powerAppUserList, @Param("serialNumber") String serialNumber, @Param("table") String table);

    Set<PowerAppUser> queryPowerUser(@Param("id") Long id);

    /**
     * 删除
     * @param id
     */
    void deleteByTableUser(@Param("id") Long id, @Param("table") String table, @Param("status") Integer status);

    /**
     * 根据主账号查询
     * @param userId
     * @return
     */
    List<Map> query(@Param("switchSn")String switchSn);

    /**
     *
     * @param serialNumber
     * @param id
     * @return
     */
    List<Map> queryBySerialNumberAndId(@Param("serialNumber") String serialNumber, @Param("id") Integer id, @Param("table") String table);

    /**
     * 查找该设备是否存在
     * @param switchSn
     * @param id
     * @return
     */
    Integer queryBySwitchSnAndId(@Param("switchSn") String switchSn, @Param("id") Integer id, @Param("table") String table);


}
