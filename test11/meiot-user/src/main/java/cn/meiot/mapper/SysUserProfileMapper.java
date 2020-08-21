package cn.meiot.mapper;

import cn.meiot.entity.SysUserProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-28
 */
@Mapper
public interface SysUserProfileMapper extends BaseMapper<SysUserProfile> {

    /**
     * 根据用户id查询记录数
     * @param userId
     * @return
     */
    Integer selectCountById(@Param("userId") Long userId);
}
