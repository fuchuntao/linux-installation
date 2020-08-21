package cn.meiot.mapper;

import cn.meiot.entity.EquipmentUser;
import cn.meiot.entity.WhiteList;
import cn.meiot.entity.vo.DeviceVersionVo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 设备用户关系表 Mapper 接口
 * </p>
 *
 * @author wuyou
 * @since 2019-11-28
 */
@Mapper
@DS("db_2")
public interface EquipmentUserMapper extends BaseMapper<EquipmentUser> {

    /**
     * 查询Vo
     * @param userId
     * @param projectId
     * @return
     */


    List<DeviceVersionVo> selectDeviceVersionVo(@Param("userId") Long userId,
                                                @Param("projectId")Integer projectId,
                                                @Param("whiteLists")  List<WhiteList> whiteLists);


    DeviceVersionVo selectUpgrade(@Param("userId") Long userId,
                                  @Param("projectId") Integer projectId,
                                  @Param("serialNumber") String serialNumber,
                                  @Param("version") String version);
}
