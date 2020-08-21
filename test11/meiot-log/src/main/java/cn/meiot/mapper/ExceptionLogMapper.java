package cn.meiot.mapper;

import cn.meiot.entity.ExceptionLog;
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
 * @since 2019-10-12
 */
@Mapper
public interface ExceptionLogMapper extends BaseMapper<ExceptionLog> {

    /**
     * 根据主用户ID查询日志列表
     * @param currentPage
     * @param pageSize
     * @param startTime
     * @param endTime
     * @param account
     * @param userId
     * @return
     */
    List<ExceptionLog> selectExceptionLogListByMainUserId(@Param("currentPage") Integer currentPage,
                                                          @Param("pageSize")Integer pageSize,
                                                          @Param("startTime")String startTime,
                                                          @Param("endTime")String endTime,
                                                          @Param("account")String account,
                                                          @Param("userId")Long userId);

    /**
     * 根据主用户ID查询日志列表total
     * @param startTime
     * @param endTime
     * @param account
     * @param userId
     * @return
     */
    Integer selectExceptionLogListByMainUserIdTotal(@Param("startTime") String startTime,
                                                    @Param("endTime") String endTime,
                                                    @Param("account") String account,
                                                    @Param("userId") Long userId);

    List<ExceptionLog> selectExceptionLogListByMainUserIdAdmin(@Param("currentPage") Integer currentPage,
                                                          @Param("pageSize")Integer pageSize,
                                                          @Param("startTime")String startTime,
                                                          @Param("endTime")String endTime,
                                                          @Param("account")String account,
                                                          @Param("type")Integer type);
    Integer selectExceptionLogListByMainUserIdTotalAdmin(@Param("startTime") String startTime,
                                                    @Param("endTime") String endTime,
                                                    @Param("account") String account,
                                                    @Param("type") Integer type);
}
