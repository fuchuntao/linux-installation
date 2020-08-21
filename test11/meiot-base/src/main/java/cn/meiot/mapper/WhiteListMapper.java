package cn.meiot.mapper;

import cn.meiot.entity.WhiteList;
import cn.meiot.entity.vo.WhiteVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wuyou
 * @since 2019-11-21
 */
@Mapper
public interface WhiteListMapper extends BaseMapper<WhiteList> {

    /**
     * 聚合设备查询最大版本号 这就是当前这些设备需要更新的版本
     * @return
     */
    List<WhiteVo> selectUpgradeDevice();
}
