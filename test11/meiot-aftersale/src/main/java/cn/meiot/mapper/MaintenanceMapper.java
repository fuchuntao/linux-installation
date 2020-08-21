package cn.meiot.mapper;

import cn.meiot.entity.Maintenance;
import cn.meiot.entity.vo.StatisticsVo;
import cn.meiot.entity.vo.StatusVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-28
 */
@Mapper
public interface MaintenanceMapper extends BaseMapper<Maintenance> {

    /**
     * 分页查询售后列表
     * @param paramMap
     * @return
     */
    List<Maintenance> selectAfterSaleByPage(Map<String, Object> paramMap);

    /**
     * 查询total
     * @param paramMap
     * @return
     */
    Integer selecTotal(Map<String, Object> paramMap);

    /**
     * 批量修改状态 也可单个
     * @param statusVoList
     * @return
     */
    Integer updateStatusByList(@Param("statusVoList") List<StatusVo> statusVoList);

    /**
     * 根据IDC查询报修记录
     * @param id
     * @param userId
     * @return
     */
    Maintenance selectAfterSaleById(@Param("id") Long id, @Param("userId") Long userId);


    /**
     * 获取报修状态的数据
     * @return
     */
        List<StatisticsVo> getStatistics(@Param("serialNumber") String serialNumber);
}
