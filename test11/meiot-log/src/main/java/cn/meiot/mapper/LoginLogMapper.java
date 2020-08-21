package cn.meiot.mapper;

import cn.meiot.entity.LoginLog;
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
 * @since 2019-10-14
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    List<LoginLog> selectLoginLogLogListByMainUserId(@Param("currentPage") Integer currentPage,
                                                     @Param("pageSize")Integer pageSize,
                                                     @Param("startTime")String startTime,
                                                     @Param("endTime")String endTime,
                                                     @Param("account")String account,
                                                     @Param("userId")Long userId);

    Integer selectLoginLogLogListByMainUserIdTotal(@Param("startTime")String startTime,
                                                   @Param("endTime")String endTime,
                                                   @Param("account")String account,
                                                   @Param("userId")Long userId);

    List<LoginLog> selectLoginLogListAdmin(@Param("currentPage") Integer currentPage,
                                           @Param("pageSize")Integer pageSize,
                                           @Param("startTime")String startTime,
                                           @Param("endTime")String endTime,
                                           @Param("account")String account,
                                           @Param("type")Integer type);

    Integer selectLoginLogListAdminTotal(@Param("startTime")String startTime,
                                         @Param("endTime")String endTime,
                                         @Param("account")String account,
                                         @Param("type")Integer type);
}
