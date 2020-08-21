package cn.meiot.mapper;

import cn.meiot.entity.TroubleTicket;
import cn.meiot.entity.TroubleTicketVo;
import cn.meiot.entity.vo.AftersaleVo;
import cn.meiot.entity.vo.StatusVo;
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
 * @since 2020-02-17
 */
@Mapper
public interface TroubleTicketMapper extends BaseMapper<TroubleTicket> {

    void updateStatusByList(@Param("statusVoList") List<StatusVo> statusVoList);

    List<AftersaleVo> selectByUserId(@Param("currentPage") Integer currentPage,
                                     @Param("pageSize") Integer pageSize,
                                     @Param("userId") Long userId);

    Integer selectByUserIdTotal(@Param("userId") Long userId);

    List<AftersaleVo> selectByUserIdAndProjectId(@Param("currentPage") Integer currentPage,
                                                 @Param("pageSize") Integer pageSize,
                                                 @Param("userId") Long userId,
                                                 @Param("projectId") Integer projectId);

    Integer selectByUserIdAndProjectIdTotal(@Param("userId") Long userId,
                                            @Param("projectId") Integer projectId);
}
