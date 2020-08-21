package cn.meiot.mapper;

import cn.meiot.entity.Firmware;
import cn.meiot.entity.vo.FirmwareFileVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wuyou
 * @since 2019-11-13
 */
@Mapper
public interface FirmwareMapper extends BaseMapper<Firmware> {

    /**
     * 通过版本号，区域获取文件路径
     * @param version
     * @param region
     * @return
     */
    FirmwareFileVo selectFirmwareUrl(@Param("version") String version,
                                     @Param("region") Integer region);

    /**
     * 查询预约列表
     * @return
     */
    List<Firmware> selectReservationVersion(String currentTime);
}
